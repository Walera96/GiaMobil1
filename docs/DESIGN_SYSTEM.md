# Дизайн-система ГИА

## Цветовая палитра

| Токен | Значение | Назначение |
|---|---|---|
| `--color-primary` | `#1e40af` | Основной синий (кнопки, заголовки) |
| `--color-primary-hover` | `#1e3a8a` | Ховер primary |
| `--color-secondary` | `#64748b` | Вторичный (текст, иконки) |
| `--color-success` | `#16a34a` | Успех, оценка 5 |
| `--color-warning` | `#f59e0b` | Предупреждение, оценка 3 |
| `--color-danger` | `#dc2626` | Ошибка, оценка 2 |
| `--color-info` | `#3b82f6` | Информация, оценка 4 |
| `--color-bg` | `#f8fafc` | Фон страницы |
| `--color-surface` | `#ffffff` | Карточки, модалки |
| `--color-border` | `#e2e8f0` | Границы |
| `--color-text` | `#0f172a` | Основной текст |
| `--color-text-muted` | `#64748b` | Вторичный текст |

## Типографика

| Элемент | Размер | Вес | Дополнительно |
|---|---|---|---|
| H1 | 24px | 700 | color: `--color-text` |
| H2 | 20px | 600 | — |
| Тело | 14px | 400 | line-height: 1.5 |
| Моноширинный | 12px | 400 | JetBrains Mono / system monospace |
| Шрифт основной | — | — | Inter или `-apple-system, BlinkMacSystemFont, "Segoe UI"` |

## Компоненты UI-kit

### Button
- **Варианты:** primary (синий), secondary (серый), danger (красный), ghost (прозрачный)
- **Размеры:** sm (32px), md (40px), lg (48px)
- **Скругление:** 6px
- **Состояния:** default, hover, active, disabled

### Card
- Фон: `--color-surface`
- Тень: `0 1px 3px rgba(0,0,0,0.1)`
- Скругление: 8px
- Padding: 16px

### Table
- Шапка: фон `#f1f5f9`, font-weight 600
- Границы: `1px solid var(--color-border)`
- Hover-строка: `#f8fafc`
- Ячейки: padding 12px 16px

### Badge
- **success:** фон `#dcfce7`, текст `#166534`
- **warning:** фон `#fef3c7`, текст `#92400e`
- **danger:** фон `#fee2e2`, текст `#991b1b`
- **info:** фон `#dbeafe`, текст `#1e40af`
- **default:** фон `#f1f5f9`, текст `#475569`

### Modal
- Центрированный, max-width 600px
- Затемнение фона: `rgba(0,0,0,0.5)`
- Скругление: 12px
- Padding: 24px

### Toast
- Позиция: правый верхний угол
- Автоисчезновение через 4 сек
- Типы: success, error, warning, info

### Input
- Граница: `1px solid var(--color-border)`
- Скругление: 6px
- Padding: 8px 12px
- Фокус: кольцо `2px solid var(--color-primary)`
- Ошибка: граница `--color-danger`

### Select
- Аналогично Input
- Стрелка справа, поворот при открытии

## Иконки

- **Web:** `lucide-react`
- **Mobile:** `@expo/vector-icons` (MaterialCommunityIcons)
- Не использовать эмодзи
