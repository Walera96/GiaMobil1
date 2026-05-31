package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.ProtocolDto;
import com.spbutu.gia.core.application.dto.ProtocolRecordDto;
import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.core.application.dto.ScoreSheetDto;
import com.spbutu.gia.core.application.dto.ScoreSheetRowDto;
import com.spbutu.gia.core.application.dto.ScoreSheetStatsDto;
import com.spbutu.gia.core.domain.entity.Protocol;
import com.spbutu.gia.core.domain.entity.ProtocolRecord;
import com.spbutu.gia.core.domain.enums.ProtocolStatus;
import com.spbutu.gia.core.domain.enums.GekPosition;
import com.spbutu.gia.core.domain.repository.AgendaItemRepository;
import com.spbutu.gia.core.domain.repository.GekMembershipRepository;
import com.spbutu.gia.core.domain.repository.ProtocolRecordRepository;
import com.spbutu.gia.core.domain.repository.ProtocolRepository;
import com.spbutu.gia.core.application.dto.VedomostDto;
import com.spbutu.gia.core.infrastructure.docx.ProtocolDocxService;
import com.spbutu.gia.core.infrastructure.pdf.PdfGenerationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис протоколов заседаний.
 * CRUD + получение записей протокола.
 */
@SuppressWarnings("null")
@Service
public class ProtocolService {

    private final ProtocolRepository protocolRepository;
    private final ProtocolRecordRepository protocolRecordRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final AppUserRepository appUserRepository;
    private final GekMembershipRepository gekMembershipRepository;
    private final PdfGenerationService pdfGenerationService;
    private final ProtocolDocxService protocolDocxService;

    public ProtocolService(ProtocolRepository protocolRepository,
                           ProtocolRecordRepository protocolRecordRepository,
                           AgendaItemRepository agendaItemRepository,
                           AppUserRepository appUserRepository,
                           GekMembershipRepository gekMembershipRepository,
                           PdfGenerationService pdfGenerationService,
                           ProtocolDocxService protocolDocxService) {
        this.protocolRepository = protocolRepository;
        this.protocolRecordRepository = protocolRecordRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.appUserRepository = appUserRepository;
        this.gekMembershipRepository = gekMembershipRepository;
        this.pdfGenerationService = pdfGenerationService;
        this.protocolDocxService = protocolDocxService;
    }

    /**
     * Получает протокол по ID.
     */
    @Transactional(readOnly = true)
    public ProtocolDto getProtocol(UUID protocolId) {
        Protocol protocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден: " + protocolId));
        return toDto(protocol);
    }

    /**
     * Получает протокол по ID заседания.
     */
    @Transactional(readOnly = true)
    public ProtocolDto getProtocolByMeetingId(UUID meetingId) {
        Protocol protocol = protocolRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол для заседания не найден: " + meetingId));
        return toDto(protocol);
    }

    /**
     * Получает все записи протокола.
     */
    @Transactional(readOnly = true)
    public List<ProtocolRecordDto> getProtocolRecords(UUID protocolId) {
        return protocolRecordRepository.findAllByProtocolId(protocolId)
                .stream()
                .map(this::toRecordDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск протоколов по критериям (студент, группа, направление, ФИО).
     */
    @Transactional(readOnly = true)
    public List<ProtocolDto> searchProtocols(UUID studentId, UUID groupId, UUID directionId, String studentName) {
        List<Protocol> protocols;
        if (studentId != null) {
            protocols = protocolRepository.findByStudentId(studentId);
        } else if (groupId != null) {
            protocols = protocolRepository.findByGroupId(groupId);
        } else if (directionId != null) {
            protocols = protocolRepository.findByDirectionId(directionId);
        } else if (studentName != null && !studentName.isBlank()) {
            protocols = protocolRepository.findByStudentNameContaining(studentName);
        } else {
            protocols = protocolRepository.findAll();
        }
        return protocols.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Обновляет статус протокола на "Подписан".
     */
    @Transactional
    public ProtocolDto signProtocol(UUID protocolId) {
        Protocol protocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден: " + protocolId));

        protocol.setStatus(ProtocolStatus.SIGNED);
        Protocol saved = protocolRepository.save(protocol);
        return toDto(saved);
    }

    @Transactional
    public ProtocolDto approveProtocol(UUID protocolId, UUID chairmanId) {
        Protocol protocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден: " + protocolId));
        AppUser chairman = appUserRepository.findById(chairmanId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        protocol.setStatus(ProtocolStatus.APPROVED);
        protocol.setApprovedAt(LocalDateTime.now());
        protocol.setApprovedBy(chairman);
        Protocol saved = protocolRepository.save(protocol);
        return toDto(saved);
    }

    @Transactional
    public void generateProtocolRecords(UUID meetingId) {
        var agendaItems = agendaItemRepository.findAllByMeetingId(meetingId);
        var protocol = protocolRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден"));
        for (var item : agendaItems) {
            var record = protocolRecordRepository.findByProtocolIdAndStudentId(protocol.getId(), item.getStudent().getId())
                    .orElse(new ProtocolRecord());
            record.setProtocol(protocol);
            record.setStudent(item.getStudent());
            var avg = item.getAverageScore();
            if (avg != null) {
                int finalScore = (int) Math.round(avg);
                record.setAverageScore(finalScore);
                record.setFinalScore(finalScore);
                record.setScorePoints(convertToPoints(finalScore));
                record.setIsWithHonors(finalScore == 5);
                record.setDecision("Признать выполнившим и защитившим ВКР с оценкой " + finalScore);
            } else {
                record.setAverageScore(null);
                record.setFinalScore(null);
                record.setScorePoints(null);
                record.setIsWithHonors(null);
                record.setDecision("Признать не явившимся на защиту ВКР");
            }
            record.setIsAbsent(avg == null);
            if (item.getStudent().getGroup() != null && item.getStudent().getGroup().getDirection() != null) {
                record.setQualification("бакалавр по направлению " + item.getStudent().getGroup().getDirection().getName());
            }
            protocolRecordRepository.save(record);
        }
    }

    private Integer convertToPoints(Integer score) {
        return switch (score) {
            case 5 -> 95;
            case 4 -> 82;
            case 3 -> 70;
            case 2 -> 55;
            default -> 0;
        };
    }

    @Transactional(readOnly = true)
    public ScoreSheetDto buildScoreSheet(UUID meetingId) {
        var protocol = protocolRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден"));
        var records = protocolRecordRepository.findAllByProtocolId(protocol.getId());
        int num = 1;
        var rows = new java.util.ArrayList<ScoreSheetRowDto>();
        for (var r : records) {
            var student = r.getStudent();
            String result;
            if (Boolean.TRUE.equals(r.getIsAbsent())) {
                result = "не явился";
            } else if (r.getFinalScore() != null && r.getFinalScore() >= 3) {
                result = "зачтено";
            } else {
                result = "не зачтено";
            }
            String fullName = student != null
                    ? student.getLastName() + " " + student.getFirstName() + " " + (student.getMiddleName() != null ? student.getMiddleName() : "")
                    : "—";
            rows.add(new ScoreSheetRowDto(
                    num++,
                    fullName,
                    student != null ? student.getRecordBookNumber() : null,
                    r.getScorePoints(),
                    r.getFinalScore(),
                    result
            ));
        }
        long present = rows.stream().filter(r -> !"не явился".equals(r.result())).count();
        long absent = rows.stream().filter(r -> "не явился".equals(r.result())).count();
        long excellent = rows.stream().filter(r -> r.finalScore() != null && r.finalScore() == 5).count();
        long good = rows.stream().filter(r -> r.finalScore() != null && r.finalScore() == 4).count();
        long satisfactory = rows.stream().filter(r -> r.finalScore() != null && r.finalScore() == 3).count();
        long unsatisfactory = rows.stream().filter(r -> r.finalScore() != null && r.finalScore() == 2).count();
        long passed = present - unsatisfactory;
        var stats = new ScoreSheetStatsDto(
                (long) rows.size(), present, absent, excellent, good, satisfactory, unsatisfactory, passed, unsatisfactory
        );
        var meeting = protocol.getMeeting();
        String directionCode = null;
        String directionName = null;
        String groupName = null;
        if (!records.isEmpty()) {
            var firstStudent = records.get(0).getStudent();
            if (firstStudent != null && firstStudent.getGroup() != null) {
                groupName = firstStudent.getGroup().getName();
                var direction = firstStudent.getGroup().getDirection();
                if (direction != null) {
                    directionCode = direction.getCode();
                    directionName = direction.getName();
                }
            }
        }
        String meetingTitle = null;
        if (meeting != null) {
            var gekName = meeting.getGek() != null ? meeting.getGek().getName() : null;
            var dateStr = meeting.getMeetingDate() != null ? meeting.getMeetingDate().toLocalDate().toString() : null;
            meetingTitle = (gekName != null ? gekName + ", " : "") + (dateStr != null ? dateStr : "");
        }
        return new ScoreSheetDto(
                meetingId,
                meetingTitle,
                directionCode,
                directionName,
                groupName,
                rows,
                stats
        );
    }

    private ProtocolDto toDto(Protocol protocol) {
        return new ProtocolDto(
                protocol.getId(),
                protocol.getMeeting() != null ? protocol.getMeeting().getId() : null,
                protocol.getProtocolNumber(),
                protocol.getStatus(),
                protocol.getGeneratedAt(),
                protocol.getFilePath(),
                protocol.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public String getStudentNameForRecord(UUID protocolRecordId) {
        var record = protocolRecordRepository.findById(protocolRecordId).orElse(null);
        if (record == null || record.getStudent() == null) return null;
        return record.getStudent().getLastName();
    }

    @Transactional(readOnly = true)
    public byte[] generateIndividualProtocolDocx(UUID protocolRecordId) {
        var record = protocolRecordRepository.findById(protocolRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));
        var extra = buildGekExtraData(record.getProtocol() != null ? record.getProtocol().getMeeting() : null);
        return protocolDocxService.generateIndividualProtocol(record, extra);
    }

    @Transactional(readOnly = true)
    public byte[] generateFinalProtocolDocx(UUID protocolId) {
        var protocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден"));
        var records = protocolRecordRepository.findAllByProtocolId(protocolId);
        var extra = buildGekExtraData(protocol.getMeeting());
        return protocolDocxService.generateFinalProtocol(protocol, records, extra);
    }

    @Transactional(readOnly = true)
    public VedomostDto buildVedomostDto(UUID meetingId) {
        var protocol = protocolRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден"));
        var records = protocolRecordRepository.findAllByProtocolId(protocol.getId());
        var meeting = protocol.getMeeting();

        VedomostDto dto = new VedomostDto();

        dto.setDocumentNumber(protocol.getProtocolNumber());

        if (meeting != null && meeting.getMeetingDate() != null) {
            var date = meeting.getMeetingDate().toLocalDate();
            dto.setDate(date);
            int year = date.getYear();
            int month = date.getMonthValue();
            if (month >= 9) {
                dto.setAcademicYear(year + "-" + (year + 1));
            } else {
                dto.setAcademicYear((year - 1) + "-" + year);
            }
        }

        String directionCode = null;
        String directionName = null;
        String groupName = null;
        String department = null;

        if (!records.isEmpty()) {
            var firstStudent = records.get(0).getStudent();
            if (firstStudent != null) {
                var group = firstStudent.getGroup();
                if (group != null) {
                    groupName = group.getName();
                    dto.setGroupName(groupName);
                    var direction = group.getDirection();
                    if (direction != null) {
                        directionCode = direction.getCode();
                        directionName = direction.getName();
                        dto.setDirectionCode(directionCode);
                        dto.setDirectionName(directionName);
                        dto.setDirectionShort(directionName);
                    }
                }
            }
        }

        if (meeting != null && meeting.getGek() != null) {
            var memberships = gekMembershipRepository.findAllByGekId(meeting.getGek().getId());
            var committeeMembers = new ArrayList<VedomostDto.CommitteeMember>();
            for (var m : memberships) {
                var member = m.getGekMember();
                if (member == null || member.getUser() == null) continue;
                var cm = new VedomostDto.CommitteeMember();
                cm.setFullName(member.getUser().getFullName());
                cm.setDegree(member.getAcademicTitle());
                cm.setPosition(m.getPositionInGek() != null ? m.getPositionInGek().name() : null);
                committeeMembers.add(cm);

                if (m.getPositionInGek() == GekPosition.CHAIRMAN) {
                    dto.setChairmanName(member.getUser().getFullName());
                    dto.setChairmanDegree(member.getAcademicTitle());
                    department = member.getDepartment();
                }
            }
            dto.setCommitteeMembers(committeeMembers);
        }

        dto.setDepartment(department);

        var students = new ArrayList<VedomostDto.StudentRecord>();
        int seq = 1;
        for (var r : records) {
            var student = r.getStudent();
            var sr = new VedomostDto.StudentRecord();
            sr.setSeqNumber(seq++);
            sr.setFullName(student != null
                    ? student.getLastName() + " " + student.getFirstName() + " " + (student.getMiddleName() != null ? student.getMiddleName() : "")
                    : "—");
            sr.setRecordBookNumber(student != null ? student.getRecordBookNumber() : null);
            sr.setScorePoints(r.getScorePoints());

            if (Boolean.TRUE.equals(r.getIsAbsent())) {
                sr.setScoreClassic(null);
            } else if (r.getFinalScore() != null) {
                sr.setScoreClassic(switch (r.getFinalScore()) {
                    case 5 -> "отлично";
                    case 4 -> "хорошо";
                    case 3 -> "удовлетворительно";
                    case 2 -> "неудовлетворительно";
                    default -> String.valueOf(r.getFinalScore());
                });
            } else {
                sr.setScoreClassic(null);
            }
            students.add(sr);
        }
        dto.setStudents(students);
        dto.calculateStatistics();

        return dto;
    }

    @Transactional(readOnly = true)
    public byte[] generateScoreSheetDocx(UUID meetingId) {
        var vedomost = buildVedomostDto(meetingId);
        var extra = buildGekExtraData(protocolRepository.findByMeetingId(meetingId)
                .map(p -> p.getMeeting()).orElse(null));
        return protocolDocxService.generateVedomost(vedomost, extra);
    }

    @Transactional(readOnly = true)
    public byte[] generateIndividualProtocolPdf(UUID protocolRecordId) {
        var record = protocolRecordRepository.findById(protocolRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена"));
        var extra = buildGekExtraData(record.getProtocol() != null ? record.getProtocol().getMeeting() : null);
        return pdfGenerationService.generateIndividualProtocolPdf(record, extra);
    }

    @Transactional(readOnly = true)
    public byte[] generateFinalProtocolPdf(UUID protocolId) {
        var protocol = protocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Протокол не найден"));
        var records = protocolRecordRepository.findAllByProtocolId(protocolId);
        var extra = buildGekExtraData(protocol.getMeeting());
        return pdfGenerationService.generateFinalProtocolPdf(protocol, records, extra);
    }

    @Transactional(readOnly = true)
    public byte[] generateScoreSheetPdf(UUID meetingId) {
        var scoreSheet = buildScoreSheet(meetingId);
        var meeting = protocolRepository.findByMeetingId(meetingId)
                .map(p -> p.getMeeting())
                .orElse(null);
        var extra = buildGekExtraData(meeting);
        return pdfGenerationService.generateScoreSheetPdf(scoreSheet, extra);
    }

    private Map<String, Object> buildGekExtraData(com.spbutu.gia.core.domain.entity.Meeting meeting) {
        Map<String, Object> extra = new java.util.HashMap<>();
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
            if (m.getPositionInGek() == GekPosition.CHAIRMAN) {
                chairmanName = name;
            } else {
                members.add(name);
            }
        }
        extra.put("chairmanName", chairmanName);
        extra.put("membersList", members);
        extra.put("membersString", String.join(", ", members));
        // Secretary not part of GEK membership; try to find any SECRETARY user
        var secretaries = appUserRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(com.spbutu.gia.auth.domain.enums.UserRole.GEK_SECRETARY))
                .toList();
        if (!secretaries.isEmpty()) {
            extra.put("secretaryName", secretaries.get(0).getFullName());
        }
        return extra;
    }

    private ProtocolRecordDto toRecordDto(ProtocolRecord record) {
        var student = record.getStudent();
        var group = student != null ? student.getGroup() : null;
        var direction = group != null ? group.getDirection() : null;
        return new ProtocolRecordDto(
                record.getId(),
                record.getProtocol() != null ? record.getProtocol().getId() : null,
                student != null ? student.getId() : null,
                student != null ? student.getLastName() + " " + student.getFirstName() : null,
                student != null ? student.getRecordBookNumber() : null,
                record.getScorePoints(),
                record.getFinalScore(),
                record.getIsAbsent(),
                record.getQualification(),
                record.getIsWithHonors(),
                record.getDecision(),
                group != null ? group.getName() : null,
                direction != null ? direction.getCode() : null,
                direction != null ? direction.getName() : null,
                record.getCreatedAt()
        );
    }
}
