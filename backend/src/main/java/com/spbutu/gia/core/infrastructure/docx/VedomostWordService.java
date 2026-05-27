package com.spbutu.gia.core.infrastructure.docx;

import com.spbutu.gia.core.application.dto.VedomostDto;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сервис генерации Word-ведомости (DOCX) через Apache POI XWPF.
 */
@Service
public class VedomostWordService {

    private static final Logger log = LoggerFactory.getLogger(VedomostWordService.class);

    public byte[] generateWord(VedomostDto vedomost) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            setDocumentMargins(doc, 567, 567, 851, 567); // ~10mm, ~10mm, ~15mm, ~10mm в twips

            // === ЛОГОТИП + ШАПКА (таблица) ===
            XWPFTable headerTable = doc.createTable(1, 2);
            setTableWidth(headerTable, "5000");
            setTableBorders(headerTable, false);

            XWPFTableCell logoCell = headerTable.getRow(0).getCell(0);
            logoCell.setWidth("1500");
            try {
                ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
                if (logoResource.exists()) {
                    try (InputStream is = logoResource.getInputStream()) {
                        XWPFParagraph logoPara = logoCell.getParagraphs().isEmpty() ? logoCell.addParagraph() : logoCell.getParagraphs().get(0);
                        logoPara.setAlignment(ParagraphAlignment.LEFT);
                        XWPFRun logoRun = logoPara.createRun();
                        logoRun.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, "logo.png", Units.toEMU(68), Units.toEMU(68));
                    }
                }
            } catch (Exception e) {
                log.warn("Не удалось вставить логотип в Word", e);
            }

            XWPFTableCell textCell = headerTable.getRow(0).getCell(1);
            textCell.setWidth("3500");
            addParagraph(textCell, "частное образовательное учреждение высшего образования", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, "\u00ABСАНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, "ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ\u00BB", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, vedomost.getInstituteName() != null ? vedomost.getInstituteName() : "Институт управления и информационных технологий", "Times New Roman", 13, true, ParagraphAlignment.CENTER);

            // === ЗАГОЛОВОК ===
            addParagraph(doc, "ВЕДОМОСТЬ ЗАЩИТЫ ВЫПУСКНЫХ КВАЛИФИКАЦИОННЫХ РАБОТ \u2116 " + (vedomost.getDocumentNumber() != null ? vedomost.getDocumentNumber() : ""), "Times New Roman", 12, true, ParagraphAlignment.CENTER);

            // === МЕТАДАННЫЕ ===
            addMetaLine(doc, "Учебный год", vedomost.getAcademicYear());
            addMetaLine(doc, "Направление", (vedomost.getDirectionCode() != null ? vedomost.getDirectionCode() : "") + " " + (vedomost.getDirectionShort() != null ? vedomost.getDirectionShort() : ""));

            String chairmanFull = "";
            if (vedomost.getChairmanName() != null && !vedomost.getChairmanName().isBlank()) {
                chairmanFull = vedomost.getChairmanName();
                if (vedomost.getChairmanDegree() != null && !vedomost.getChairmanDegree().isBlank()) {
                    chairmanFull += ", " + vedomost.getChairmanDegree();
                }
            }

            String membersLine = "";
            if (vedomost.getCommitteeMembers() != null && !vedomost.getCommitteeMembers().isEmpty()) {
                membersLine = vedomost.getCommitteeMembers().stream()
                        .map(m -> (m.getFullName() != null ? m.getFullName() : "") + (m.getDegree() != null && !m.getDegree().isBlank() ? ", " + m.getDegree() : ""))
                        .reduce((a, b) -> a + "; " + b).orElse("");
            }

            // === МЕТАДАННЫЕ (одна таблица 2×4) ===
            XWPFTable metaTable = doc.createTable(2, 4);
            setTableWidth(metaTable, "5000");
            setTableBorders(metaTable, false);

            // Row 0: Кафедра | Форма ГИА | Председатель ГЭК | Члены ГЭК
            setMetaCell(metaTable.getRow(0).getCell(0), "Кафедра", vedomost.getDepartment(), 1800);
            setMetaCell(metaTable.getRow(0).getCell(1), "Форма ГИА", vedomost.getGiaForm(), 1800);
            setMetaCell(metaTable.getRow(0).getCell(2), "Председатель ГЭК", chairmanFull, 1800);
            setMetaCell(metaTable.getRow(0).getCell(3), "Члены ГЭК", membersLine, 1800);

            // Row 1: Группа | (пусто) | Курс | Дата
            String dateStr = vedomost.getDate() != null ? vedomost.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
            setGroupCell(metaTable.getRow(1).getCell(0), "Группа", vedomost.getGroupName(), 1200);
            setGroupCell(metaTable.getRow(1).getCell(1), "", "", 800);  // spacer
            setGroupCell(metaTable.getRow(1).getCell(2), "Курс", vedomost.getCourse() != null ? String.valueOf(vedomost.getCourse()) : "", 800);
            setGroupCell(metaTable.getRow(1).getCell(3), "Дата", dateStr, 1200);

            // === ТАБЛИЦА СТУДЕНТОВ ===
            int[] colWidths = {567, 3686, 2552, 1134, 1701}; // twips
            XWPFTable table = doc.createTable(1, 5);
            setTableWidth(table, "5000");
            DocxTableFixer.fixTableGrid(table, colWidths);
            DocxTableFixer.applyTableBorders(table);

            // Заголовки
            XWPFTableRow headerRow = table.getRow(0);
            setCellText(headerRow.getCell(0), "\u2116\nп/п", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
            setCellText(headerRow.getCell(1), "Ф.И.О. студента", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
            setCellText(headerRow.getCell(2), "\u2116 зачетной\nкнижки", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
            setCellText(headerRow.getCell(3), "Оценка\nбаллы", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
            setCellText(headerRow.getCell(4), "Оценка\nклассическая", "Times New Roman", 11, false, ParagraphAlignment.CENTER);

            // Данные
            List<VedomostDto.StudentRecord> students = vedomost.getStudents();
            if (students != null) {
                for (VedomostDto.StudentRecord student : students) {
                    XWPFTableRow dataRow = table.createRow();
                    setCellText(dataRow.getCell(0), student.getSeqNumber() != null ? String.valueOf(student.getSeqNumber()) : "", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
                    setCellText(dataRow.getCell(1), student.getFullName() != null ? student.getFullName() : "", "Times New Roman", 11, false, ParagraphAlignment.LEFT);
                    setCellText(dataRow.getCell(2), student.getRecordBookNumber() != null ? student.getRecordBookNumber() : "", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
                    setCellText(dataRow.getCell(3), student.getScorePoints() != null ? String.valueOf(student.getScorePoints()) : "", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
                    setCellText(dataRow.getCell(4), student.getScoreClassic() != null ? student.getScoreClassic() : "", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
                }
            }

            // Пустые строки до 25
            int existing = students != null ? students.size() : 0;
            for (int i = existing; i < 25; i++) {
                XWPFTableRow emptyRow = table.createRow();
                setCellText(emptyRow.getCell(0), String.valueOf(i + 1), "Times New Roman", 11, false, ParagraphAlignment.CENTER);
                setCellText(emptyRow.getCell(1), "", "Times New Roman", 11, false, ParagraphAlignment.LEFT);
                setCellText(emptyRow.getCell(2), "", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
                setCellText(emptyRow.getCell(3), "", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
                setCellText(emptyRow.getCell(4), "", "Times New Roman", 11, false, ParagraphAlignment.CENTER);
            }

            // === ДИРЕКТОР ===
            addSignLine(doc, "Директор института", vedomost.getDirectorName());

            // === СТАТИСТИКА ===
            addParagraph(doc, "", "Times New Roman", 8, false, ParagraphAlignment.LEFT);
            addStatLine(doc, "Число студентов, участвовавших в аттестации", vedomost.getTotalStudents());
            addStatLine(doc, "из них получивших:", null);
            addStatLine(doc, "\u00ABзачтено\u00BB", vedomost.getCountZachteno());
            addStatLine(doc, "\u00ABне зачтено\u00BB", vedomost.getCountNeZachteno());
            addStatLine(doc, "\u00ABотлично\u00BB", vedomost.getCountOtlichno());
            addStatLine(doc, "\u00ABхорошо\u00BB", vedomost.getCountHorosho());
            addStatLine(doc, "\u00ABудовлетворительно\u00BB", vedomost.getCountUdov());
            addStatLine(doc, "\u00ABнеудовлетворительно\u00BB", vedomost.getCountNeud());
            addStatLine(doc, "Число студентов, не явившихся на ГИА", vedomost.getCountAbsent());

            // === ПРЕДСЕДАТЕЛЬ ГЭК ===
            addParagraph(doc, "", "Times New Roman", 8, false, ParagraphAlignment.LEFT);
            addSignLine(doc, "Председатель ГЭК", vedomost.getChairmanName());

            // === ЧЛЕНЫ ГЭК ===
            addParagraph(doc, "Члены ГЭК:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);
            if (vedomost.getCommitteeMembers() != null) {
                for (VedomostDto.CommitteeMember member : vedomost.getCommitteeMembers()) {
                    String text = (member.getFullName() != null ? member.getFullName() : "") +
                            (member.getDegree() != null && !member.getDegree().isBlank() ? ", " + member.getDegree() : "");
                    addSignLine(doc, "", text);
                }
            }
            int memberLines = vedomost.getCommitteeMembers() != null ? vedomost.getCommitteeMembers().size() : 0;
            for (int i = memberLines; i < 5; i++) {
                addSignLine(doc, "", "");
            }

            doc.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Ошибка генерации Word ведомости", e);
            throw new RuntimeException("Не удалось сгенерировать Word ведомости", e);
        }
    }

    // === Helpers ===

    private void setDocumentMargins(XWPFDocument doc, int top, int right, int left, int bottom) {
        CTBody body = doc.getDocument().getBody();
        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr sectPr = body.getSectPr();
        if (!sectPr.isSetPgMar()) {
            sectPr.addNewPgMar();
        }
        CTPageMar pgMar = sectPr.getPgMar();
        pgMar.setTop(BigInteger.valueOf(top));
        pgMar.setRight(BigInteger.valueOf(right));
        pgMar.setLeft(BigInteger.valueOf(left));
        pgMar.setBottom(BigInteger.valueOf(bottom));
    }

    private void setTableWidth(XWPFTable table, String width) {
        CTTblPr tblPr = table.getCTTbl().getTblPr() != null ? table.getCTTbl().getTblPr() : table.getCTTbl().addNewTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(new BigInteger(width));
        tblWidth.setType(STTblWidth.PCT);
    }

    private void setTableBorders(XWPFTable table, boolean visible) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }
        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        CTBorder border = CTBorder.Factory.newInstance();
        border.setVal(visible ? STBorder.SINGLE : STBorder.NIL);
        border.setSz(BigInteger.valueOf(visible ? 4 : 0));
        border.setColor("000000");
        borders.setTop(border);
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
    }

    private void addParagraph(XWPFDocument doc, String text, String fontName, int fontSize, boolean bold, ParagraphAlignment align) {
        XWPFParagraph para = doc.createParagraph();
        para.setAlignment(align);
        para.setSpacingAfter(0);
        para.setSpacingBefore(0);
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontFamily(fontName);
        run.setFontSize(fontSize);
        run.setBold(bold);
    }

    private void addParagraph(XWPFTableCell cell, String text, String fontName, int fontSize, boolean bold, ParagraphAlignment align) {
        XWPFParagraph para = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
        para.setAlignment(align);
        para.setSpacingAfter(0);
        para.setSpacingBefore(0);
        // очистить существующие run'ы
        for (int i = para.getRuns().size() - 1; i >= 0; i--) {
            para.removeRun(i);
        }
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontFamily(fontName);
        run.setFontSize(fontSize);
        run.setBold(bold);
    }

    private void addMetaLine(XWPFDocument doc, String label, String value) {
        XWPFTable table = doc.createTable(1, 2);
        setTableWidth(table, "5000");
        setTableBorders(table, false);
        table.getRow(0).getCell(0).setWidth("1800");
        table.getRow(0).getCell(1).setWidth("3200");

        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r1 = p1.createRun();
        r1.setText(label);
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(11);

        XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r2 = p2.createRun();
        r2.setText(value != null ? value : "");
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(11);
        r2.setItalic(true);
        r2.setUnderline(UnderlinePatterns.SINGLE);
    }

    private void setGroupCell(XWPFTableCell cell, String label, String value, int widthTwips) {
        cell.setWidth(String.valueOf(widthTwips));
        XWPFParagraph para = cell.getParagraphs().get(0);
        para.setAlignment(ParagraphAlignment.LEFT);
        for (int i = para.getRuns().size() - 1; i >= 0; i--) {
            para.removeRun(i);
        }
        XWPFRun runLabel = para.createRun();
        runLabel.setText(label + " ");
        runLabel.setFontFamily("Times New Roman");
        runLabel.setFontSize(12);
        runLabel.setBold(true);

        XWPFRun runValue = para.createRun();
        runValue.setText(value != null ? value : "");
        runValue.setFontFamily("Times New Roman");
        runValue.setFontSize(12);
        runValue.setBold(true);
        runValue.setItalic(true);
        runValue.setUnderline(UnderlinePatterns.SINGLE);
    }

    private void setMetaCell(XWPFTableCell cell, String label, String value, int widthTwips) {
        cell.setWidth(String.valueOf(widthTwips));
        XWPFParagraph para = cell.getParagraphs().get(0);
        para.setAlignment(ParagraphAlignment.LEFT);
        for (int i = para.getRuns().size() - 1; i >= 0; i--) {
            para.removeRun(i);
        }
        XWPFRun runLabel = para.createRun();
        runLabel.setText(label + " ");
        runLabel.setFontFamily("Times New Roman");
        runLabel.setFontSize(11);

        XWPFRun runValue = para.createRun();
        runValue.setText(value != null ? value : "");
        runValue.setFontFamily("Times New Roman");
        runValue.setFontSize(11);
        runValue.setItalic(true);
        runValue.setUnderline(UnderlinePatterns.SINGLE);
    }

    private void setCellText(XWPFTableCell cell, String text, String fontName, int fontSize, boolean bold, ParagraphAlignment align) {
        XWPFParagraph para = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
        para.setAlignment(align);
        para.setSpacingAfter(0);
        para.setSpacingBefore(0);
        for (int i = para.getRuns().size() - 1; i >= 0; i--) {
            para.removeRun(i);
        }
        XWPFRun run = para.createRun();
        run.setText(text != null ? text : "");
        run.setFontFamily(fontName);
        run.setFontSize(fontSize);
        run.setBold(bold);

        // Установить границы ячейки
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();
        CTBorder border = CTBorder.Factory.newInstance();
        border.setVal(STBorder.SINGLE);
        border.setSz(BigInteger.valueOf(4));
        border.setColor("000000");
        borders.setTop(border);
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
    }

    private void addSignLine(XWPFDocument doc, String label, String value) {
        XWPFTable table = doc.createTable(1, 2);
        setTableWidth(table, "5000");
        setTableBorders(table, false);
        table.getRow(0).getCell(0).setWidth("2000");
        table.getRow(0).getCell(1).setWidth("3000");

        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r1 = p1.createRun();
        r1.setText(label);
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(12);
        r1.setBold(true);

        XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r2 = p2.createRun();
        r2.setText(value != null ? value : "");
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(12);
        r2.setUnderline(UnderlinePatterns.SINGLE);
    }

    private void addStatLine(XWPFDocument doc, String label, Integer value) {
        XWPFTable table = doc.createTable(1, 2);
        setTableWidth(table, "5000");
        setTableBorders(table, false);
        table.getRow(0).getCell(0).setWidth("3500");
        table.getRow(0).getCell(1).setWidth("1500");

        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun r1 = p1.createRun();
        r1.setText(label);
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(8);
        boolean plain = label.startsWith("Число студентов") || label.startsWith("из них");
        if (!plain) {
            r1.setItalic(true);
        }

        XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r2 = p2.createRun();
        r2.setText(value != null ? String.valueOf(value) : "");
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(8);
        r2.setUnderline(UnderlinePatterns.SINGLE);
    }
}
