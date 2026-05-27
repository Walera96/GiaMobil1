package com.spbutu.gia.core.application.service.scos;

import com.spbutu.gia.core.application.dto.scos.*;
import com.spbutu.gia.core.domain.entity.*;
import com.spbutu.gia.core.domain.entity.scos.ScosExportConfig;
import com.spbutu.gia.core.domain.entity.scos.ScosExportLog;
import com.spbutu.gia.core.domain.repository.*;
import com.spbutu.gia.core.domain.repository.scos.ScosExportConfigRepository;
import com.spbutu.gia.core.domain.repository.scos.ScosExportLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class ScosExportService {

    private final StudentRepository studentRepository;
    private final ProtocolRecordRepository protocolRecordRepository;
    private final ScosExportConfigRepository configRepository;
    private final ScosExportLogRepository logRepository;

    public ScosExportService(StudentRepository studentRepository,
                             ProtocolRecordRepository protocolRecordRepository,
                             ScosExportConfigRepository configRepository,
                             ScosExportLogRepository logRepository) {
        this.studentRepository = studentRepository;
        this.protocolRecordRepository = protocolRecordRepository;
        this.configRepository = configRepository;
        this.logRepository = logRepository;
    }

    @Transactional(readOnly = true)
    public ScosExportPackageDto prepareExportData(UUID directionId, String academicYear) {
        List<Student> students = studentRepository.findAll();
        if (directionId != null) {
            students = students.stream()
                    .filter(s -> s.getGroup() != null &&
                            s.getGroup().getDirection() != null &&
                            directionId.equals(s.getGroup().getDirection().getId()))
                    .collect(Collectors.toList());
        }

        List<ScosStudentResultDto> results = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        int awardedCount = 0;
        int notAwardedCount = 0;

        for (Student student : students) {
            ScosStudentResultDto dto = buildStudentResult(student, academicYear, validationErrors);
            if (dto != null) {
                results.add(dto);
                if ("AWARDED".equals(dto.getDecision())) {
                    awardedCount++;
                } else {
                    notAwardedCount++;
                }
            }
        }

        ScosExportPackageDto packageDto = new ScosExportPackageDto();
        packageDto.setPackageId(UUID.randomUUID());
        packageDto.setExportDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        packageDto.setResults(results);
        packageDto.setTotalCount(results.size());
        packageDto.setAwardedCount(awardedCount);
        packageDto.setNotAwardedCount(notAwardedCount);
        packageDto.setValidationErrors(validationErrors);

        return packageDto;
    }

    private ScosStudentResultDto buildStudentResult(Student student, String academicYear, List<String> errors) {
        ScosStudentResultDto dto = new ScosStudentResultDto();
        dto.setStudentId(student.getId().toString());
        dto.setLastName(student.getLastName());
        dto.setFirstName(student.getFirstName());
        dto.setMiddleName(student.getMiddleName());
        dto.setRecordBookNumber(student.getRecordBookNumber());

        if (student.getGroup() != null) {
            dto.setGroupName(student.getGroup().getName());
            if (student.getGroup().getDirection() != null) {
                Direction direction = student.getGroup().getDirection();
                dto.setDirectionCode(direction.getCode());

                // Map to SCOS code if config exists
                configRepository.findByDirectionCodeAndIsActiveTrue(direction.getCode())
                        .ifPresent(config -> {
                            if (config.getScosDirectionCode() != null) {
                                dto.setDirectionCode(config.getScosDirectionCode());
                            }
                        });
            }
        }

        dto.setThesisTopic(student.getThesisTopic());
        dto.setSupervisorName(student.getSupervisorName());

        // Find latest protocol record for this student
        List<ProtocolRecord> records = protocolRecordRepository.findByStudentId(student.getId());
        ProtocolRecord latestRecord = records.stream()
                .max(Comparator.comparing(r -> {
                    Protocol p = r.getProtocol();
                    return p != null && p.getGeneratedAt() != null ? p.getGeneratedAt() : LocalDateTime.MIN;
                }))
                .orElse(null);

        if (latestRecord != null) {
            Integer finalScore = latestRecord.getFinalScore();
            dto.setFinalScore(finalScore);
            dto.setFivePointGrade(finalScore);
            dto.setEctsGrade(mapScoreToEcts(finalScore));
            dto.setDecision(finalScore != null && finalScore >= 3 ? "AWARDED" : "NOT_AWARDED");

            Protocol protocol = latestRecord.getProtocol();
            if (protocol != null) {
                dto.setProtocolNumber(protocol.getProtocolNumber());
                dto.setProtocolDate(protocol.getGeneratedAt() != null
                        ? protocol.getGeneratedAt().toLocalDate().toString()
                        : null);
            }
        } else {
            dto.setDecision("NOT_AWARDED");
            errors.add("Студент " + student.getLastName() + " не имеет записи в протоколе");
        }

        dto.setQualificationAwarded("БАКАЛАВР");

        return dto;
    }

    private String mapScoreToEcts(Integer score) {
        if (score == null) return "F";
        if (score >= 5) return "A";
        if (score >= 4) return "B";
        if (score >= 3) return "C";
        return "F";
    }

    @Transactional(readOnly = true)
    public String generateXml(ScosExportPackageDto packageDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<gia_results_export>\n");
        sb.append("  <metadata>\n");
        sb.append("    <export_date>").append(escapeXml(packageDto.getExportDate())).append("</export_date>\n");
        sb.append("    <institution>ЧОУ ВО СПбУТУиЭ</institution>\n");
        sb.append("    <total_records>").append(packageDto.getTotalCount()).append("</total_records>\n");
        sb.append("  </metadata>\n");
        sb.append("  <results>\n");

        for (ScosStudentResultDto r : packageDto.getResults()) {
            sb.append("    <student_result>\n");
            sb.append("      <student_id>").append(escapeXml(r.getStudentId())).append("</student_id>\n");
            sb.append("      <full_name>").append(escapeXml(r.getLastName() + " " + r.getFirstName() + " " + (r.getMiddleName() != null ? r.getMiddleName() : ""))).append("</full_name>\n");
            sb.append("      <record_book_number>").append(escapeXml(r.getRecordBookNumber())).append("</record_book_number>\n");
            sb.append("      <group_name>").append(escapeXml(r.getGroupName())).append("</group_name>\n");
            sb.append("      <thesis_topic>").append(escapeXml(r.getThesisTopic())).append("</thesis_topic>\n");
            sb.append("      <supervisor>").append(escapeXml(r.getSupervisorName())).append("</supervisor>\n");
            sb.append("      <final_score>").append(r.getFinalScore() != null ? r.getFinalScore() : "").append("</final_score>\n");
            sb.append("      <ects_grade>").append(escapeXml(r.getEctsGrade())).append("</ects_grade>\n");
            sb.append("      <five_point_grade>").append(r.getFivePointGrade() != null ? r.getFivePointGrade() : "").append("</five_point_grade>\n");
            sb.append("      <qualification_awarded>").append(escapeXml(r.getQualificationAwarded())).append("</qualification_awarded>\n");
            sb.append("      <protocol_number>").append(escapeXml(r.getProtocolNumber())).append("</protocol_number>\n");
            sb.append("      <protocol_date>").append(escapeXml(r.getProtocolDate())).append("</protocol_date>\n");
            sb.append("      <decision>").append(escapeXml(r.getDecision())).append("</decision>\n");
            sb.append("    </student_result>\n");
        }

        sb.append("  </results>\n");
        sb.append("</gia_results_export>");
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String generateJson(ScosExportPackageDto packageDto) {
        // Simple JSON generation using Jackson would be better, but for simplicity:
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"exportDate\": \"").append(escapeJson(packageDto.getExportDate())).append("\",\n");
        sb.append("  \"institution\": \"ЧОУ ВО СПбУТУиЭ\",\n");
        sb.append("  \"totalRecords\": ").append(packageDto.getTotalCount()).append(",\n");
        sb.append("  \"results\": [\n");

        List<ScosStudentResultDto> results = packageDto.getResults();
        for (int i = 0; i < results.size(); i++) {
            ScosStudentResultDto r = results.get(i);
            sb.append("    {\n");
            sb.append("      \"studentId\": \"").append(escapeJson(r.getStudentId())).append("\",\n");
            sb.append("      \"fullName\": \"").append(escapeJson(r.getLastName() + " " + r.getFirstName() + " " + (r.getMiddleName() != null ? r.getMiddleName() : ""))).append("\",\n");
            sb.append("      \"recordBookNumber\": \"").append(escapeJson(r.getRecordBookNumber())).append("\",\n");
            sb.append("      \"groupName\": \"").append(escapeJson(r.getGroupName())).append("\",\n");
            sb.append("      \"thesisTopic\": \"").append(escapeJson(r.getThesisTopic())).append("\",\n");
            sb.append("      \"supervisor\": \"").append(escapeJson(r.getSupervisorName())).append("\",\n");
            sb.append("      \"finalScore\": ").append(r.getFinalScore() != null ? r.getFinalScore() : "null").append(",\n");
            sb.append("      \"ectsGrade\": \"").append(escapeJson(r.getEctsGrade())).append("\",\n");
            sb.append("      \"fivePointGrade\": ").append(r.getFivePointGrade() != null ? r.getFivePointGrade() : "null").append(",\n");
            sb.append("      \"qualificationAwarded\": \"").append(escapeJson(r.getQualificationAwarded())).append("\",\n");
            sb.append("      \"protocolNumber\": \"").append(escapeJson(r.getProtocolNumber())).append("\",\n");
            sb.append("      \"protocolDate\": \"").append(escapeJson(r.getProtocolDate())).append("\",\n");
            sb.append("      \"decision\": \"").append(escapeJson(r.getDecision())).append("\"\n");
            sb.append("    }").append(i < results.size() - 1 ? "," : "").append("\n");
        }

        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public List<String> validateExport(ScosExportPackageDto packageDto) {
        List<String> errors = new ArrayList<>();

        for (ScosStudentResultDto r : packageDto.getResults()) {
            if (r.getStudentId() == null || r.getStudentId().isBlank()) {
                errors.add("Отсутствует studentId");
            }
            if (r.getLastName() == null || r.getLastName().isBlank()) {
                errors.add("Отсутствует фамилия для студента " + r.getStudentId());
            }
            if (r.getRecordBookNumber() == null || r.getRecordBookNumber().isBlank()) {
                errors.add("Отсутствует номер зачётной книжки для " + r.getLastName());
            }
            if (r.getEctsGrade() == null || !r.getEctsGrade().matches("[A-F]")) {
                errors.add("Некорректная ECTS оценка для " + r.getLastName() + ": " + r.getEctsGrade());
            }
            if (r.getFivePointGrade() == null || r.getFivePointGrade() < 2 || r.getFivePointGrade() > 5) {
                errors.add("Некорректная 5-балльная оценка для " + r.getLastName());
            }
            if (r.getProtocolNumber() == null || r.getProtocolNumber().isBlank()) {
                errors.add("Отсутствует номер протокола для " + r.getLastName());
            }
            if (r.getDecision() == null || (!r.getDecision().equals("AWARDED") && !r.getDecision().equals("NOT_AWARDED"))) {
                errors.add("Некорректное решение для " + r.getLastName());
            }
        }

        // Check for duplicates
        Set<String> seenIds = new HashSet<>();
        for (ScosStudentResultDto r : packageDto.getResults()) {
            if (!seenIds.add(r.getStudentId())) {
                errors.add("Дубликат studentId: " + r.getStudentId());
            }
        }

        return errors;
    }

    @Transactional
    public ScosExportLog saveExportLog(ScosExportPackageDto packageDto, String format, String content, String createdBy) {
        ScosExportLog log = new ScosExportLog();
        log.setExportDate(LocalDateTime.now());
        log.setFileName("gia_export_" + packageDto.getPackageId() + "." + format);
        log.setRecordCount(packageDto.getTotalCount());
        List<String> errors = validateExport(packageDto);
        log.setStatus(errors.isEmpty() ? ScosExportLog.ExportStatus.SUCCESS : ScosExportLog.ExportStatus.PARTIAL);
        log.setErrorDetails(errors.isEmpty() ? null : String.join("; ", errors));
        log.setFileContent(content);
        log.setCreatedBy(createdBy);
        return logRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ScosExportLogDto> getExportHistory() {
        return logRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toLogDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ScosExportConfigDto> getConfig(String directionCode) {
        return configRepository.findByDirectionCode(directionCode)
                .map(this::toConfigDto);
    }

    @Transactional
    public ScosExportConfigDto saveConfig(ScosExportConfigDto dto) {
        ScosExportConfig config = configRepository.findByDirectionCode(dto.getDirectionCode())
                .orElse(new ScosExportConfig());
        config.setDirectionCode(dto.getDirectionCode());
        config.setScosDirectionCode(dto.getScosDirectionCode());
        config.setScosDirectionName(dto.getScosDirectionName());
        config.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return toConfigDto(configRepository.save(config));
    }

    private ScosExportLogDto toLogDto(ScosExportLog log) {
        ScosExportLogDto dto = new ScosExportLogDto();
        dto.setId(log.getId());
        dto.setExportDate(log.getExportDate());
        dto.setFileName(log.getFileName());
        dto.setRecordCount(log.getRecordCount());
        dto.setStatus(log.getStatus() != null ? log.getStatus().name() : null);
        dto.setErrorDetails(log.getErrorDetails());
        dto.setDirectionCode(log.getDirectionCode());
        dto.setAcademicYear(log.getAcademicYear());
        dto.setCreatedBy(log.getCreatedBy());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    private ScosExportConfigDto toConfigDto(ScosExportConfig config) {
        ScosExportConfigDto dto = new ScosExportConfigDto();
        dto.setId(config.getId());
        dto.setDirectionCode(config.getDirectionCode());
        dto.setScosDirectionCode(config.getScosDirectionCode());
        dto.setScosDirectionName(config.getScosDirectionName());
        dto.setIsActive(config.getIsActive());
        return dto;
    }

    private String escapeXml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
