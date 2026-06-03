# Модуль Assignments (Учебные задания)

## Описание

Модуль управления учебными заданиями для системы ГИА СПбУТУИЭ.

- **Преподаватели** создают задания, выставляют дедлайны, загружают материалы, проверяют работы.
- **Студенты** видят список заданий, сдают работы, получают оценки и обратную связь.
- **Гибкая система оценивания** — весовые критерии, накопительная система, пороги для автоматической оценки.

## Структура модуля

```
com.spbutu.gia.assignments
├── application
│   ├── dto          # DTO для API (AssignmentCreateDto, AssignmentDto, SubmissionDto, ReviewDto)
│   └── service      # Бизнес-логика (AssignmentService, AssignmentSubmissionService)
├── domain
│   ├── entity       # JPA-сущности (Assignment, AssignmentSubmission)
│   ├── enums        # Перечисления (AssignmentType, SubmissionStatus)
│   ├── repository   # Spring Data репозитории
│   └── vo           # Value Objects (ScoringConfig, AttachedFile)
└── web
    ├── TeacherAssignmentController.java   # REST API для преподавателей (/api/teacher/assignments)
    ├── StudentAssignmentController.java   # REST API для студентов (/api/student/assignments)
    └── AssignmentSseController.java       # SSE-уведомления (/api/sse/assignments)
```

## API Endpoints

### Преподаватель (`/api/teacher/assignments`)

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/` | Создать задание |
| GET | `/my` | Список заданий преподавателя |
| GET | `/{id}` | Детали задания |
| DELETE | `/{id}` | Удалить задание |
| GET | `/{id}/submissions` | Сдачи по заданию (фильтр `?status=`) |
| POST | `/{id}/submissions/{subId}/review` | Проверить сдачу |

### Студент (`/api/student/assignments`)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/my` | Мои задания |
| GET | `/{id}` | Детали задания |
| POST | `/{id}/submit` | Сдать задание |
| GET | `/submissions` | Мои сдачи |
| POST | `/submissions/{id}/save-draft` | Сохранить черновик |

### SSE-уведомления (`/api/sse/assignments`)

Подключение через `EventSource` с JWT-токеном в query-параметре.

События:
- `new-assignment` — новое задание назначено
- `submission-reviewed` — работа проверена, оценка выставлена
- `deadline-warning` — приближается дедлайн (за 24ч)

## Сущности

### Assignment (Задание)

| Поле | Тип | Описание |
|------|-----|----------|
| id | UUID | Идентификатор |
| title | String | Название |
| description | String | Описание |
| assignmentType | Enum | VKR, COURSEWORK, LAB, PRACTICE, EXAM, HOMEWORK |
| createdBy | UUID | Преподаватель-создатель |
| targetGroupId | UUID | Целевая группа |
| targetStudentIds | JSONB | Список ID студентов (опционально) |
| deadline | ZonedDateTime | Дедлайн |
| allowLateSubmission | boolean | Разрешить позднюю сдачу |
| maxScore | Integer | Максимальный балл |
| scoringConfig | JSONB | Конфигурация оценивания |
| attachedFiles | JSONB | Файлы преподавателя |

### AssignmentSubmission (Сдача)

| Поле | Тип | Описание |
|------|-----|----------|
| id | UUID | Идентификатор |
| assignmentId | UUID | Задание |
| studentId | UUID | Студент |
| solutionFiles | JSONB | Файлы решения |
| studentComment | String | Комментарий студента |
| status | Enum | DRAFT, SUBMITTED, REVIEWING, REVIEWED, RETURNED |
| submittedAt | ZonedDateTime | Время сдачи |
| totalScore | Integer | Итоговый балл |
| teacherFeedback | String | Обратная связь (видна студенту) |
| teacherComment | String | Внутренний комментарий |
| reviewedBy | UUID | Преподаватель-проверивший |
| reviewedAt | ZonedDateTime | Время проверки |
| version | Integer | Версия сдачи (поддержка пересдач) |

## Миграции БД

- `V32__assignments.sql` — создание таблиц assignments, assignment_submissions, ENUM-типов
- `V33__fix_assignment_target_student_ids.sql` — исправление типа target_student_ids на JSONB
- `V34__seed_demo_assignments.sql` — демо-данные для тестирования

## Интеграционные тесты

Тесты расположены в `src/test/java/com/spbutu/gia/assignments/AssignmentIntegrationTest.java`.

Используются **TestContainers** (PostgreSQL 15) для изолированного тестирования.

Запуск:
```bash
./mvnw test -Dtest=AssignmentIntegrationTest
```

## Интеграция с фронтендом

- **Teacher Portal**: `/teacher/assignments` — список, создание, проверка
- **Student Portal**: `/student/assignments` — просмотр, сдача, оценки
- **Zustand store**: `teacherAssignmentsStore.ts`, `studentAssignmentsStore.ts`
- **React Query hooks**: `useTeacherAssignments.ts`, `useStudentAssignments.ts`
- **API клиент**: `src/api/assignments.ts`

## Деплой

Модуль является частью монолитного backend-приложения. При деплое:

1. Убедиться, что Flyway миграции применены (`V32`, `V33`, `V34`).
2. Проверить `spring.security` конфигурацию — доступ к `/api/teacher/**` для ролей `SUPERVISOR`, `DEPARTMENT_HEAD`, `SYSTEM_ADMIN`, `UNIVERSITY_ADMIN`.
3. Проверить `spring.security` конфигурацию — доступ к `/api/student/**` для роли `STUDENT`.
4. Убедиться, что PostgreSQL поддерживает JSONB (версия 12+).

## Зависимости

- `hypersistence-utils-hibernate-63` — для маппинга JSONB в JPA
- `jackson-datatype-hibernate6` — для сериализации Hibernate-прокси
- `flyway-database-postgresql` — миграции схемы
- `testcontainers` (test scope) — интеграционные тесты
