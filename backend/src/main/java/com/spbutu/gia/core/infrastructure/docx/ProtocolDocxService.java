package com.spbutu.gia.core.infrastructure.docx;

import com.spbutu.gia.core.domain.entity.Protocol;
import com.spbutu.gia.core.domain.entity.ProtocolRecord;
import com.spbutu.gia.core.domain.entity.Student;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Сервис генерации протоколов DOCX по правильным шаблонам.
 * Генерирует документы с нуля через Apache POI XWPF.
 */
@Service
public class ProtocolDocxService {

    private static final Logger log = LoggerFactory.getLogger(ProtocolDocxService.class);

    // --- ИНДИВИДУАЛЬНЫЙ ПРОТОКОЛ ---

    public byte[] generateIndividualProtocol(ProtocolRecord record, Map<String, Object> extraData) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            setA4Page(doc);
            setMargins(doc, 1134, 850, 1134, 850); // ~2cm, ~1.5cm

            Student student = record.getStudent();
            Protocol protocol = record.getProtocol();
            var meeting = protocol != null ? protocol.getMeeting() : null;

            // === ЛОГОТИП + УНИВЕРСИТЕТ ===
            insertLogoAndHeader(doc);

            // === ЗАГОЛОВОК: ПРОТОКОЛ № ===
            addParagraph(doc, "ПРОТОКОЛ", "Times New Roman", 12, true, ParagraphAlignment.CENTER);
            String protocolNum = protocol != null && protocol.getProtocolNumber() != null ? protocol.getProtocolNumber() : "_____";
            addParagraph(doc, "№ " + protocolNum, "Times New Roman", 12, true, ParagraphAlignment.CENTER);

            addParagraph(doc, "заседания государственной экзаменационной комиссии", "Times New Roman", 12, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "по проведению государственной итоговой аттестации", "Times New Roman", 12, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "в форме защиты выпускной квалификационной работы", "Times New Roman", 12, false, ParagraphAlignment.CENTER);

            // === ДАТА И ВРЕМЯ ===
            String dateStr = formatDate(meeting);
            String timeStr = formatTimeRange(meeting);
            addParagraph(doc, "« " + extractDay(dateStr) + " »  " + extractMonthName(dateStr) + "  " + extractYear(dateStr) + " г.   с   " + extractStartTime(timeStr) + "   до   " + extractEndTime(timeStr), "Times New Roman", 12, false, ParagraphAlignment.CENTER);

            // === НА ТЕМУ ===
            addParagraph(doc, "На тему: " + (student != null && student.getThesisTopic() != null ? student.getThesisTopic() : "_________________________"), "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            // === СТУДЕНТ ===
            String studentFio = student != null ? fullName(student) : "_________________________";
            addParagraph(doc, "студента (ки) / экстерна  " + studentFio, "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            // === НАПРАВЛЕНИЕ, ПРОФИЛЬ, РУКОВОДИТЕЛЬ ===
            String dirCode = "—", dirName = "—", profile = "—", supervisor = "—";
            if (student != null && student.getGroup() != null && student.getGroup().getDirection() != null) {
                var dir = student.getGroup().getDirection();
                dirCode = dir.getCode() != null ? dir.getCode() : "—";
                dirName = dir.getName() != null ? dir.getName() : "—";
                profile = dir.getProfile() != null ? dir.getProfile() : dirName;
            }
            if (student != null && student.getSupervisorName() != null) supervisor = student.getSupervisorName();

            // === НАПРАВЛЕНИЕ + ГЭК В ОДНОЙ ТАБЛИЦЕ ===
            String chairman = extractString(extraData, "chairmanName");
            String members = extractString(extraData, "membersString");
            String secretary = extractString(extraData, "secretaryName");

            XWPFTable metaTable = doc.createTable(2, 3);
            setTableWidth(metaTable, "5000");
            setTableBorders(metaTable, false);

            setMetaCell(metaTable.getRow(0).getCell(0), "Направление подготовки", dirCode + " " + dirName, 2000);
            setMetaCell(metaTable.getRow(0).getCell(1), "Направленность (профиль)", profile, 2000);
            setMetaCell(metaTable.getRow(0).getCell(2), "Руководитель ВКР", supervisor, 1800);

            setMetaCell(metaTable.getRow(1).getCell(0), "Председатель ГЭК:", chairman.isBlank() ? "_________________________" : chairman, 2000);
            setMetaCell(metaTable.getRow(1).getCell(1), "Члены ГЭК:", members.isBlank() ? "_________________________" : members, 2000);
            setMetaCell(metaTable.getRow(1).getCell(2), "Секретарь ГЭК:", secretary.isBlank() ? "_________________________" : secretary, 1800);

            addParagraph(doc, "ГОСУДАРСТВЕННАЯ ЭКЗАМЕНАЦИОННАЯ КОМИССИЯ:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "Состав ГЭК утвержден приказом №______ от «___» ____________ 20__ г.", "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            // === МАТЕРИАЛЫ ===
            addParagraph(doc, "В ГЭК представлены следующие материалы:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "1. Приказ о допуске студента (ки) / экстерна к защите № ________ от ____________________ г. ____________.", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "2. ВКР на _____ страницах.", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "3. Материалы презентации в виде _________________________ на __ листах.", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "4. Отзыв руководителя ВКР с оценкой _____.", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "5. Дополнительные материалы ________________________________________________.", "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            // === ВОПРОСЫ (ТАБЛИЦА) ===
            addParagraph(doc, "После сообщения о выполненной работе в течение ____ мин. студенту (ке) / экстерну были заданы следующие вопросы:", "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            XWPFTable questionsTable = doc.createTable(4, 3);
            setTableWidth(questionsTable, "5000");
            setTableBorders(questionsTable, true);
            setCellText(questionsTable.getRow(0).getCell(0), "№ п/п", true);
            setCellText(questionsTable.getRow(0).getCell(1), "ФИО лица, задававшего вопросы", true);
            setCellText(questionsTable.getRow(0).getCell(2), "Содержание вопросов", true);
            for (int i = 1; i < 4; i++) {
                setCellText(questionsTable.getRow(i).getCell(0), String.valueOf(i), false);
                setCellText(questionsTable.getRow(i).getCell(1), "", false);
                setCellText(questionsTable.getRow(i).getCell(2), "", false);
            }

            addParagraph(doc, "Общая характеристика ответов студента / экстерна", "Times New Roman", 12, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "________________________________________________________________________________________________________________________", "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            // === РЕШЕНИЕ ГЭК ===
            addParagraph(doc, "РЕШЕНИЕ ГЭК:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);

            String finalGrade = record.getFinalScore() != null ? scoreToString(record.getFinalScore()) : "_________________________";
            addParagraph(doc, "1. Признать, что студент (ка) / экстерн выполнил (а) и защитил (а) ВКР с оценкой " + finalGrade, "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            String qualification = record.getQualification() != null ? record.getQualification() : "___________________________";
            String honors = Boolean.TRUE.equals(record.getIsWithHonors()) ? "с отличием" : "без отличия";
            addParagraph(doc, "2. Присвоить квалификацию " + qualification + " и выдать документ о высшем образовании и о квалификации образца, установленного Министерством науки и высшего образования Российской Федерации ____________________         (" + honors + ")", "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "3. Отметить, что ________________________________________________________________________________________________________", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "4. Особое мнение и рекомендации членов государственной экзаменационной комиссии ________________________________________________________________________________________________________", "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            // === ПОДПИСИ ===
            addParagraph(doc, "", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addTwoColumnLine(doc, "Председатель ГЭК:", chairman.isBlank() ? "___________________" : chairman);
            addTwoColumnLine(doc, "Члены ГЭК:", members.isBlank() ? "___________________" : members);
            addTwoColumnLine(doc, "Секретарь ГЭК:", secretary.isBlank() ? "___________________" : secretary);

            setFontFamilyThroughout(doc, "Times New Roman");
            doc.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка генерации индивидуального протокола DOCX", e);
            throw new RuntimeException("Не удалось сгенерировать индивидуальный протокол", e);
        }
    }

    // --- ИТОГОВЫЙ ПРОТОКОЛ ---

    public byte[] generateFinalProtocol(Protocol protocol, List<ProtocolRecord> records, Map<String, Object> extraData) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            setA4Page(doc);
            setMargins(doc, 1134, 850, 1134, 850);

            var meeting = protocol != null ? protocol.getMeeting() : null;
            var firstRecord = records.stream().findFirst().orElse(null);
            var firstStudent = firstRecord != null ? firstRecord.getStudent() : null;
            var direction = firstStudent != null && firstStudent.getGroup() != null ? firstStudent.getGroup().getDirection() : null;

            String chairman = extractString(extraData, "chairmanName");
            String members = extractString(extraData, "membersString");
            String secretary = extractString(extraData, "secretaryName");

            // === ШАПКА (без логотипа) ===
            XWPFTable headerTable = doc.createTable(1, 1);
            setTableWidth(headerTable, "5000");
            setTableBorders(headerTable, false);
            XWPFTableCell textCell = headerTable.getRow(0).getCell(0);
            addParagraph(textCell, "частное образовательное учреждение высшего образования", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, "\u00ABСАНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, "ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ\u00BB", "Times New Roman", 13, true, ParagraphAlignment.CENTER);

            // === ЗАГОЛОВОК ===
            addParagraph(doc, "ИТОГОВЫЙ ПРОТОКОЛ", "Times New Roman", 12, true, ParagraphAlignment.CENTER);
            String protocolNum = protocol != null && protocol.getProtocolNumber() != null ? protocol.getProtocolNumber() : "_____";
            addParagraph(doc, "№ " + protocolNum, "Times New Roman", 12, true, ParagraphAlignment.CENTER);

            addParagraph(doc, "заседания государственной экзаменационной комиссии", "Times New Roman", 12, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "по проведению государственной итоговой аттестации", "Times New Roman", 12, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "в форме защиты выпускной квалификационной работы", "Times New Roman", 12, false, ParagraphAlignment.CENTER);

            // === НАПРАВЛЕНИЕ + ГЭК В ОДНОЙ ТАБЛИЦЕ ===
            String dirCode = direction != null && direction.getCode() != null ? direction.getCode() : "—";
            String dirName = direction != null && direction.getName() != null ? direction.getName() : "—";
            String profile = direction != null && direction.getProfile() != null ? direction.getProfile() : dirName;

            XWPFTable metaTable = doc.createTable(2, 3);
            setTableWidth(metaTable, "5000");
            setTableBorders(metaTable, false);

            setMetaCell(metaTable.getRow(0).getCell(0), "Направление подготовки", dirCode + " " + dirName, 2000);
            setMetaCell(metaTable.getRow(0).getCell(1), "Направленность (профиль)", profile, 2000);
            setMetaCell(metaTable.getRow(0).getCell(2), "Председатель", chairman.isBlank() ? "_________________________" : chairman, 1800);

            setMetaCell(metaTable.getRow(1).getCell(0), "Члены ГЭК:", members.isBlank() ? "_________________________" : members, 3000);
            setMetaCell(metaTable.getRow(1).getCell(1), "Секретарь ГЭК:", secretary.isBlank() ? "_________________________" : secretary, 2000);
            setMetaCell(metaTable.getRow(1).getCell(2), "", "", 1000);

            // === ДАТА ВРЕМЯ ===
            String dateStr = formatDate(meeting);
            String timeStr = formatTimeRange(meeting);
            addParagraph(doc, "« " + extractDay(dateStr) + " »  " + extractMonthName(dateStr) + "  " + extractYear(dateStr) + " г.   с  " + extractStartTime(timeStr) + " час.   до  " + extractEndTime(timeStr) + " час.", "Times New Roman", 12, false, ParagraphAlignment.CENTER);

            addParagraph(doc, "ГОСУДАРСТВЕННАЯ ЭКЗАМЕНАЦИОННАЯ КОМИССИЯ:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "Состав ГЭК утвержден приказом №______ от «___» ____________ 20__ г.", "Times New Roman", 12, false, ParagraphAlignment.LEFT);

            // === ВСТУПЛЕНИЕ ===
            addParagraph(doc, "Заслушав защиты и рассмотрев представленные материалы, отзывы руководителей, ответы на заданные вопросы и пр., комиссия", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "ПОСТАНОВИЛА:", "Times New Roman", 12, true, ParagraphAlignment.CENTER);

            // === I. ПРИЗНАТЬ ===
            var present = records.stream().filter(r -> !Boolean.TRUE.equals(r.getIsAbsent())).toList();
            addParagraph(doc, "I. Признать, что перечисленные ниже студенты/экстерны выполнили и защитили выпускные квалификационные работы с оценками:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);

            if (!present.isEmpty()) {
                XWPFTable table1 = doc.createTable(present.size() + 1, 3);
                setTableWidth(table1, "5000");
                setTableBorders(table1, true);
                setCellText(table1.getRow(0).getCell(0), "№", true);
                setCellText(table1.getRow(0).getCell(1), "ФИО", true);
                setCellText(table1.getRow(0).getCell(2), "Оценка", true);
                for (int i = 0; i < present.size(); i++) {
                    var r = present.get(i);
                    var s = r.getStudent();
                    setCellText(table1.getRow(i + 1).getCell(0), String.valueOf(i + 1), false);
                    setCellText(table1.getRow(i + 1).getCell(1), s != null ? fullName(s) : "—", false);
                    setCellText(table1.getRow(i + 1).getCell(2), r.getFinalScore() != null ? scoreToString(r.getFinalScore()) : "—", false);
                }
            }

            // === II. НЕ ЯВИЛИСЬ ===
            var absent = records.stream().filter(r -> Boolean.TRUE.equals(r.getIsAbsent())).toList();
            if (!absent.isEmpty()) {
                addParagraph(doc, "II. Признать, что на защиту ВКР не явились следующие студенты/экстерны:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);
                XWPFTable table2 = doc.createTable(absent.size() + 1, 2);
                setTableWidth(table2, "5000");
                setTableBorders(table2, true);
                setCellText(table2.getRow(0).getCell(0), "№", true);
                setCellText(table2.getRow(0).getCell(1), "ФИО", true);
                for (int i = 0; i < absent.size(); i++) {
                    var r = absent.get(i);
                    var s = r.getStudent();
                    setCellText(table2.getRow(i + 1).getCell(0), String.valueOf(i + 1), false);
                    setCellText(table2.getRow(i + 1).getCell(1), s != null ? fullName(s) : "—", false);
                }
            }

            // === III. ПРИСВОИТЬ КВАЛИФИКАЦИЮ ===
            addParagraph(doc, "III. Присвоить перечисленным студентам/экстернам квалификацию бакалавр по направлению подготовки " + dirName + " и выдать документ о высшем образовании и о квалификации образца, установленного Министерством науки и высшего образования Российской Федерации:", "Times New Roman", 12, true, ParagraphAlignment.LEFT);

            if (!present.isEmpty()) {
                XWPFTable table3 = doc.createTable(present.size() + 1, 2);
                setTableWidth(table3, "5000");
                setTableBorders(table3, true);
                setCellText(table3.getRow(0).getCell(0), "№", true);
                setCellText(table3.getRow(0).getCell(1), "ФИО", true);
                for (int i = 0; i < present.size(); i++) {
                    var r = present.get(i);
                    var s = r.getStudent();
                    setCellText(table3.getRow(i + 1).getCell(0), String.valueOf(i + 1), false);
                    setCellText(table3.getRow(i + 1).getCell(1), s != null ? fullName(s) : "—", false);
                }
            }

            // === ПОДПИСИ ===
            addParagraph(doc, "", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addTwoColumnLine(doc, "Председатель ГЭК", chairman.isBlank() ? "___________________" : chairman);
            addTwoColumnLine(doc, "Секретарь ГЭК", secretary.isBlank() ? "___________________" : secretary);

            setFontFamilyThroughout(doc, "Times New Roman");
            doc.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка генерации итогового протокола DOCX", e);
            throw new RuntimeException("Не удалось сгенерировать итоговый протокол", e);
        }
    }

    // --- ВЕДОМОСТЬ DOCX ---

    public byte[] generateVedomost(com.spbutu.gia.core.application.dto.VedomostDto vedomost, Map<String, Object> extraData) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            setA4Page(doc);
            setMargins(doc, 567, 567, 851, 567);

            // === ЛОГОТИП + ИНСТИТУТ ===
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
                log.warn("Не удалось вставить логотип", e);
            }

            XWPFTableCell textCell = headerTable.getRow(0).getCell(1);
            textCell.setWidth("3500");
            addParagraph(textCell, "частное образовательное учреждение высшего образования", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, "\u00ABСАНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, "ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ\u00BB", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
            addParagraph(textCell, vedomost.getInstituteName() != null ? vedomost.getInstituteName() : "Институт управления и информационных технологий", "Times New Roman", 13, true, ParagraphAlignment.CENTER);

            // === ЗАГОЛОВОК ===
            addParagraph(doc, "ВЕДОМОСТЬ ЗАЩИТЫ ВЫПУСКНЫХ КВАЛИФИКАЦИОННЫХ РАБОТ", "Times New Roman", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "№ " + (vedomost.getDocumentNumber() != null ? vedomost.getDocumentNumber() : ""), "Times New Roman", 12, true, ParagraphAlignment.CENTER);

            // === МЕТАДАННЫЕ ===
            addMetaLine(doc, "Учебный год", vedomost.getAcademicYear());
            String directionFull = (vedomost.getDirectionCode() != null ? vedomost.getDirectionCode() : "") + " " + (vedomost.getDirectionShort() != null ? vedomost.getDirectionShort() : "");
            addMetaLine(doc, "Направление", directionFull.trim());

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
            XWPFTable studentsTable = doc.createTable(1, 5);
            setTableWidth(studentsTable, "5000");
            setTableBorders(studentsTable, true);
            setCellText(studentsTable.getRow(0).getCell(0), "№ п/п", true);
            setCellText(studentsTable.getRow(0).getCell(1), "Ф.И.О. студента", true);
            setCellText(studentsTable.getRow(0).getCell(2), "№ зачетной книжки", true);
            setCellText(studentsTable.getRow(0).getCell(3), "Оценка (баллы)", true);
            setCellText(studentsTable.getRow(0).getCell(4), "Оценка (классическая)", true);

            var rows = vedomost.getStudents();
            if (rows != null) {
                for (int i = 0; i < rows.size(); i++) {
                    var row = rows.get(i);
                    XWPFTableRow tRow = studentsTable.createRow();
                    setCellText(tRow.getCell(0), String.valueOf(i + 1), false);
                    setCellText(tRow.getCell(1), row.getFullName() != null ? row.getFullName() : "", false);
                    setCellText(tRow.getCell(2), row.getRecordBookNumber() != null ? row.getRecordBookNumber() : "", false);
                    setCellText(tRow.getCell(3), row.getScorePoints() != null ? String.valueOf(row.getScorePoints()) : "", false);
                    setCellText(tRow.getCell(4), row.getScoreClassic() != null ? row.getScoreClassic() : "", false);
                }
            }

            // === СТАТИСТИКА ===
            addParagraph(doc, "", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            if (vedomost.getTotalStudents() != null) {
                addParagraph(doc, "Число студентов, участвовавших в аттестации " + vedomost.getTotalStudents() + " из них получивших:", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
                addParagraph(doc, "«зачтено» " + (vedomost.getCountZachteno() != null ? vedomost.getCountZachteno() : 0) + "    «не зачтено» " + (vedomost.getCountNeZachteno() != null ? vedomost.getCountNeZachteno() : 0), "Times New Roman", 12, false, ParagraphAlignment.LEFT);
                addParagraph(doc, "«отлично» " + (vedomost.getCountOtlichno() != null ? vedomost.getCountOtlichno() : 0) + "    «хорошо» " + (vedomost.getCountHorosho() != null ? vedomost.getCountHorosho() : 0) + "    «удовлетворительно» " + (vedomost.getCountUdov() != null ? vedomost.getCountUdov() : 0) + "    «неудовлетворительно» " + (vedomost.getCountNeud() != null ? vedomost.getCountNeud() : 0), "Times New Roman", 12, false, ParagraphAlignment.LEFT);
                addParagraph(doc, "Число студентов, не явившихся на ГИА " + (vedomost.getCountAbsent() != null ? vedomost.getCountAbsent() : 0), "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            }

            // === ПОДПИСИ ===
            addParagraph(doc, "", "Times New Roman", 12, false, ParagraphAlignment.LEFT);
            addTwoColumnLine(doc, "Председатель ГЭК", chairmanFull.isBlank() ? "___________________" : chairmanFull);
            addTwoColumnLine(doc, "Члены ГЭК:", membersLine.isBlank() ? "___________________" : membersLine);

            setFontFamilyThroughout(doc, "Times New Roman");
            doc.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка генерации ведомости DOCX", e);
            throw new RuntimeException("Не удалось сгенерировать ведомость", e);
        }
    }

    // --- HELPERS ---

    private void insertLogoAndHeader(XWPFDocument doc) {
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
            log.warn("Не удалось вставить логотип", e);
        }

        XWPFTableCell textCell = headerTable.getRow(0).getCell(1);
        textCell.setWidth("3500");
        addParagraph(textCell, "частное образовательное учреждение высшего образования", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
        addParagraph(textCell, "\u00ABСАНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
        addParagraph(textCell, "ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ\u00BB", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
    }

    private void addParagraph(XWPFDocument doc, String text, String font, int size, boolean bold, ParagraphAlignment align) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(align);
        p.setSpacingAfter(120);
        XWPFRun run = p.createRun();
        run.setFontFamily(font);
        run.setFontSize(size);
        run.setBold(bold);
        run.setText(text);
    }

    private void addParagraph(XWPFTableCell cell, String text, String font, int size, boolean bold, ParagraphAlignment align) {
        XWPFParagraph p = cell.getParagraphs().isEmpty() ? cell.addParagraph() : cell.getParagraphs().get(0);
        p.setAlignment(align);
        p.setSpacingAfter(60);
        XWPFRun run = p.createRun();
        run.setFontFamily(font);
        run.setFontSize(size);
        run.setBold(bold);
        run.setText(text);
    }

    private void addTwoColumnLine(XWPFDocument doc, String label, String value) {
        XWPFTable table = doc.createTable(1, 2);
        setTableWidth(table, "5000");
        setTableBorders(table, false);
        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r1 = p1.createRun();
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(11);
        r1.setBold(false);
        r1.setText(label);

        XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r2 = p2.createRun();
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(11);
        r2.setItalic(true);
        r2.setUnderline(UnderlinePatterns.SINGLE);
        r2.setText(value);

        table.getRow(0).getCell(0).setWidth("2500");
        table.getRow(0).getCell(1).setWidth("2500");
    }

    private void addMetaLine(XWPFDocument doc, String label, String value) {
        XWPFTable table = doc.createTable(1, 2);
        setTableWidth(table, "5000");
        setTableBorders(table, false);
        XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);
        p1.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r1 = p1.createRun();
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(11);
        r1.setText(label + ":");

        XWPFParagraph p2 = table.getRow(0).getCell(1).getParagraphs().get(0);
        p2.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r2 = p2.createRun();
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(11);
        r2.setItalic(true);
        r2.setUnderline(UnderlinePatterns.SINGLE);
        r2.setText(value != null ? value : "");

        table.getRow(0).getCell(0).setWidth("2000");
        table.getRow(0).getCell(1).setWidth("3000");
    }

    private void setGroupCell(XWPFTableCell cell, String label, String value, int widthTwips) {
        cell.setWidth(String.valueOf(widthTwips));
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun r1 = p.createRun();
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(12);
        r1.setBold(true);
        r1.setText(label + " ");
        XWPFRun r2 = p.createRun();
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(12);
        r2.setBold(true);
        r2.setItalic(true);
        r2.setText(value != null ? value : "");
        r2.setUnderline(UnderlinePatterns.SINGLE);
    }

    private void setMetaCell(XWPFTableCell cell, String label, String value, int widthTwips) {
        cell.setWidth(String.valueOf(widthTwips));
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.LEFT);
        for (int i = p.getRuns().size() - 1; i >= 0; i--) {
            p.removeRun(i);
        }
        XWPFRun r1 = p.createRun();
        r1.setFontFamily("Times New Roman");
        r1.setFontSize(11);
        r1.setText(label + " ");
        XWPFRun r2 = p.createRun();
        r2.setFontFamily("Times New Roman");
        r2.setFontSize(11);
        r2.setItalic(true);
        r2.setUnderline(UnderlinePatterns.SINGLE);
        r2.setText(value != null ? value : "");
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold) {
        XWPFParagraph p = cell.getParagraphs().get(0);
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = p.createRun();
        run.setFontFamily("Times New Roman");
        run.setFontSize(12);
        run.setBold(bold);
        run.setText(text);
    }

    private void setTableWidth(XWPFTable table, String width) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();
        CTTblWidth tblWidth = tblPr.getTblW();
        if (tblWidth == null) tblWidth = tblPr.addNewTblW();
        tblWidth.setW(new BigInteger(width));
        tblWidth.setType(STTblWidth.PCT);
    }

    private void setTableBorders(XWPFTable table, boolean visible) {
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();
        CTTblBorders borders = tblPr.getTblBorders();
        if (borders == null) borders = tblPr.addNewTblBorders();
        if (!visible) {
            borders.getTop().setVal(STBorder.NONE);
            borders.getBottom().setVal(STBorder.NONE);
            borders.getLeft().setVal(STBorder.NONE);
            borders.getRight().setVal(STBorder.NONE);
            borders.getInsideH().setVal(STBorder.NONE);
            borders.getInsideV().setVal(STBorder.NONE);
        }
    }

    private void setA4Page(XWPFDocument doc) {
        CTBody body = doc.getDocument().getBody();
        CTSectPr sectPr = body.isSetSectPr() ? body.getSectPr() : body.addNewSectPr();
        CTPageSz pageSize = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
        pageSize.setW(BigInteger.valueOf(11906)); // A4 width in twips
        pageSize.setH(BigInteger.valueOf(16838)); // A4 height in twips
    }

    private void setMargins(XWPFDocument doc, int left, int top, int right, int bottom) {
        CTBody body = doc.getDocument().getBody();
        CTSectPr sectPr = body.isSetSectPr() ? body.getSectPr() : body.addNewSectPr();
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar mar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
        mar.setLeft(BigInteger.valueOf(left));
        mar.setTop(BigInteger.valueOf(top));
        mar.setRight(BigInteger.valueOf(right));
        mar.setBottom(BigInteger.valueOf(bottom));
    }

    private void setFontFamilyThroughout(XWPFDocument doc, String fontFamily) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            for (XWPFRun r : p.getRuns()) {
                r.setFontFamily(fontFamily);
            }
        }
        for (XWPFTable t : doc.getTables()) {
            for (XWPFTableRow row : t.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            r.setFontFamily(fontFamily);
                        }
                    }
                }
            }
        }
    }

    // --- FORMATTING UTILS ---

    private String fullName(Student s) {
        if (s == null) return "—";
        String name = s.getLastName() + " " + s.getFirstName();
        if (s.getMiddleName() != null && !s.getMiddleName().isBlank()) {
            name += " " + s.getMiddleName();
        }
        return name;
    }

    private String formatDate(com.spbutu.gia.core.domain.entity.Meeting meeting) {
        if (meeting == null || meeting.getMeetingDate() == null) return "—";
        return meeting.getMeetingDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String formatTimeRange(com.spbutu.gia.core.domain.entity.Meeting meeting) {
        if (meeting == null) return "—";
        String start = meeting.getStartTime() != null ? meeting.getStartTime().toString() : "—";
        String end = meeting.getEndTime() != null ? meeting.getEndTime().toString() : "—";
        return start + " - " + end;
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

    private String extractStartTime(String timeStr) {
        if (timeStr == null || timeStr.equals("—")) return "__";
        String[] parts = timeStr.split(" - ");
        return parts.length > 0 ? parts[0] : "__";
    }

    private String extractEndTime(String timeStr) {
        if (timeStr == null || timeStr.equals("—")) return "__";
        String[] parts = timeStr.split(" - ");
        return parts.length > 1 ? parts[1] : "__";
    }

    private String scoreToString(Integer score) {
        if (score == null) return "—";
        return switch (score) {
            case 5 -> "отлично";
            case 4 -> "хорошо";
            case 3 -> "удовлетворительно";
            case 2 -> "неудовлетворительно";
            default -> String.valueOf(score);
        };
    }

    private String extractString(Map<String, Object> data, String key) {
        if (data == null) return "";
        Object val = data.get(key);
        return val != null ? val.toString() : "";
    }
}
