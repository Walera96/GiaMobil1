package com.spbutu.gia.core.infrastructure.excel;

import com.spbutu.gia.core.application.dto.VedomostDto;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сервис генерации Excel-ведомости (.xls) через Apache POI HSSF.
 */
@Service
public class VedomostExcelService {

    private static final Logger log = LoggerFactory.getLogger(VedomostExcelService.class);

    public byte[] generateExcel(VedomostDto vedomost) {
        try (HSSFWorkbook wb = new HSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            HSSFSheet sheet = wb.createSheet("Ведомость");

            // === ПАРАМЕТРЫ СТРАНИЦЫ ===
            sheet.setMargin(PageMargin.TOP, 1.3 / 2.54);
            sheet.setMargin(PageMargin.BOTTOM, 1.2 / 2.54);
            sheet.setMargin(PageMargin.LEFT, 1.9 / 2.54);
            sheet.setMargin(PageMargin.RIGHT, 1.0 / 2.54);

            HSSFPrintSetup ps = sheet.getPrintSetup();
            ps.setLandscape(false);
            ps.setScale((short) 85);
            ps.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
            ps.setHeaderMargin(1.3 / 2.54);
            ps.setFooterMargin(1.3 / 2.54);
            ps.setFitWidth((short) 1);
            ps.setFitHeight((short) 1);
            sheet.setFitToPage(true);

            // === ШИРИНЫ КОЛОНОК (8-колоночная сетка) ===
            // A(2см) B(2см) C(2см) D(3см) E(3см) F(3см) G(2см) H(2см)
            sheet.setColumnWidth(0, 7 * 256);
            sheet.setColumnWidth(1, 7 * 256);
            sheet.setColumnWidth(2, 7 * 256);
            sheet.setColumnWidth(3, 11 * 256);
            sheet.setColumnWidth(4, 11 * 256);
            sheet.setColumnWidth(5, 11 * 256);
            sheet.setColumnWidth(6, 7 * 256);
            sheet.setColumnWidth(7, 7 * 256);

            // === СТИЛИ ===
            HSSFCellStyle headerStyleLine0 = createHeaderStyleLine0(wb);
            HSSFCellStyle headerStyleLine1 = createHeaderStyleLine1(wb);
            HSSFCellStyle headerStyleLine2 = createHeaderStyleLine2(wb);
            HSSFCellStyle headerStyleLine3 = createHeaderStyleLine3(wb);
            HSSFCellStyle titleStyle = createTitleStyle(wb);
            HSSFCellStyle metaLabelStyle = createMetaLabelStyle(wb);
            HSSFCellStyle metaValueStyle = createMetaValueStyle(wb);
            HSSFCellStyle groupLabelStyle = createGroupLabelStyle(wb);
            HSSFCellStyle groupValueStyle = createGroupValueStyle(wb);
            HSSFCellStyle tableHeaderStyle = createTableHeaderStyle(wb);
            HSSFCellStyle tableCellStyle = createTableCellStyle(wb);
            HSSFCellStyle tableCellLeftStyle = createTableCellLeftStyle(wb);
            HSSFCellStyle statsLabelStyle = createStatsLabelStyle(wb);
            HSSFCellStyle statsLabelPlainStyle = createStatsLabelPlainStyle(wb);
            HSSFCellStyle statsValueStyle = createStatsValueStyle(wb);
            HSSFCellStyle signLabelStyle = createSignLabelStyle(wb);
            HSSFCellStyle signValueStyle = createSignValueStyle(wb);
            HSSFCellStyle memberTitleStyle = createMemberTitleStyle(wb);

            int currentRow = 0;

            // === ЛОГОТИП (колонки A-B, строки 0-3, размер 2.16×2.16 см) ===
            try {
                ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
                if (logoResource.exists()) {
                    byte[] logoBytes = logoResource.getInputStream().readAllBytes();
                    int logoIdx = wb.addPicture(logoBytes, HSSFWorkbook.PICTURE_TYPE_PNG);
                    HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                    HSSFClientAnchor anchor = new HSSFClientAnchor(
                            50, 30, 0, 0,
                            (short) 0, 0, (short) 1, 3
                    );
                    anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
                    HSSFPicture picture = patriarch.createPicture(anchor, logoIdx);
                    // Логотип 183×183 px @ 96 DPI = 4.84 см; масштаб 0.45 → ~2.18 см
                    picture.resize(0.45);
                }
            } catch (Exception e) {
                log.warn("Не удалось вставить логотип в Excel", e);
            }

            // === ШАПКА (текст справа от логотипа, колонки C-H = 2-7) ===
            final int headerStartCol = 2;
            final int headerEndCol = 7;

            Row row0 = sheet.createRow(currentRow++);
            Cell cell = row0.createCell(headerStartCol);
            cell.setCellValue("частное образовательное учреждение высшего образования");
            cell.setCellStyle(headerStyleLine0);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, headerStartCol, headerEndCol));
            row0.setHeightInPoints(14);

            Row row1 = sheet.createRow(currentRow++);
            cell = row1.createCell(headerStartCol);
            cell.setCellValue("\u00ABСАНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ");
            cell.setCellStyle(headerStyleLine1);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, headerStartCol, headerEndCol));
            row1.setHeightInPoints(18);

            Row row2 = sheet.createRow(currentRow++);
            cell = row2.createCell(headerStartCol);
            cell.setCellValue("ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ\u00BB");
            cell.setCellStyle(headerStyleLine2);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, headerStartCol, headerEndCol));
            row2.setHeightInPoints(18);

            Row row3 = sheet.createRow(currentRow++);
            cell = row3.createCell(headerStartCol);
            cell.setCellValue(vedomost.getInstituteName() != null ? vedomost.getInstituteName() : "Институт управления и информационных технологий");
            cell.setCellStyle(headerStyleLine3);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, headerStartCol, headerEndCol));
            row3.setHeightInPoints(16);

            // === ЗАГОЛОВОК ВЕДОМОСТИ ===
            Row titleRow = sheet.createRow(currentRow++);
            cell = titleRow.createCell(0);
            cell.setCellValue("ВЕДОМОСТЬ ЗАЩИТЫ ВЫПУСКНЫХ КВАЛИФИКАЦИОННЫХ РАБОТ \u2116 " + (vedomost.getDocumentNumber() != null ? vedomost.getDocumentNumber() : ""));
            cell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 0, 4));

            currentRow++; // пустая строка

            // === МЕТАДАННЫЕ ===
            currentRow = addMetaRow(sheet, currentRow, "Учебный год", vedomost.getAcademicYear(), metaLabelStyle, metaValueStyle);
            currentRow = addMetaRow(sheet, currentRow, "Направление",
                    (vedomost.getDirectionCode() != null ? vedomost.getDirectionCode() : "") + " " + (vedomost.getDirectionShort() != null ? vedomost.getDirectionShort() : ""),
                    metaLabelStyle, metaValueStyle);
            currentRow = addMetaRow(sheet, currentRow, "Кафедра", vedomost.getDepartment(), metaLabelStyle, metaValueStyle);
            currentRow = addMetaRow(sheet, currentRow, "Форма ГИА", vedomost.getGiaForm(), metaLabelStyle, metaValueStyle);

            String chairmanFull = "";
            if (vedomost.getChairmanName() != null && !vedomost.getChairmanName().isBlank()) {
                chairmanFull = vedomost.getChairmanName();
                if (vedomost.getChairmanDegree() != null && !vedomost.getChairmanDegree().isBlank()) {
                    chairmanFull += ", " + vedomost.getChairmanDegree();
                }
            }
            currentRow = addMetaRow(sheet, currentRow, "Председатель ГЭК", chairmanFull, metaLabelStyle, metaValueStyle);

            String membersLine = "";
            if (vedomost.getCommitteeMembers() != null && !vedomost.getCommitteeMembers().isEmpty()) {
                membersLine = vedomost.getCommitteeMembers().stream()
                        .map(m -> (m.getFullName() != null ? m.getFullName() : "") + (m.getDegree() != null && !m.getDegree().isBlank() ? ", " + m.getDegree() : ""))
                        .reduce((a, b) -> a + "; " + b).orElse("");
            }
            currentRow = addMetaRow(sheet, currentRow, "Члены ГЭК", membersLine, metaLabelStyle, metaValueStyle);

            currentRow++; // пустая строка

            // === ГРУППА/КУРС/ДАТА ===
            Row groupRow = sheet.createRow(currentRow++);
            addGroupCell(groupRow, 0, "Группа", vedomost.getGroupName(), groupLabelStyle, groupValueStyle);
            addGroupCell(groupRow, 2, "Курс", vedomost.getCourse() != null ? String.valueOf(vedomost.getCourse()) : "", groupLabelStyle, groupValueStyle);
            String dateStr = vedomost.getDate() != null ? vedomost.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
            addGroupCell(groupRow, 4, "Дата", dateStr, groupLabelStyle, groupValueStyle);

            currentRow++; // пустая строка

            // === ТАБЛИЦА ЗАГОЛОВКИ ===
            int tableStartRow = currentRow;
            Row tableHeaderRow1 = sheet.createRow(currentRow++);
            createCell(tableHeaderRow1, 0, "\u2116\nп/п", tableHeaderStyle);
            createCell(tableHeaderRow1, 1, "Ф.И.О. студента", tableHeaderStyle);
            createCell(tableHeaderRow1, 2, "\u2116 зачетной\nкнижки", tableHeaderStyle);
            Cell scoreCell = tableHeaderRow1.createCell(3);
            scoreCell.setCellValue("Оценка");
            scoreCell.setCellStyle(tableHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(tableStartRow, tableStartRow, 3, 4));

            Row tableHeaderRow2 = sheet.createRow(currentRow++);
            createCell(tableHeaderRow2, 0, "", tableHeaderStyle);
            createCell(tableHeaderRow2, 1, "", tableHeaderStyle);
            createCell(tableHeaderRow2, 2, "", tableHeaderStyle);
            createCell(tableHeaderRow2, 3, "баллы", tableHeaderStyle);
            createCell(tableHeaderRow2, 4, "классическая", tableHeaderStyle);

            // Объединение ячеек заголовков
            sheet.addMergedRegion(new CellRangeAddress(tableStartRow, tableStartRow + 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(tableStartRow, tableStartRow + 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(tableStartRow, tableStartRow + 1, 2, 2));

            // === ДАННЫЕ СТУДЕНТОВ ===
            List<VedomostDto.StudentRecord> students = vedomost.getStudents();
            if (students != null) {
                for (VedomostDto.StudentRecord student : students) {
                    Row studentRow = sheet.createRow(currentRow++);
                    createCell(studentRow, 0, student.getSeqNumber() != null ? String.valueOf(student.getSeqNumber()) : "", tableCellStyle);
                    createCell(studentRow, 1, student.getFullName() != null ? student.getFullName() : "", tableCellLeftStyle);
                    createCell(studentRow, 2, student.getRecordBookNumber() != null ? student.getRecordBookNumber() : "", tableCellStyle);
                    createCell(studentRow, 3, student.getScorePoints() != null ? String.valueOf(student.getScorePoints()) : "", tableCellStyle);
                    createCell(studentRow, 4, student.getScoreClassic() != null ? student.getScoreClassic() : "", tableCellStyle);
                }
            }

            // Пустые строки до 25
            int existingRows = students != null ? students.size() : 0;
            for (int i = existingRows; i < 25; i++) {
                Row emptyRow = sheet.createRow(currentRow++);
                createCell(emptyRow, 0, String.valueOf(i + 1), tableCellStyle);
                createCell(emptyRow, 1, "", tableCellLeftStyle);
                createCell(emptyRow, 2, "", tableCellStyle);
                createCell(emptyRow, 3, "", tableCellStyle);
                createCell(emptyRow, 4, "", tableCellStyle);
            }

            currentRow++; // пустая строка

            // === ДИРЕКТОР ===
            Row directorRow = sheet.createRow(currentRow++);
            Cell dirLabel = directorRow.createCell(0);
            dirLabel.setCellValue("Директор института");
            dirLabel.setCellStyle(signLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 0, 1));
            Cell dirValue = directorRow.createCell(2);
            dirValue.setCellValue(vedomost.getDirectorName() != null ? vedomost.getDirectorName() : "");
            dirValue.setCellStyle(signValueStyle);
            sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 2, 4));

            currentRow++; // пустая строка

            // === СТАТИСТИКА (правый блок, колонки 3-4) ===
            currentRow = addStatsRow(sheet, currentRow, "Число студентов, участвовавших в аттестации", vedomost.getTotalStudents(), statsLabelPlainStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "из них получивших:", null, statsLabelStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "\u00ABзачтено\u00BB", vedomost.getCountZachteno(), statsLabelStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "\u00ABне зачтено\u00BB", vedomost.getCountNeZachteno(), statsLabelStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "\u00ABотлично\u00BB", vedomost.getCountOtlichno(), statsLabelStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "\u00ABхорошо\u00BB", vedomost.getCountHorosho(), statsLabelStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "\u00ABудовлетворительно\u00BB", vedomost.getCountUdov(), statsLabelStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "\u00ABнеудовлетворительно\u00BB", vedomost.getCountNeud(), statsLabelStyle, statsValueStyle);
            currentRow = addStatsRow(sheet, currentRow, "Число студентов, не явившихся на ГИА", vedomost.getCountAbsent(), statsLabelPlainStyle, statsValueStyle);

            currentRow++; // пустая строка

            // === ПРЕДСЕДАТЕЛЬ ГЭК ===
            Row chairRow = sheet.createRow(currentRow++);
            Cell chairLabel = chairRow.createCell(0);
            chairLabel.setCellValue("Председатель ГЭК");
            chairLabel.setCellStyle(signLabelStyle);
            sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 0, 1));
            Cell chairValue = chairRow.createCell(2);
            chairValue.setCellValue(vedomost.getChairmanName() != null ? vedomost.getChairmanName() : "");
            chairValue.setCellStyle(signValueStyle);
            sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 2, 4));

            currentRow++; // пустая строка

            // === ЧЛЕНЫ ГЭК ===
            Row membersTitleRow = sheet.createRow(currentRow++);
            Cell membersTitle = membersTitleRow.createCell(0);
            membersTitle.setCellValue("Члены ГЭК:");
            membersTitle.setCellStyle(memberTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 0, 4));

            if (vedomost.getCommitteeMembers() != null) {
                for (VedomostDto.CommitteeMember member : vedomost.getCommitteeMembers()) {
                    Row memberRow = sheet.createRow(currentRow++);
                    Cell mCell = memberRow.createCell(0);
                    String mText = (member.getFullName() != null ? member.getFullName() : "") +
                            (member.getDegree() != null && !member.getDegree().isBlank() ? ", " + member.getDegree() : "");
                    mCell.setCellValue(mText);
                    mCell.setCellStyle(signValueStyle);
                    sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 0, 4));
                }
            }
            int memberLines = vedomost.getCommitteeMembers() != null ? vedomost.getCommitteeMembers().size() : 0;
            for (int i = memberLines; i < 5; i++) {
                Row emptyMemberRow = sheet.createRow(currentRow++);
                Cell emCell = emptyMemberRow.createCell(0);
                emCell.setCellValue("");
                emCell.setCellStyle(signValueStyle);
                sheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow - 1, 0, 4));
            }

            // === ВЫСОТА СТРОК ===
            for (int r = 0; r <= currentRow; r++) {
                Row row = sheet.getRow(r);
                if (row != null) {
                    row.setHeightInPoints(18);
                }
            }

            wb.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Ошибка генерации Excel ведомости", e);
            throw new RuntimeException("Не удалось сгенерировать Excel ведомости", e);
        }
    }

    // === Helpers ===

    private int addMetaRow(Sheet sheet, int rowIdx, String label, String value, CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, 1));

        Cell valueCell = row.createCell(2);
        valueCell.setCellValue(value != null ? value : "");
        valueCell.setCellStyle(valueStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 2, 4));
        return rowIdx + 1;
    }

    private void addGroupCell(Row row, int col, String label, String value, CellStyle labelStyle, CellStyle valueStyle) {
        Cell labelCell = row.createCell(col);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(col + 1);
        valueCell.setCellValue(value != null ? value : "");
        valueCell.setCellStyle(valueStyle);
    }

    private int addStatsRow(Sheet sheet, int rowIdx, String label, Integer value, CellStyle labelStyle, CellStyle valueStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(3);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(4);
        valueCell.setCellValue(value != null ? String.valueOf(value) : "");
        valueCell.setCellStyle(valueStyle);
        return rowIdx + 1;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    // === Стили ===

    private HSSFCellStyle createHeaderStyleLine0(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createHeaderStyleLine1(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createHeaderStyleLine2(HSSFWorkbook wb) {
        return createHeaderStyleLine1(wb);
    }

    private HSSFCellStyle createHeaderStyleLine3(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 13);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createTitleStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createMetaLabelStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createMetaValueStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        font.setItalic(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private HSSFCellStyle createGroupLabelStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createGroupValueStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setItalic(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private HSSFCellStyle createTableHeaderStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private HSSFCellStyle createTableCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    private HSSFCellStyle createTableCellLeftStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = createTableCellStyle(wb);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private HSSFCellStyle createStatsLabelStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 8);
        font.setItalic(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createStatsLabelPlainStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = createStatsLabelStyle(wb);
        HSSFFont font = wb.createFont();
        font.setItalic(false);
        style.setFont(font);
        return style;
    }

    private HSSFCellStyle createStatsValueStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 8);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private HSSFCellStyle createSignLabelStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private HSSFCellStyle createSignValueStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private HSSFCellStyle createMemberTitleStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        HSSFFont font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
