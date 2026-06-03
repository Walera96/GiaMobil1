# Инструкции по применению патчей портальной архитектуры

> **Статус проекта:** Большинство патчей из `patches_gia_full/` уже интегрированы.  
> Данный документ описывает полный процесс для новой установки и содержит заметки о текущем состоянии.

---

## Содержание

1. [Что уже применено](#что-уже-применено)
2. [Файлы патчей](#файлы-патчей)
3. [Пошаговая миграция](#пошаговая-миграция)
4. [Проверка после применения](#проверка-после-применения)
5. [Риски и откат](#риски-и-откат)

---

## Что уже применено

Следующие компоненты уже работают в текущей кодовой базе:

| Компонент | Статус | Примечание |
|-----------|--------|------------|
| `UserRole.java` | ✅ | С методами `getPortal()`, `isAdminPortal()`, `isGekPortal()` |
| `AppUser.java` | ✅ | `Set<UserRole>`, `primaryPortal`, `departmentId`, `studyGroupId` |
| `CustomUserDetails.java` | ✅ | Мульти-роль authorities |
| `SecurityConfig.java` | ✅ | `/portal/*` маршруты + переходные `/groups`, `/meetings` |
| `AuthController.java` | ✅ | Возвращает `roles`, `availablePortals`, `primaryPortal` |
| `authStore.ts` | ✅ | Zustand с мульти-ролями |
| `PortalSelector.tsx` | ✅ | Экран выбора портала |
| `App.tsx` | ✅ | Роутер с lazy loading, AuthGuard, assignments, mobile entry `/m` |
| `Sidebar.tsx` | ✅ | Фильтрация пунктов по ролям + бейдж уведомлений |
| `useAuthGuard.ts` | ✅ | Проверка доступа к порталам |
| `NotificationsPage.tsx` | ✅ | Центр уведомлений с SSE |
| `MobileAssignmentsPage.tsx` | ✅ | Mobile entry point `/m` |

**Не применено** (требует решения):
- Отдельные `portal-*/App.tsx` с изолированными sidebars (патч предлагает разделение, текущая реализация — единый App)
- `Portal*Controller.java` в пакетах `portal.*` (текущие контроллеры в `core.*`, `assignments.*`)

---

## Файлы патчей

```
patches_gia_full/
├── README.md                     # Обзор рефакторинга
├── APPLIED_DIFF.patch            # Diff базовых сущностей
├── PORTAL_REFACTOR.patch         # Полный патч (портальные контроллеры + frontend Apps)
├── UserRole.java                 # Enum ролей
├── AppUser.java                  # Сущность пользователя
├── CustomUserDetails.java        # Spring Security UserDetails
├── SecurityConfig.java           # Конфигурация безопасности
├── AuthController.java           # Контроллер аутентификации
├── authStore_full.ts             # Zustand store (auth)
├── PortalSelector.tsx            # Компонент выбора портала
└── App.tsx                       # Базовый роутер (устарел относительно текущего)
```

---

## Пошаговая миграция

### Шаг 0: Бэкап

```bash
git branch refactor-portals-backup
git checkout refactor-portals-backup
```

### Шаг 1: База данных

SQL-миграция уже применена через Flyway (V32–V34). Проверьте таблицы:

```sql
\dt user_roles
\d app_user
```

Должны быть:
- `user_roles` (user_id, role)
- `app_user` с колонками `department_id`, `study_group_id`, `primary_portal`

### Шаг 2: Backend (если применяете на чистую систему)

```bash
# Копировать файлы
patches_gia_full/UserRole.java        → backend/src/main/java/.../auth/domain/enums/UserRole.java
patches_gia_full/AppUser.java         → backend/src/main/java/.../auth/domain/entity/AppUser.java
patches_gia_full/CustomUserDetails.java → backend/src/main/java/.../auth/infrastructure/security/CustomUserDetails.java
patches_gia_full/SecurityConfig.java  → backend/src/main/java/.../auth/infrastructure/config/SecurityConfig.java
patches_gia_full/AuthController.java  → backend/src/main/java/.../auth/web/AuthController.java
```

**Важно:** Текущий `AuthController` использует `JwtService`, а патч — `JwtUtil`.  
При ручном слиянии используйте `JwtService`.

### Шаг 3: UserDetailsServiceImpl

Найдите `UserDetailsServiceImpl.java` и замените возвращаемый тип:

```java
// Было:
return new org.springframework.security.core.userdetails.User(...)

// Стало:
return new CustomUserDetails(user);
```

### Шаг 4: Frontend (если применяете на чистую систему)

```bash
patches_gia_full/authStore_full.ts    → frontend-web/src/store/authStore.ts
patches_gia_full/PortalSelector.tsx   → frontend-web/src/components/PortalSelector.tsx
```

**Не копируйте** `patches_gia_full/App.tsx` — текущая версия содержит lazy loading, AuthGuard, assignments и mobile entry `/m`.

### Шаг 5: Проверка компиляции

```bash
cd backend && ./mvnw compile -q
cd ../frontend-web && npx tsc --noEmit
```

---

## Проверка после применения

1. **Вход в систему** → `POST /auth/login` возвращает:
   ```json
   {
     "accessToken": "...",
     "refreshToken": "...",
     "user": { "id": "...", "username": "...", "fullName": "..." },
     "roles": ["GEK_SECRETARY"],
     "availablePortals": ["gek", "methodist"],
     "primaryPortal": "gek"
   }
   ```

2. **PortalSelector** — отображает только доступные порталы
3. **AuthGuard** — при прямом переходе на `/groups` проверяет роль
4. **Sidebar** — фильтрует пункты по ролям
5. **SSE** — `/api/sse/notifications` подключается и присылает уведомления

---

## Риски и откат

| Риск | Решение |
|------|---------|
| Ошибки компиляции | Поискать `getRole()` → заменить на `getRoles()` |
| БД не мигрирована | Применить SQL из README.md патча |
| Frontend не компилируется | Проверить импорты в `authStore.ts` |

**Откат:**
```bash
git checkout main
```

---

## Расширения (не входит в базовый патч)

- [ ] Отдельные frontend-приложения для каждого портала (`vite build` с entry points)
- [ ] WebSocket вместо SSE для голосования
- [ ] Деканат: приказы, движение контингента
- [ ] Кафедра: нагрузка, дисциплины
- [ ] Методист: импорт из Excel/CSV
- [ ] Expo-приложение (см. `PROMPT_KIMICODE_MOBILE_APP.md`)

---

*Обновлено: 2026-05-18*
