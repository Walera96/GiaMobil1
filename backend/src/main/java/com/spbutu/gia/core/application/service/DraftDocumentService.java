package com.spbutu.gia.core.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spbutu.gia.core.domain.entity.DraftDocument;
import com.spbutu.gia.core.domain.entity.Protocol;
import com.spbutu.gia.core.domain.entity.ProtocolRecord;
import com.spbutu.gia.core.domain.enums.DocumentType;
import com.spbutu.gia.core.domain.enums.DraftStatus;
import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.core.domain.repository.DraftDocumentRepository;
import com.spbutu.gia.core.domain.repository.GekMembershipRepository;
import com.spbutu.gia.core.domain.repository.ProtocolRecordRepository;
import com.spbutu.gia.core.domain.repository.ProtocolRepository;
import com.spbutu.gia.core.infrastructure.docx.DocxGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Сервис черновиков документов.
 * Создание, редактирование, предпросмотр и генерация финальных DOCX.
 */
@Service
@SuppressWarnings("null")
public class DraftDocumentService {

    private static final Logger log = LoggerFactory.getLogger(DraftDocumentService.class);

    private final DraftDocumentRepository draftDocumentRepository;
    private final ProtocolRepository protocolRepository;
    private final ProtocolRecordRepository protocolRecordRepository;
    private final GekMembershipRepository gekMembershipRepository;
    private final AppUserRepository appUserRepository;
    private final DocxGenerationService docxGenerationService;
    private final ProtocolService protocolService;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    public DraftDocumentService(DraftDocumentRepository draftDocumentRepository,
                                ProtocolRepository protocolRepository,
                                ProtocolRecordRepository protocolRecordRepository,
                                GekMembershipRepository gekMembershipRepository,
                                AppUserRepository appUserRepository,
                                DocxGenerationService docxGenerationService,
                                ProtocolService protocolService,
                                TemplateEngine templateEngine,
                                ObjectMapper objectMapper) {
        this.draftDocumentRepository = draftDocumentRepository;
        this.protocolRepository = protocolRepository;
        this.protocolRecordRepository = protocolRecordRepository;
        this.gekMembershipRepository = gekMembershipRepository;
        this.appUserRepository = appUserRepository;
        this.docxGenerationService = docxGenerationService;
        this.protocolService = protocolService;
        this.templateEngine = templateEngine;
        this.objectMapper = objectMapper;
    }

    // === CRUD ===

    @Transactional(readOnly = true)
    public DraftDocument getDraft(UUID id) {
        return draftDocumentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Черновик не найден: " + id));
    }

    @Transactional(readOnly = true)
    public List<DraftDocument> getDraftsByProtocol(UUID protocolId) {
        return draftDocumentRepository.findAllByProtocolId(protocolId);
    }

    @Transactional
    public DraftDocument createDraft(UUID protocolId, DocumentType documentType, String createdBy) {
        Protocol protocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден: " + protocolId));

        Optional<DraftDocument> existing = draftDocumentRepository.findByProtocolIdAndDocumentType(protocolId, documentType);
        if (existing.isPresent()) {
            log.info("Черновик уже существует для протокола {} и типа {}", protocolId, documentType);
            return existing.get();
        }

        Map<String, Object> content = buildInitialContent(protocol, documentType);

        DraftDocument draft = new DraftDocument();
        draft.setProtocolId(protocolId);
        draft.setDocumentType(documentType);
        draft.setContent(serializeContent(content));
        draft.setStatus(DraftStatus.DRAFT);
        draft.setCreatedBy(createdBy);

        return draftDocumentRepository.save(draft);
    }

    @Transactional
    public DraftDocument updateDraft(UUID id, String contentJson) {
        DraftDocument draft = getDraft(id);
        draft.setContent(contentJson);
        return draftDocumentRepository.save(draft);
    }

    @Transactional
    public DraftDocument approveDraft(UUID id) {
        DraftDocument draft = getDraft(id);
        draft.setStatus(DraftStatus.APPROVED);
        return draftDocumentRepository.save(draft);
    }

    // === Preview ===

    @Transactional(readOnly = true)
    public String previewDraft(UUID id) {
        DraftDocument draft = getDraft(id);
        Map<String, Object> content = deserializeContent(draft.getContent());

        Context context = new Context(Locale.forLanguageTag("ru"));
        context.setVariable("draft", draft);
        context.setVariable("content", content);
        context.setVariable("placeholders", content.get("placeholders"));
        context.setVariable("documentType", draft.getDocumentType().name());
        context.setVariable("status", draft.getStatus().name());

        return templateEngine.process("draft-preview", context);
    }

    // === DOCX generation from approved draft ===

    @Transactional(readOnly = true)
    public byte[] generateDocxFromDraft(UUID draftId) {
        DraftDocument draft = getDraft(draftId);

        Map<String, Object> content = deserializeContent(draft.getContent());
        @SuppressWarnings("unchecked")
        Map<String, String> customPlaceholders = (Map<String, String>) content.getOrDefault("placeholders", new HashMap<>());

        Protocol protocol = protocolRepository.findById(draft.getProtocolId())
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден: " + draft.getProtocolId()));

        return switch (draft.getDocumentType()) {
            case FINAL -> {
                List<ProtocolRecord> records = protocolRecordRepository.findAllByProtocolId(protocol.getId());
                var extra = buildGekExtraData(protocol.getMeeting());
                yield docxGenerationService.generateFinalProtocolFromMap(protocol, records, customPlaceholders, extra);
            }
            case SCORESHEET -> {
                UUID meetingId = protocol.getMeeting() != null ? protocol.getMeeting().getId() : null;
                if (meetingId == null) {
                    throw new IllegalStateException("У протокола нет заседания");
                }
                var scoreSheet = protocolService.buildScoreSheet(meetingId);
                var extra = buildGekExtraData(protocol.getMeeting());
                yield docxGenerationService.generateScoreSheetFromMap(scoreSheet, customPlaceholders, extra);
            }
            case INDIVIDUAL -> {
                // Для индивидуального протокола требуется recordId — пока не поддерживаем
                throw new UnsupportedOperationException("Индивидуальные протоколы пока не поддерживаются в черновиках");
            }
        };
    }

    // === Helpers ===

    private Map<String, Object> buildInitialContent(Protocol protocol, DocumentType documentType) {
        Map<String, Object> content = new HashMap<>();
        Map<String, String> placeholders = new HashMap<>();

        switch (documentType) {
            case FINAL -> {
                List<ProtocolRecord> records = protocolRecordRepository.findAllByProtocolId(protocol.getId());
                placeholders.putAll(buildFinalProtocolPlaceholders(protocol, records));
            }
            case SCORESHEET -> {
                UUID meetingId = protocol.getMeeting() != null ? protocol.getMeeting().getId() : null;
                if (meetingId != null) {
                    var scoreSheet = protocolService.buildScoreSheet(meetingId);
                    placeholders.putAll(buildScoreSheetPlaceholders(scoreSheet));
                }
            }
            case INDIVIDUAL -> {
                // Пока не поддерживаем — требуется конкретная запись
                placeholders.put("PROTOCOL_NUMBER", protocol.getProtocolNumber() != null ? protocol.getProtocolNumber() : "—");
            }
        }

        content.put("placeholders", placeholders);
        return content;
    }

    private Map<String, String> buildFinalProtocolPlaceholders(Protocol protocol, List<ProtocolRecord> records) {
        Map<String, String> map = new HashMap<>();
        var meeting = protocol.getMeeting();

        map.put("UNIVERSITY_NAME", "САНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ");
        map.put("PROTOCOL_NUMBER", protocol.getProtocolNumber() != null ? protocol.getProtocolNumber() : "—");

        if (meeting != null && meeting.getMeetingDate() != null) {
            var d = meeting.getMeetingDate();
            map.put("DATE_DAY", String.valueOf(d.getDayOfMonth()));
            map.put("DATE_MONTH", formatMonthRu(d.getMonthValue()));
            map.put("DATE_YEAR", String.valueOf(d.getYear()));
            map.put("DATE", d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            map.put("START_TIME", meeting.getStartTime() != null ? meeting.getStartTime().toString() : "—");
            map.put("END_TIME", meeting.getEndTime() != null ? meeting.getEndTime().toString() : "—");
        } else {
            map.put("DATE_DAY", "—");
            map.put("DATE_MONTH", "—");
            map.put("DATE_YEAR", "—");
            map.put("DATE", "—");
            map.put("START_TIME", "—");
            map.put("END_TIME", "—");
        }

        String directionCode = "—";
        String directionName = "—";
        if (!records.isEmpty()) {
            var s = records.get(0).getStudent();
            if (s != null && s.getGroup() != null && s.getGroup().getDirection() != null) {
                directionCode = s.getGroup().getDirection().getCode();
                directionName = s.getGroup().getDirection().getName();
            }
        }
        map.put("DIRECTION_CODE", directionCode);
        map.put("DIRECTION_NAME", directionName);

        var extra = buildGekExtraData(meeting);
        map.put("GEK_CHAIRMAN", extractString(extra, "chairmanName"));
        map.put("GEK_MEMBERS", extractString(extra, "membersString"));
        map.put("SECRETARY_NAME", extractString(extra, "secretaryName"));

        return map;
    }

    private Map<String, String> buildScoreSheetPlaceholders(com.spbutu.gia.core.application.dto.ScoreSheetDto dto) {
        Map<String, String> map = new HashMap<>();
        map.put("UNIVERSITY_NAME", "САНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ");
        map.put("DIRECTION_CODE", dto.directionCode() != null ? dto.directionCode() : "—");
        map.put("DIRECTION_NAME", dto.directionName() != null ? dto.directionName() : "—");
        map.put("GROUP_NAME", dto.groupName() != null ? dto.groupName() : "—");
        map.put("PROTOCOL_NUMBER", "—");
        map.put("DATE", dto.meetingTitle() != null ? dto.meetingTitle() : "—");

        var stats = dto.stats();
        if (stats != null) {
            map.put("TOTAL_STUDENTS", String.valueOf(stats.totalStudents()));
            map.put("PRESENT_COUNT", String.valueOf(stats.presentCount()));
            map.put("ABSENT_COUNT", String.valueOf(stats.absentCount()));
            map.put("EXCELLENT_COUNT", String.valueOf(stats.excellentCount()));
            map.put("GOOD_COUNT", String.valueOf(stats.goodCount()));
            map.put("SATISFACTORY_COUNT", String.valueOf(stats.satisfactoryCount()));
            map.put("UNSATISFACTORY_COUNT", String.valueOf(stats.unsatisfactoryCount()));
        }

        return map;
    }

    private Map<String, Object> buildGekExtraData(com.spbutu.gia.core.domain.entity.Meeting meeting) {
        Map<String, Object> extra = new HashMap<>();
        if (meeting == null || meeting.getGek() == null) {
            return extra;
        }
        var memberships = gekMembershipRepository.findAllByGekId(meeting.getGek().getId());
        String chairmanName = null;
        java.util.List<String> members = new java.util.ArrayList<>();
        for (var m : memberships) {
            var member = m.getGekMember();
            if (member == null || member.getUser() == null) continue;
            String name = member.getUser().getFullName();
            if (m.getPositionInGek() == com.spbutu.gia.core.domain.enums.GekPosition.CHAIRMAN) {
                chairmanName = name;
            } else {
                members.add(name);
            }
        }
        extra.put("chairmanName", chairmanName);
        extra.put("membersList", members);
        extra.put("membersString", String.join(", ", members));
        var secretaries = appUserRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(com.spbutu.gia.auth.domain.enums.UserRole.GEK_SECRETARY))
                .toList();
        if (!secretaries.isEmpty()) {
            extra.put("secretaryName", secretaries.get(0).getFullName());
        }
        return extra;
    }

    private String extractString(Map<String, Object> data, String key) {
        if (data == null) return "—";
        Object value = data.get(key);
        if (value instanceof String s) return s;
        if (value instanceof List<?> list) {
            return String.join(", ", list.stream().map(Object::toString).toList());
        }
        return value != null ? value.toString() : "—";
    }

    private String formatMonthRu(int month) {
        return switch (month) {
            case 1 -> "января";
            case 2 -> "февраля";
            case 3 -> "марта";
            case 4 -> "апреля";
            case 5 -> "мая";
            case 6 -> "июня";
            case 7 -> "июля";
            case 8 -> "августа";
            case 9 -> "сентября";
            case 10 -> "октября";
            case 11 -> "ноября";
            case 12 -> "декабря";
            default -> "";
        };
    }

    private String serializeContent(Map<String, Object> content) {
        try {
            return objectMapper.writeValueAsString(content);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации контента черновика", e);
        }
    }

    private Map<String, Object> deserializeContent(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Ошибка десериализации контента черновика, возвращаем пустой map");
            return new HashMap<>();
        }
    }
}
