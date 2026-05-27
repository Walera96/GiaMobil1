package com.spbutu.gia.core.infrastructure.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.spbutu.gia.core.application.dto.ScoreSheetDto;
import com.spbutu.gia.core.domain.entity.Protocol;
import com.spbutu.gia.core.domain.entity.ProtocolRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PdfGenerationService {

    private static final Logger log = LoggerFactory.getLogger(PdfGenerationService.class);
    private final TemplateEngine templateEngine;

    public PdfGenerationService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateIndividualProtocolPdf(ProtocolRecord record, Map<String, Object> extraData) {
        Context ctx = new Context();
        Map<String, Object> data = buildIndividualProtocolData(record);
        if (extraData != null) data.putAll(extraData);
        ctx.setVariables(data);
        return render("pdf/individual-protocol", ctx);
    }

    public byte[] generateFinalProtocolPdf(Protocol protocol, List<ProtocolRecord> records, Map<String, Object> extraData) {
        Context ctx = new Context();
        Map<String, Object> data = buildFinalProtocolData(protocol, records);
        if (extraData != null) data.putAll(extraData);
        ctx.setVariables(data);
        return render("pdf/final-protocol", ctx);
    }

    public byte[] generateScoreSheetPdf(ScoreSheetDto dto, Map<String, Object> extraData) {
        Context ctx = new Context();
        Map<String, Object> data = buildScoreSheetData(dto);
        if (extraData != null) data.putAll(extraData);
        ctx.setVariables(data);
        return render("pdf/scoresheet", ctx);
    }

    private byte[] render(String templateName, Context ctx) {
        try {
            String html = templateEngine.process(templateName, ctx);
            ITextRenderer renderer = new ITextRenderer();
            registerFont(renderer);
            String baseUrl = ResourceUtils.getURL("classpath:static/").toString();
            renderer.setDocumentFromString(html, baseUrl);
            renderer.layout();
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                renderer.createPDF(os);
                return os.toByteArray();
            }
        } catch (DocumentException | IOException e) {
            log.error("PDF generation failed for template: {}", templateName, e);
            throw new RuntimeException("Ошибка генерации PDF: " + e.getMessage(), e);
        }
    }

    private void registerFont(ITextRenderer renderer) {
        String[] fontFiles = {
            "fonts/TimesNewRoman.ttf",
            "fonts/TimesNewRoman-Bold.ttf",
            "fonts/TimesNewRoman-Italic.ttf",
            "fonts/TimesNewRoman-BoldItalic.ttf"
        };
        for (String fontFile : fontFiles) {
            try {
                String f = Objects.requireNonNull(fontFile);
                ClassPathResource fontResource = new ClassPathResource(f);
                if (fontResource.exists()) {
                    String fontUrl = fontResource.getURL().toString();
                    renderer.getFontResolver().addFont(fontUrl, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    log.debug("Registered font: {}", fontFile);
                }
            } catch (Exception e) {
                log.warn("Failed to register font {}: {}", fontFile, e.getMessage());
            }
        }
    }

    private Map<String, Object> buildIndividualProtocolData(ProtocolRecord record) {
        Map<String, Object> map = new HashMap<>();
        var student = record.getStudent();
        var protocol = record.getProtocol();
        var meeting = protocol != null ? protocol.getMeeting() : null;

        map.put("protocolNumber", protocol != null ? protocol.getProtocolNumber() : "—");
        String meetingDateStr = formatMeetingDate(meeting);
        map.put("meetingDate", meetingDateStr);
        map.put("meetingDay", extractDay(meetingDateStr));
        map.put("meetingMonth", extractMonthName(meetingDateStr));
        map.put("meetingYear", extractYear(meetingDateStr));
        map.put("startTime", formatTime(meeting != null ? meeting.getStartTime() : null));
        map.put("endTime", formatTime(meeting != null ? meeting.getEndTime() : null));
        map.put("thesisTopic", student != null ? student.getThesisTopic() : "—");
        String studentFullName = formatStudentName(student);
        map.put("studentName", studentFullName);
        map.put("studentFullName", studentFullName);
        map.put("directionCode", student != null && student.getGroup() != null && student.getGroup().getDirection() != null
                ? student.getGroup().getDirection().getCode() : "—");
        map.put("directionName", student != null && student.getGroup() != null && student.getGroup().getDirection() != null
                ? student.getGroup().getDirection().getName() : "—");
        map.put("profileName", student != null && student.getGroup() != null && student.getGroup().getDirection() != null
                ? student.getGroup().getDirection().getName() : "—");
        map.put("supervisorName", student != null ? student.getSupervisorName() : "—");
        map.put("finalScore", record.getFinalScore());
        map.put("qualification", record.getQualification());
        map.put("isWithHonors", record.getIsWithHonors());
        map.put("decision", record.getDecision());
        // Extra data defaults
        map.put("chairmanName", "—");
        map.put("membersString", "—");
        map.put("secretaryName", "—");
        map.put("membersList", List.of());
        return map;
    }

    private Map<String, Object> buildFinalProtocolData(Protocol protocol, List<ProtocolRecord> records) {
        Map<String, Object> map = new HashMap<>();
        var meeting = protocol != null ? protocol.getMeeting() : null;

        map.put("protocolNumber", protocol != null ? protocol.getProtocolNumber() : "—");
        String meetingDateStr = formatMeetingDate(meeting);
        map.put("meetingDate", meetingDateStr);
        map.put("meetingDay", extractDay(meetingDateStr));
        map.put("meetingMonth", extractMonthName(meetingDateStr));
        map.put("meetingYear", extractYear(meetingDateStr));
        map.put("startTime", formatTime(meeting != null ? meeting.getStartTime() : null));
        map.put("endTime", formatTime(meeting != null ? meeting.getEndTime() : null));

        var firstRecord = records.stream().findFirst().orElse(null);
        var firstStudent = firstRecord != null ? firstRecord.getStudent() : null;
        var direction = firstStudent != null && firstStudent.getGroup() != null ? firstStudent.getGroup().getDirection() : null;
        map.put("directionCode", direction != null ? direction.getCode() : "—");
        map.put("directionName", direction != null ? direction.getName() : "—");
        map.put("profileName", direction != null ? direction.getName() : "—");

        map.put("presentStudents", buildStudentRowMaps(records.stream().filter(r -> !Boolean.TRUE.equals(r.getIsAbsent())).toList()));
        map.put("absentStudents", buildStudentRowMaps(records.stream().filter(r -> Boolean.TRUE.equals(r.getIsAbsent())).toList()));
        map.put("allStudents", buildStudentRowMaps(records));
        // Extra data defaults
        map.put("chairmanName", "—");
        map.put("membersString", "—");
        map.put("secretaryName", "—");
        map.put("membersList", List.of());
        return map;
    }

    private List<Map<String, Object>> buildStudentRowMaps(List<ProtocolRecord> records) {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (var r : records) {
            Map<String, Object> m = new HashMap<>();
            var s = r.getStudent();
            m.put("studentFullName", s != null
                    ? s.getLastName() + " " + s.getFirstName() + " " + (s.getMiddleName() != null ? s.getMiddleName() : "")
                    : "—");
            m.put("finalScore", r.getFinalScore());
            m.put("qualification", r.getQualification());
            list.add(m);
        }
        return list;
    }

    private Map<String, Object> buildScoreSheetData(ScoreSheetDto dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", dto.meetingTitle());
        map.put("protocolNumber", dto.meetingTitle());
        map.put("directionCode", dto.directionCode());
        map.put("directionName", dto.directionName());
        map.put("groupName", dto.groupName());
        map.put("rows", dto.rows());
        map.put("stats", dto.stats());
        map.put("academicYear", "2025–2026");
        map.put("department", "Информационных технологий и математики");
        map.put("giaForm", "Выполнение и защита ВКР");
        map.put("course", 4);
        map.put("meetingDate", dto.meetingTitle());
        // Extra data defaults
        map.put("chairmanName", "—");
        map.put("membersString", "—");
        map.put("secretaryName", "—");
        map.put("directorName", "—");
        map.put("instituteName", "Институт управления и информационных технологий");
        return map;
    }

    private String formatStudentName(com.spbutu.gia.core.domain.entity.Student student) {
        if (student == null) return "—";
        String name = student.getLastName() + " " + student.getFirstName();
        if (student.getMiddleName() != null && !student.getMiddleName().isBlank()) {
            name += " " + student.getMiddleName();
        }
        return name;
    }

    private String formatMeetingDate(com.spbutu.gia.core.domain.entity.Meeting meeting) {
        if (meeting == null || meeting.getMeetingDate() == null) return "—";
        return meeting.getMeetingDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String formatTime(java.time.LocalTime time) {
        if (time == null) return "—";
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String extractDay(String dateStr) {
        if (dateStr == null || dateStr.length() < 2 || dateStr.equals("—")) return "  ";
        return dateStr.substring(0, 2);
    }

    private String extractMonthName(String dateStr) {
        if (dateStr == null || dateStr.length() < 5 || dateStr.equals("—")) return "          ";
        String monthNum = dateStr.substring(3, 5);
        return switch (monthNum) {
            case "01" -> "января";
            case "02" -> "февраля";
            case "03" -> "марта";
            case "04" -> "апреля";
            case "05" -> "мая";
            case "06" -> "июня";
            case "07" -> "июля";
            case "08" -> "августа";
            case "09" -> "сентября";
            case "10" -> "октября";
            case "11" -> "ноября";
            case "12" -> "декабря";
            default -> "          ";
        };
    }

    private String extractYear(String dateStr) {
        if (dateStr == null || dateStr.length() < 10 || dateStr.equals("—")) return "20  ";
        return dateStr.substring(6, 10);
    }
}
