package com.spbutu.gia.core.infrastructure.docx;

import com.spbutu.gia.core.application.dto.ScoreSheetDto;
import com.spbutu.gia.core.application.dto.ScoreSheetRowDto;
import com.spbutu.gia.core.domain.entity.Protocol;
import com.spbutu.gia.core.domain.entity.ProtocolRecord;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.apache.poi.util.Units;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Сервис генерации DOCX-документов на основе шаблонов с плейсхолдерами {{FIELD_NAME}}.
 * Использует Apache POI (poi-ooxml).
 */
@Service
public class DocxGenerationService {

    private static final Logger log = LoggerFactory.getLogger(DocxGenerationService.class);

    private static final String TEMPLATE_INDIVIDUAL = "templates/docx/individual_protocol_shablon.docx";
    private static final String TEMPLATE_FINAL = "templates/docx/itogovoy_protocol_shablon.docx";
    private static final String TEMPLATE_SCORESHEET = "templates/docx/vedmost_gia_shablon.docx";

    // --- Public API ---

    public byte[] generateIndividualProtocol(ProtocolRecord record, Map<String, Object> data) {
        try (InputStream is = loadTemplate(TEMPLATE_INDIVIDUAL);
             XWPFDocument doc = new XWPFDocument(is)) {

            insertLogoRightAligned(doc);
            Map<String, String> placeholders = buildIndividualProtocolMap(record, data);
            replaceAllPlaceholders(doc, placeholders);
            formatIndividualProtocolHeader(doc);
            setFontFamilyThroughout(doc, "Times New Roman");

            return writeToBytes(doc);
        } catch (Exception e) {
            log.error("Ошибка генерации индивидуального протокола", e);
            throw new RuntimeException("Не удалось сгенерировать индивидуальный протокол", e);
        }
    }

    public byte[] generateFinalProtocol(Protocol protocol, List<ProtocolRecord> records, Map<String, Object> data) {
        try (InputStream is = loadTemplate(TEMPLATE_FINAL);
             XWPFDocument doc = new XWPFDocument(is)) {

            insertLogo(doc);
            // Fill dynamic tables FIRST (before placeholder replacement)
            fillPresentStudentsTable(doc, records);
            fillAbsentStudentsTable(doc, records);
            if (records.stream().noneMatch(r -> Boolean.TRUE.equals(r.getIsAbsent()))) {
                removeAbsentSection(doc);
            }
            fillQualificationTable(doc, records);

            Map<String, String> placeholders = buildFinalProtocolMap(protocol, records, data);
            replaceAllPlaceholders(doc, placeholders);
            formatFinalProtocolHeader(doc);

            setFontFamilyThroughout(doc, "Times New Roman");
            return writeToBytes(doc);
        } catch (Exception e) {
            log.error("Ошибка генерации итогового протокола", e);
            throw new RuntimeException("Не удалось сгенерировать итоговый протокол", e);
        }
    }

    public byte[] generateScoreSheet(ScoreSheetDto dto, Map<String, Object> data) {
        try (InputStream is = loadTemplate(TEMPLATE_SCORESHEET);
             XWPFDocument doc = new XWPFDocument(is)) {

            insertLogo(doc);
            fillScoreSheetTable(doc, dto);

            Map<String, String> placeholders = buildScoreSheetMap(dto, data);
            replaceAllPlaceholders(doc, placeholders);
            setFontFamilyThroughout(doc, "Times New Roman");
            return writeToBytes(doc);
        } catch (Exception e) {
            log.error("Ошибка генерации ведомости", e);
            throw new RuntimeException("Не удалось сгенерировать ведомость", e);
        }
    }

    // --- Public API: generation from custom placeholder maps (for drafts) ---

    public byte[] generateIndividualProtocolFromMap(ProtocolRecord record, Map<String, String> customPlaceholders, Map<String, Object> data) {
        try (InputStream is = loadTemplate(TEMPLATE_INDIVIDUAL);
             XWPFDocument doc = new XWPFDocument(is)) {

            insertLogoRightAligned(doc);
            Map<String, String> placeholders = buildIndividualProtocolMap(record, data);
            if (customPlaceholders != null) {
                placeholders.putAll(customPlaceholders);
            }
            replaceAllPlaceholders(doc, placeholders);
            formatIndividualProtocolHeader(doc);
            setFontFamilyThroughout(doc, "Times New Roman");

            return writeToBytes(doc);
        } catch (Exception e) {
            log.error("Ошибка генерации индивидуального протокола из черновика", e);
            throw new RuntimeException("Не удалось сгенерировать индивидуальный протокол", e);
        }
    }

    public byte[] generateFinalProtocolFromMap(Protocol protocol, List<ProtocolRecord> records, Map<String, String> customPlaceholders, Map<String, Object> data) {
        try (InputStream is = loadTemplate(TEMPLATE_FINAL);
             XWPFDocument doc = new XWPFDocument(is)) {

            insertLogo(doc);
            fillPresentStudentsTable(doc, records);
            fillAbsentStudentsTable(doc, records);
            if (records.stream().noneMatch(r -> Boolean.TRUE.equals(r.getIsAbsent()))) {
                removeAbsentSection(doc);
            }
            fillQualificationTable(doc, records);

            Map<String, String> placeholders = buildFinalProtocolMap(protocol, records, data);
            if (customPlaceholders != null) {
                placeholders.putAll(customPlaceholders);
            }
            replaceAllPlaceholders(doc, placeholders);
            formatFinalProtocolHeader(doc);

            setFontFamilyThroughout(doc, "Times New Roman");
            return writeToBytes(doc);
        } catch (Exception e) {
            log.error("Ошибка генерации итогового протокола из черновика", e);
            throw new RuntimeException("Не удалось сгенерировать итоговый протокол", e);
        }
    }

    public byte[] generateScoreSheetFromMap(ScoreSheetDto dto, Map<String, String> customPlaceholders, Map<String, Object> data) {
        try (InputStream is = loadTemplate(TEMPLATE_SCORESHEET);
             XWPFDocument doc = new XWPFDocument(is)) {

            insertLogo(doc);
            fillScoreSheetTable(doc, dto);

            Map<String, String> placeholders = buildScoreSheetMap(dto, data);
            if (customPlaceholders != null) {
                placeholders.putAll(customPlaceholders);
            }
            replaceAllPlaceholders(doc, placeholders);
            setFontFamilyThroughout(doc, "Times New Roman");
            return writeToBytes(doc);
        } catch (Exception e) {
            log.error("Ошибка генерации ведомости из черновика", e);
            throw new RuntimeException("Не удалось сгенерировать ведомость", e);
        }
    }

    // --- Template loading ---

    private InputStream loadTemplate(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(java.util.Objects.requireNonNull(path));
        if (!resource.exists()) {
            throw new IllegalStateException("Шаблон не найден: " + path);
        }
        return resource.getInputStream();
    }

    private byte[] writeToBytes(XWPFDocument doc) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        return out.toByteArray();
    }

    // --- Logo insertion ---

    private void insertLogo(XWPFDocument doc) {
        insertLogoAligned(doc, ParagraphAlignment.LEFT);
    }

    private void insertLogoRightAligned(XWPFDocument doc) {
        insertLogoAligned(doc, ParagraphAlignment.RIGHT);
    }

    private void insertLogoAligned(XWPFDocument doc, ParagraphAlignment alignment) {
        try {
            ClassPathResource resource = new ClassPathResource("static/images/logo.png");
            if (!resource.exists()) {
                log.warn("Логотип не найден: static/images/logo.png");
                return;
            }

            // Insert logo run at the beginning of the first paragraph
            // to avoid CTBody manipulation that causes XmlValueDisconnectedException
            XWPFParagraph targetPara;
            if (doc.getParagraphs().isEmpty()) {
                targetPara = doc.createParagraph();
            } else {
                targetPara = doc.getParagraphs().get(0);
            }
            targetPara.setAlignment(alignment);

            try (InputStream is = resource.getInputStream()) {
                XWPFRun logoRun = targetPara.insertNewRun(0);
                if (logoRun != null) {
                    logoRun.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, "logo.png",
                            Units.toEMU(45), Units.toEMU(45));
                    logoRun.addBreak();
                }
            }

        } catch (Exception e) {
            log.warn("Не удалось вставить логотип в DOCX", e);
        }
    }

    // --- Placeholder builders ---

    private Map<String, String> buildIndividualProtocolMap(ProtocolRecord record, Map<String, Object> data) {
        Map<String, String> map = new java.util.HashMap<>();
        var student = record.getStudent();
        var protocol = record.getProtocol();
        var meeting = protocol != null ? protocol.getMeeting() : null;

        map.put("UNIVERSITY_NAME", "САНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ");
        map.put("PROTOCOL_NUMBER", protocol != null && protocol.getProtocolNumber() != null ? protocol.getProtocolNumber() : "—");

        if (meeting != null && meeting.getMeetingDate() != null) {
            var d = meeting.getMeetingDate();
            map.put("DATE_DAY", String.valueOf(d.getDayOfMonth()));
            map.put("DATE_MONTH", formatMonthRu(d.getMonthValue()));
            map.put("DATE_YEAR", String.valueOf(d.getYear()));
            map.put("START_TIME", meeting.getStartTime() != null ? meeting.getStartTime().toString() : "—");
            map.put("END_TIME", meeting.getEndTime() != null ? meeting.getEndTime().toString() : "—");
            map.put("DATE", d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } else {
            map.put("DATE_DAY", "—");
            map.put("DATE_MONTH", "—");
            map.put("DATE_YEAR", "—");
            map.put("START_TIME", "—");
            map.put("END_TIME", "—");
            map.put("DATE", "—");
        }

        map.put("STUDENT_FIO", student != null ? fullName(student) : "—");
        map.put("GROUP_NAME", student != null && student.getGroup() != null ? student.getGroup().getName() : "—");
        map.put("DIRECTION_CODE", student != null && student.getGroup() != null && student.getGroup().getDirection() != null
                ? student.getGroup().getDirection().getCode() : "—");
        map.put("DIRECTION_NAME", student != null && student.getGroup() != null && student.getGroup().getDirection() != null
                ? student.getGroup().getDirection().getName() : "—");
        map.put("PROFILE", student != null && student.getGroup() != null && student.getGroup().getDirection() != null
                ? (student.getGroup().getDirection().getProfile() != null ? student.getGroup().getDirection().getProfile() : "—") : "—");
        map.put("THESIS_TOPIC", student != null && student.getThesisTopic() != null ? student.getThesisTopic() : "—");
        map.put("SUPERVISOR", student != null && student.getSupervisorName() != null ? student.getSupervisorName() : "—");

        map.put("GEK_CHAIRMAN", extractString(data, "chairmanName"));
        map.put("GEK_MEMBERS", extractString(data, "membersString"));
        map.put("SECRETARY_NAME", extractString(data, "secretaryName"));

        Integer finalScore = record.getFinalScore();
        map.put("FINAL_GRADE", finalScore != null ? scoreToString(finalScore) : "—");
        map.put("QUALIFICATION", record.getQualification() != null ? record.getQualification() : "—");
        map.put("HONORS_STATUS", Boolean.TRUE.equals(record.getIsWithHonors()) ? "с отличием" : "без отличия");

        // Empty questions placeholders
        for (int i = 1; i <= 5; i++) {
            map.put("QUESTION_" + i, "—");
            map.put("ANSWER_" + i, "—");
        }

        return map;
    }

    private Map<String, String> buildFinalProtocolMap(Protocol protocol, List<ProtocolRecord> records, Map<String, Object> data) {
        Map<String, String> map = new java.util.HashMap<>();
        var meeting = protocol != null ? protocol.getMeeting() : null;

        map.put("UNIVERSITY_NAME", "САНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ");
        map.put("PROTOCOL_NUMBER", protocol != null && protocol.getProtocolNumber() != null ? protocol.getProtocolNumber() : "—");

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
        map.put("PROFILE", (!records.isEmpty() && records.get(0).getStudent() != null && records.get(0).getStudent().getGroup() != null && records.get(0).getStudent().getGroup().getDirection() != null)
                ? records.get(0).getStudent().getGroup().getDirection().getProfile() != null ? records.get(0).getStudent().getGroup().getDirection().getProfile() : "—" : "—");

        // BUG-1 FIX: {{QUALIFICATION}} placeholder mapping
        map.put("QUALIFICATION", directionCode + " " + directionName);

        map.put("GEK_CHAIRMAN", extractString(data, "chairmanName"));
        map.put("GEK_MEMBERS", extractString(data, "membersString"));
        map.put("SECRETARY_NAME", extractString(data, "secretaryName"));

        // Table placeholders are handled dynamically by fill*Table methods before replacement

        return map;
    }

    private Map<String, String> buildScoreSheetMap(ScoreSheetDto dto, Map<String, Object> data) {
        Map<String, String> map = new java.util.HashMap<>();
        map.put("UNIVERSITY_NAME", "САНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ");
        map.put("DIRECTION_CODE", dto.directionCode() != null ? dto.directionCode() : "—");
        map.put("DIRECTION_NAME", dto.directionName() != null ? dto.directionName() : "—");
        map.put("GROUP_NAME", dto.groupName() != null ? dto.groupName() : "—");
        map.put("PROTOCOL_NUMBER", "—");
        map.put("DATE", dto.meetingTitle() != null ? dto.meetingTitle() : "—");
        map.put("GEK_CHAIRMAN", extractString(data, "chairmanName"));
        map.put("GEK_MEMBERS", extractString(data, "membersString"));

        var stats = dto.stats();
        if (stats != null) {
            map.put("TOTAL_STUDENTS", String.valueOf(stats.totalStudents()));
            map.put("PRESENT_COUNT", String.valueOf(stats.presentCount()));
            map.put("ABSENT_COUNT", String.valueOf(stats.absentCount()));
            map.put("EXCELLENT_COUNT", String.valueOf(stats.excellentCount()));
            map.put("GOOD_COUNT", String.valueOf(stats.goodCount()));
            map.put("SATISFACTORY_COUNT", String.valueOf(stats.satisfactoryCount()));
            map.put("UNSATISFACTORY_COUNT", String.valueOf(stats.unsatisfactoryCount()));
        } else {
            map.put("TOTAL_STUDENTS", "0");
            map.put("PRESENT_COUNT", "0");
            map.put("ABSENT_COUNT", "0");
            map.put("EXCELLENT_COUNT", "0");
            map.put("GOOD_COUNT", "0");
            map.put("SATISFACTORY_COUNT", "0");
            map.put("UNSATISFACTORY_COUNT", "0");
        }

        // Table row placeholders are handled dynamically by fillScoreSheetTable before replacement

        return map;
    }

    // --- Placeholder replacement ---

    private void replaceAllPlaceholders(XWPFDocument doc, Map<String, String> placeholders) {
        // Replace in paragraphs
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            replaceInParagraph(paragraph, placeholders);
        }
        // Replace in tables
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replaceInParagraph(paragraph, placeholders);
                    }
                }
            }
        }
    }

    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        String fullText = getParagraphText(paragraph);
        if (fullText == null || !fullText.contains("{{")) {
            return;
        }

        String replaced = fullText;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            replaced = replaced.replace("{{" + entry.getKey() + "}}", entry.getValue() != null ? entry.getValue() : "");
        }

        if (!replaced.equals(fullText)) {
            // Clear existing runs and set new text in the first run
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs.isEmpty()) {
                XWPFRun run = paragraph.createRun();
                run.setText(replaced, 0);
            } else {
                // Keep the first run's formatting, remove others
                XWPFRun firstRun = runs.get(0);
                firstRun.setText(replaced, 0);
                for (int i = runs.size() - 1; i > 0; i--) {
                    paragraph.removeRun(i);
                }
            }
        }
    }

    private String getParagraphText(XWPFParagraph paragraph) {
        StringBuilder sb = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                sb.append(text);
            }
        }
        return sb.toString();
    }

    // --- Dynamic table filling ---

    private void fillScoreSheetTable(XWPFDocument doc, ScoreSheetDto dto) {
        List<ScoreSheetRowDto> rows = dto.rows();
        if (rows == null || rows.isEmpty()) return;

        for (XWPFTable table : doc.getTables()) {
            if (table.getRows().size() < 2) continue;
            // Find data row (second row with placeholders)
            XWPFTableRow templateRow = null;
            for (int i = 0; i < table.getRows().size(); i++) {
                String rowText = getRowText(table.getRow(i));
                if (rowText.contains("{{STUDENT_FIO}}") || rowText.contains("{{STUDENT_NUMBER}}")) {
                    templateRow = table.getRow(i);
                    break;
                }
            }
            if (templateRow == null) continue;

            for (int i = 0; i < rows.size(); i++) {
                ScoreSheetRowDto row = rows.get(i);
                XWPFTableRow newRow;
                if (i == 0) {
                    newRow = templateRow;
                } else {
                    newRow = copyRow(table, templateRow);
                }
                setCellText(newRow.getCell(0), String.valueOf(row.number()));
                setCellText(newRow.getCell(1), row.studentFullName() != null ? row.studentFullName() : "—");
                setCellText(newRow.getCell(2), row.recordBookNumber() != null ? row.recordBookNumber() : "—");
                setCellText(newRow.getCell(3), row.scorePoints() != null ? String.valueOf(row.scorePoints()) : "—");
                setCellText(newRow.getCell(4), formatClassicGrade(row));
            }
            // Apply borders to the filled table
            DocxTableFixer.applyTableBorders(table);
            break; // Only process first matching table
        }
    }

    private void fillPresentStudentsTable(XWPFDocument doc, List<ProtocolRecord> records) {
        var present = records.stream().filter(r -> !Boolean.TRUE.equals(r.getIsAbsent()) && r.getFinalScore() != null).toList();
        XWPFTable filledTable = fillGenericTable(doc, present, "{{PRESENT_FIO}}",
            (row, record, idx) -> {
                setCellText(row.getCell(0), String.valueOf(idx + 1));
                setCellText(row.getCell(1), record.getStudent() != null ? fullName(record.getStudent()) : "—");
                setCellText(row.getCell(2), scoreToString(record.getFinalScore()));
                setCellText(row.getCell(3), record.getQualification() != null ? record.getQualification() : "—");
            });
        if (filledTable != null) {
            DocxTableFixer.applyTableBorders(filledTable);
        }
    }

    private void fillAbsentStudentsTable(XWPFDocument doc, List<ProtocolRecord> records) {
        var absent = records.stream().filter(r -> Boolean.TRUE.equals(r.getIsAbsent())).toList();
        XWPFTable filledTable = fillGenericTable(doc, absent, "{{ABSENT_FIO}}",
            (row, record, idx) -> {
                setCellText(row.getCell(0), String.valueOf(idx + 1));
                setCellText(row.getCell(1), record.getStudent() != null ? fullName(record.getStudent()) : "—");
            });
        if (filledTable != null) {
            DocxTableFixer.applyTableBorders(filledTable);
        }
    }

    private void fillQualificationTable(XWPFDocument doc, List<ProtocolRecord> records) {
        var present = records.stream().filter(r -> !Boolean.TRUE.equals(r.getIsAbsent())).toList();
        XWPFTable filledTable = fillGenericTable(doc, present, "{{QUAL_FIO}}",
            (row, record, idx) -> {
                setCellText(row.getCell(0), String.valueOf(idx + 1));
                setCellText(row.getCell(1), record.getStudent() != null ? fullName(record.getStudent()) : "—");
                setCellText(row.getCell(2), record.getQualification() != null ? record.getQualification() : "—");
            });
        if (filledTable != null) {
            DocxTableFixer.applyTableBorders(filledTable);
        }
    }

    @FunctionalInterface
    interface RowFiller {
        void fill(XWPFTableRow row, ProtocolRecord record, int index);
    }

    private XWPFTable fillGenericTable(XWPFDocument doc, List<ProtocolRecord> records, String marker, RowFiller filler) {
        if (records.isEmpty()) return null;
        for (XWPFTable table : doc.getTables()) {
            XWPFTableRow templateRow = null;
            for (int i = 0; i < table.getRows().size(); i++) {
                if (getRowText(table.getRow(i)).contains(marker)) {
                    templateRow = table.getRow(i);
                    break;
                }
            }
            if (templateRow == null) continue;

            for (int i = 0; i < records.size(); i++) {
                XWPFTableRow newRow = (i == 0) ? templateRow : copyRow(table, templateRow);
                filler.fill(newRow, records.get(i), i);
            }
            return table;
        }
        return null;
    }

    // --- Row utilities ---

    private String getRowText(XWPFTableRow row) {
        StringBuilder sb = new StringBuilder();
        for (XWPFTableCell cell : row.getTableCells()) {
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                sb.append(getParagraphText(paragraph));
            }
        }
        return sb.toString();
    }

    private XWPFTableRow copyRow(XWPFTable table, XWPFTableRow sourceRow) {
        XWPFTableRow newRow = table.createRow();
        int cellCount = sourceRow.getTableCells().size();
        while (newRow.getTableCells().size() < cellCount) {
            newRow.createCell();
        }
        for (int i = 0; i < cellCount; i++) {
            XWPFTableCell sourceCell = sourceRow.getCell(i);
            XWPFTableCell newCell = newRow.getCell(i);
            // Copy cell properties
            if (sourceCell.getCTTc() != null && sourceCell.getCTTc().getTcPr() != null) {
                newCell.getCTTc().setTcPr(sourceCell.getCTTc().getTcPr());
            }
        }
        return newRow;
    }

    private void setCellText(XWPFTableCell cell, String text) {
        if (cell == null) return;
        XWPFParagraph paragraph;
        if (cell.getParagraphs().isEmpty()) {
            paragraph = cell.addParagraph();
        } else {
            paragraph = cell.getParagraphs().get(0);
        }
        // Remove existing runs one by one (getRuns() may return unmodifiable list)
        while (paragraph.getRuns().size() > 0) {
            paragraph.removeRun(0);
        }
        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "—");
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
    }

    // --- Font ---

    private void setFontFamilyThroughout(XWPFDocument doc, String fontFamily) {
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                run.setFontFamily(fontFamily);
            }
        }
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        for (XWPFRun run : paragraph.getRuns()) {
                            run.setFontFamily(fontFamily);
                        }
                    }
                }
            }
        }
    }

    // --- Helpers ---

    private String fullName(com.spbutu.gia.core.domain.entity.Student student) {
        if (student == null) return "—";
        StringBuilder sb = new StringBuilder();
        sb.append(student.getLastName()).append(" ").append(student.getFirstName());
        if (student.getMiddleName() != null && !student.getMiddleName().isBlank()) {
            sb.append(" ").append(student.getMiddleName());
        }
        return sb.toString();
    }

    private String scoreToString(int score) {
        return switch (score) {
            case 5 -> "отлично";
            case 4 -> "хорошо";
            case 3 -> "удовлетворительно";
            case 2 -> "неудовлетворительно";
            default -> String.valueOf(score);
        };
    }

    private String formatClassicGrade(ScoreSheetRowDto row) {
        if (row.finalScore() != null) {
            return scoreToString(row.finalScore());
        }
        if ("не явился".equals(row.result())) {
            return "не явился";
        }
        return "—";
    }

    private void removeAbsentSection(XWPFDocument doc) {
        // Find and remove the Section II paragraph (absent students heading)
        XWPFParagraph sectionPara = null;
        for (XWPFParagraph para : doc.getParagraphs()) {
            String text = getParagraphText(para);
            if (text != null && text.contains("II.") &&
                (text.contains("не явились") || text.contains("неявившихся") || text.contains("Отсутствовавшие"))) {
                sectionPara = para;
                break;
            }
        }

        if (sectionPara != null) {
            int pos = doc.getPosOfParagraph(sectionPara);
            if (pos >= 0) {
                doc.removeBodyElement(pos);
            }
        }

        // Find and remove the absent students table
        XWPFTable absentTable = null;
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                String rowText = getRowText(row);
                if (rowText.contains("{{ABSENT_FIO}}") || rowText.contains("{{ABSENT_NUMBER}}")) {
                    absentTable = table;
                    break;
                }
            }
            if (absentTable != null) break;
        }

        if (absentTable != null) {
            int pos = doc.getPosOfTable(absentTable);
            if (pos >= 0) {
                doc.removeBodyElement(pos);
            }
        }
    }

    private void formatFinalProtocolHeader(XWPFDocument doc) {
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            String text = getParagraphText(paragraph);
            if (text == null || text.isBlank()) continue;

            // 1. "частное образовательное учреждение высшего образования" → 12pt, center
            if (text.contains("частное образовательное учреждение высшего образования")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(false);
                }
            }
            // 2. University name → 12pt bold, center
            else if (text.contains("САНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ") || text.contains("ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(true);
                }
            }
            // 3. "ИТОГОВЫЙ ПРОТОКОЛ" → 12pt bold, center
            else if (text.contains("ИТОГОВЫЙ ПРОТОКОЛ")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(true);
                }
            }
            // 4. Commission description lines → 12pt bold, center
            else if (text.contains("заседания государственной экзаменационной комиссии")
                    || text.contains("по проведению государственной итоговой аттестации")
                    || text.contains("в форме защиты выпускной квалификационной работы")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(true);
                }
            }
            // 5. Direction, profile, date/time → justify (both), 12pt
            else if (text.contains("Направление подготовки")
                    || text.contains("Направленность (профиль)")
                    || (text.contains("г.") && text.contains("с ") && text.contains("до "))) {
                paragraph.setAlignment(ParagraphAlignment.BOTH);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                }
            }
        }
    }

    private void formatIndividualProtocolHeader(XWPFDocument doc) {
        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            String text = getParagraphText(paragraph);
            if (text == null || text.isBlank()) continue;

            // 1. "частное образовательное учреждение высшего образования" → 12pt, center
            if (text.contains("частное образовательное учреждение высшего образования")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(false);
                }
            }
            // 2. University name → 12pt bold, center
            else if (text.contains("САНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ") || text.contains("ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(true);
                }
            }
            // 3. "ПРОТОКОЛ №" → 12pt bold, center
            else if (text.contains("ПРОТОКОЛ №")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(true);
                }
            }
            // 4. Commission description lines → 12pt bold, center
            else if (text.contains("заседания государственной экзаменационной комиссии")
                    || text.contains("по проведению государственной итоговой аттестации")
                    || text.contains("в форме защиты выпускной квалификационной работы")) {
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                    run.setBold(true);
                }
            }
            // 5. Direction, profile, date/time → justify (both), 12pt
            else if (text.contains("Направление подготовки")
                    || text.contains("Направленность (профиль)")
                    || (text.contains("г.") && text.contains("с ") && text.contains("до "))) {
                paragraph.setAlignment(ParagraphAlignment.BOTH);
                for (XWPFRun run : paragraph.getRuns()) {
                    run.setFontSize(12);
                }
            }
        }
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

    @SuppressWarnings("unchecked")
    private String extractString(Map<String, Object> data, String key) {
        if (data == null) return "—";
        Object value = data.get(key);
        if (value instanceof String s) return s;
        if (value instanceof java.util.List<?> list) {
            if (key.equals("membersList")) {
                return String.join(", ", (List<String>) list);
            }
        }
        return value != null ? value.toString() : "—";
    }
}
