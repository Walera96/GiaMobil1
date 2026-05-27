# Инструкция по сборке проекта GIA_Mobile после правок

## Путь к проекту
`C:\Users\Валера\Desktop\GIA_Mobile`

## Общие изменения

### 1. Исправлено форматирование метаданных в DOCX

**Проблема:** Value-поля (значения) в метаданных были жирными (**bold**), а по шаблону должны быть *курсив + подчёркивание*.

**Решение:** Изменено форматирование run-ов в методах генерации.

---

## Изменённые файлы

### Файл 1: `VedomostWordService.java`
**Путь:** `backend/src/main/java/com/spbutu/gia/core/infrastructure/docx/VedomostWordService.java`

**Что изменено:**
1. **Метод `addMetaLine`** — форматирование Value изменено:
   - Было: `r2.setBold(true)` (жирный)
   - Стало: `r2.setItalic(true)` + `r2.setUnderline(UnderlinePatterns.SINGLE)` (курсив + подчёркивание)
   - Размер шрифта: 12pt (полупунктов = 24)

   ```java
   XWPFRun r2 = p2.createRun();
   r2.setFontFamily("Times New Roman");
   r2.setFontSize(12);
   r2.setItalic(true);
   r2.setUnderline(UnderlinePatterns.SINGLE);
   r2.setText(value != null ? value : "");
   ```

2. **Метод `setGroupCell`** — форматирование:
   - Label: **жирный**, 12pt
   - Value: ***жирный + курсив + подчёркивание***, 12pt

   ```java
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
   ```

3. **Метод `generateVedomost`** — структура метаданных:
   - Учебный год, Направление, Кафедра, Форма ГИА, Председатель ГЭК, Члены ГЭК — каждый в своей строке через `addMetaLine()` (2 колонки: Label | Value)
   - Группа/Курс/Дата — в одной строке через `setGroupCell()` (6 колонок с пустыми спейсерами)

---

### Файл 2: `ProtocolDocxService.java`
**Путь:** `backend/src/main/java/com/spbutu/gia/core/infrastructure/docx/ProtocolDocxService.java`

**Что изменено:**

1. **Метод `generateIndividualProtocol`** — метаданные перенесены в **одну таблицу 2×3** (было: отдельные строки через `addTwoColumnLine`):
   - Row 0: Направление подготовки | Направленность (профиль) | Руководитель ВКР
   - Row 1: Председатель ГЭК | Члены ГЭК | Секретарь ГЭК
   - Форматирование через `setMetaCell()`: Label = 11pt обычный, Value = 11pt *курсив + подчёркивание*

   ```java
   XWPFTable metaTable = doc.createTable(2, 3);
   setTableWidth(metaTable, "5000");
   setTableBorders(metaTable, false);
   
   setMetaCell(metaTable.getRow(0).getCell(0), "Направление подготовки", dirCode + " " + dirName, 2000);
   setMetaCell(metaTable.getRow(0).getCell(1), "Направленность (профиль)", profile, 2000);
   setMetaCell(metaTable.getRow(0).getCell(2), "Руководитель ВКР", supervisor, 1800);
   
   setMetaCell(metaTable.getRow(1).getCell(0), "Председатель ГЭК:", chairman, 2000);
   setMetaCell(metaTable.getRow(1).getCell(1), "Члены ГЭК:", members, 2000);
   setMetaCell(metaTable.getRow(1).getCell(2), "Секретарь ГЭК:", secretary, 1800);
   ```

2. **Метод `generateFinalProtocol`** — аналогично, таблица 2×3:
   - Row 0: Направление подготовки | Направленность (профиль) | Председатель
   - Row 1: Члены ГЭК | Секретарь ГЭК | (пусто)

3. **Убран логотип** из итогового протокола (`generateFinalProtocol`). Теперь только текстовая шапка:
   ```java
   XWPFTable headerTable = doc.createTable(1, 1);
   setTableWidth(headerTable, "5000");
   setTableBorders(headerTable, false);
   XWPFTableCell textCell = headerTable.getRow(0).getCell(0);
   addParagraph(textCell, "частное образовательное учреждение высшего образования", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
   addParagraph(textCell, "\u00ABСАНКТ-ПЕТЕРБУРГСКИЙ УНИВЕРСИТЕТ", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
   addParagraph(textCell, "ТЕХНОЛОГИЙ УПРАВЛЕНИЯ И ЭКОНОМИКИ\u00BB", "Times New Roman", 13, true, ParagraphAlignment.CENTER);
   ```

4. **Метод `generateVedomost`** (в ProtocolDocxService) — метаданные вертикально:
   - Кафедра, Форма ГИА, Председатель ГЭК, Члены ГЭК — через `addTwoColumnLine()` (Label | Value)
   - Value форматирование: 11pt *курсив + подчёркивание* (было 12pt **жирный**)
   - Группа/Курс/Дата — через `setGroupCell()` в одной строке

5. **Новый метод `setMetaCell`** — универсальный метод для ячеек метаданных:
   ```java
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
   ```

---

### Файл 3: `DocxGenerationService.java`
**Путь:** `backend/src/main/java/com/spbutu/gia/core/infrastructure/docx/DocxGenerationService.java`

**Что изменено:**
- Улучшено логирование ошибок:
```java
// Было:
log.warn("Ошибка генерации документа из шаблона: {}", e.getMessage());
// Стало:
log.error("Ошибка генерации документа из шаблона: {}", e.getMessage(), e);
```

---

### Файл 4: `App.js` (frontend)
**Путь:** `frontend/src/App.js`

**Что изменено:**
- Добавлен импорт `StatusBar` из `expo-status-bar`:
```javascript
import { StatusBar } from 'expo-status-bar';
```
- Добавлен компонент в JSX:
```jsx
<StatusBar style="auto" />
```

**Важно:** `expo-status-bar` работает только в Expo/React Native. Если frontend — обычный React для веба, этот импорт может вызвать ошибку сборки. Для веба используйте `@react-native-community/netinfo` или просто удалите эту строку.

---

## Шаблоны DOCX (не изменялись)

В `backend/src/main/resources/templates/docx/` лежат шаблоны, которые заполняются данными:
- `vedmost_gia_shablon.docx` — таблица студентов (без шапки)
- `vedmost.docx` — таблица студентов (без шапки)
- `individual_protocol_shablon.docx` — таблица вопросов (без шапки)

Шапка (университет, кафедра, группа и т.д.) генерируется **кодом** в сервисах выше.

---

## Команды для сборки

### Backend (Spring Boot)
```bash
cd C:\Users\Валера\Desktop\GIA_Mobile\backend
mvn clean install -DskipTests
```

### Frontend (React)
```bash
cd C:\Users\Валера\Desktop\GIA_Mobile\frontend
npm install  # если expo-status-bar не установлен
npm start
```

Если `expo-status-bar` не установлен:
```bash
cd C:\Users\Валера\Desktop\GIA_Mobile\frontend
npm install expo-status-bar
```

---

## Проверка после сборки

1. **Ведомость** — проверить форматирование:
   - Учебный год, Направление, Кафедра, Форма ГИА, Председатель, Члены ГЭК — каждый в своей строке (Label слева, Value справа)
   - Value-поля должны быть *курсив + подчёркивание*
   - Группа/Курс/Дата — в одной строке

2. **Итоговый протокол** — проверить:
   - Направление/Профиль/Председатель — в одной строке
   - Члены ГЭК/Секретарь — во второй строке
   - **Нет логотипа** — только текстовая шапка

3. **Индивидуальный протокол** — проверить:
   - Направление/Профиль/Руководитель — в одной строке
   - Председатель/Члены/Секретарь — во второй строке
   - **Логотип есть**

4. **Ведомость (из ProtocolDocxService)** — проверить:
   - Кафедра/Форма/Председатель/Члены — каждый в своей строке
   - Value — *курсив + подчёркивание* (не жирный)
   - Группа/Курс/Дата — в одной строке

---

## Контакт
Если после сборки что-то не так — проверьте:
1. Версия Apache POI (должна поддерживать XWPFTableCell.setWidth)
2. Логи Spring Boot на ошибки компиляции
3. Консоль браузера на ошибки frontend
