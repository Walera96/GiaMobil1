# Промпт для Kimi Code: Мобильное приложение GIA_Mobile

## Контекст

Существующий проект **GIA_Mobile** — это система управления государственной итоговой аттестацией (ГИА) для вуза. 

**Стек:**
- Backend: Spring Boot 3.5.14 + Java 17 + PostgreSQL 15 + Flyway
- Frontend Web: React 18 + Vite + TypeScript + Tailwind CSS + shadcn/ui + Zustand + TanStack Query
- Mobile: Expo (React Native) — планируется

**Backend API:** `http://localhost:8090/api`

## Задача

Создать мобильное приложение на **Expo (React Native)** в папке `mobile/`, которое:

1. **Аутентификация**: JWT (access + refresh), вход по логину/паролю, сохранение токенов в SecureStore
2. **Порталы**: после входа показывать `PortalSelector` с доступными порталами (admin, methodist, gek, student, teacher_portal)
3. **Student Portal** (приоритет):
   - Список заданий с фильтрами (Все/Активные/Просроченные/Завершённые)
   - Карточка задания: тип (LAB/COURSEWORK/HOMEWORK/EXAM/PROJECT), дедлайн, статус, баллы
   - Детали задания: описание, таймер дедлайна, форма сдачи (текст + файлы)
   - Push-уведомления о новых заданиях и оценках (через Expo Notifications)
4. **Teacher Portal**:
   - Список созданных заданий
   - Просмотр сдач студентов
   - Выставление оценки (слайдер + feedback)
5. **Notifications**: центр уведомлений с real-time обновлениями (SSE через EventSource или polling)
6. **Offline**: кэширование списка заданий в AsyncStorage для работы без интернета

## API Endpoints (основные)

```
POST   /auth/login
POST   /auth/refresh
GET    /auth/me

GET    /api/student/assignments?status=&page=&size=
GET    /api/student/assignments/{id}
POST   /api/student/assignments/{id}/submit
GET    /api/student/assignments/my-submissions

GET    /api/teacher/assignments/my
POST   /api/teacher/assignments
GET    /api/teacher/assignments/{id}/submissions
POST   /api/teacher/assignments/{id}/submissions/{subId}/review

GET    /api/sse/notifications?token=...
```

## Требования к UI

- **NativeWind** (Tailwind для React Native) или **StyleSheet**
- **Bottom Sheet** для модальных окон
- **React Navigation** (bottom tabs + stack)
- **Lucide React Native** для иконок
- **Zustand** для состояния
- **TanStack Query** для серверного состояния
- **Expo Image Picker** для прикрепления файлов
- **Expo Notifications** для push

## Структура проекта

```
mobile/
├── App.tsx
├── src/
│   ├── api/
│   │   ├── axios.ts          // axios instance с JWT interceptor
│   │   ├── auth.ts
│   │   ├── assignments.ts
│   │   └── sse.ts
│   ├── components/
│   │   ├── ui/               // Button, Card, Badge, Input, Progress
│   │   ├── AssignmentCard.tsx
│   │   ├── SubmissionForm.tsx
│   │   └── ReviewModal.tsx
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useAssignments.ts
│   │   └── useNotifications.ts
│   ├── store/
│   │   ├── authStore.ts
│   │   └── assignmentStore.ts
│   ├── navigation/
│   │   ├── AppNavigator.tsx
│   │   ├── StudentNavigator.tsx
│   │   └── TeacherNavigator.tsx
│   ├── screens/
│   │   ├── LoginScreen.tsx
│   │   ├── PortalSelectorScreen.tsx
│   │   ├── student/
│   │   │   ├── StudentAssignmentsScreen.tsx
│   │   │   ├── AssignmentDetailScreen.tsx
│   │   │   └── StudentProfileScreen.tsx
│   │   └── teacher/
│   │       ├── TeacherAssignmentsScreen.tsx
│   │       ├── SubmissionsScreen.tsx
│   │       └── ReviewScreen.tsx
│   └── types/
│       └── index.ts
├── package.json
└── app.json
```

## Критерии готовности

- [ ] Вход работает, токены сохраняются
- [ ] Студент видит список заданий
- [ ] Студент может сдать задание (текст)
- [ ] Преподаватель видит сдачи и может оценить
- [ ] Уведомления приходят в реальном времени
- [ ] Приложение собирается через `npx expo start`
- [ ] TypeScript без ошибок (`tsc --noEmit`)

## Ограничения

- НЕ менять backend код
- НЕ менять frontend-web код
- Использовать существующее API
- Поддержка Android и iOS
