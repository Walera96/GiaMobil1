package com.spbutu.gia.core.infrastructure.docx;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * Утилита для исправления таблиц DOCX: добавление tblGrid, границ, стилизации ячеек.
 * Критично для совместимости с MS Word.
 */
@Component
public class DocxTableFixer {

    /**
     * После создания таблицы через XWPFTable добавляет tblGrid с корректными ширинами колонок.
     *
     * @param table        таблица
     * @param columnWidths ширины колонок в twips (1/20 пункта)
     */
    public static void fixTableGrid(XWPFTable table, int[] columnWidths) {
        CTTbl ctTbl = table.getCTTbl();

        // Удаляем существующий tblGrid если есть
        if (ctTbl.getTblGrid() != null) {
            ctTbl.setTblGrid(null);
        }

        // Создаём новый tblGrid
        CTTblGrid tblGrid = ctTbl.addNewTblGrid();

        // Добавляем колонки с ширинами
        for (int width : columnWidths) {
            CTTblGridCol gridCol = tblGrid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(width));
        }

        // Устанавливаем ширину таблицы
        CTTblPr tblPr = ctTbl.getTblPr() != null ? ctTbl.getTblPr() : ctTbl.addNewTblPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(5000));
    }

    /**
     * Применяет границы ко всей таблице (тонкие, чёрные, 0.5pt).
     */
    public static void applyTableBorders(XWPFTable table) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }

        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();

        setBorder(borders.addNewTop(), "000000", 4);
        setBorder(borders.addNewLeft(), "000000", 4);
        setBorder(borders.addNewBottom(), "000000", 4);
        setBorder(borders.addNewRight(), "000000", 4);
        setBorder(borders.addNewInsideH(), "000000", 4);
        setBorder(borders.addNewInsideV(), "000000", 4);
    }

    private static void setBorder(CTBorder border, String color, int size) {
        border.setVal(STBorder.SINGLE);
        border.setColor(color);
        border.setSz(BigInteger.valueOf(size));
        border.setSpace(BigInteger.valueOf(0));
    }

    /**
     * Создаёт ячейку с текстом и стилем.
     */
    public static XWPFTableCell createStyledCell(XWPFTableRow row, String text,
                                                  String fontName, int fontSize,
                                                  boolean bold, boolean italic,
                                                  ParagraphAlignment align,
                                                  int widthTwips) {
        XWPFTableCell cell = row.addNewTableCell();

        // Установка ширины ячейки
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTTblWidth tcWidth = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
        tcWidth.setType(STTblWidth.DXA);
        tcWidth.setW(BigInteger.valueOf(widthTwips));

        // Установка границ ячейки
        CTTcBorders cellBorders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();
        setBorder(cellBorders.addNewTop(), "000000", 4);
        setBorder(cellBorders.addNewLeft(), "000000", 4);
        setBorder(cellBorders.addNewBottom(), "000000", 4);
        setBorder(cellBorders.addNewRight(), "000000", 4);

        // Добавление текста
        XWPFParagraph para = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
        para.setAlignment(align);
        para.setSpacingAfter(0);
        para.setSpacingBefore(0);

        // Очистить существующие run'ы
        while (para.getRuns().size() > 0) {
            para.removeRun(0);
        }

        XWPFRun run = para.createRun();
        run.setFontFamily(fontName);
        run.setFontSize(fontSize);
        run.setBold(bold);
        run.setItalic(italic);
        run.setText(text != null ? text : "");

        // Вертикальное выравнивание по центру
        CTVerticalJc vAlign = tcPr.isSetVAlign() ? tcPr.getVAlign() : tcPr.addNewVAlign();
        vAlign.setVal(STVerticalJc.CENTER);

        return cell;
    }
}
