# Полный рефакторинг GIA_Mobile: модули и порталы

> Оценка времени: 8-10 дней
> Статус: файлы для пошагового применения

---

## Состав

| Файл | Назначение | Куда копировать |
|------|-----------|-----------------|
| `UserRole.java` | Новые роли + методы порталов | `backend/src/main/java/.../auth/domain/enums/` |
| `AppUser.java` | Множественные роли (Set), привязки | `backend/src/main/java/.../auth/domain/entity/` |
| `CustomUserDetails.java` | Spring Security authorities из Set<role> | `backend/src/main/java/.../auth/infrastructure/security/` |
| `SecurityConfig.java` | Портальная маршрутизация `/portal/*` | `backend/src/main/java/.../auth/infrastructure/config/` |
| `AuthController.java` | Возврат roles + portals + primaryPortal | `backend/src/main/java/.../auth/web/` |
| `authStore_full.ts` | Zustand store с мульти-ролями | `frontend-web/src/store/authStore.ts` |
| `PortalSelector.tsx` | Экран выбора портала после входа | `frontend-web/src/components/PortalSelector.tsx` |
| `App.tsx` | Главный роутер с порталами + PortalSelector | `frontend-web/src/App.tsx` |

---

## Пошаговая миграция

### Шаг 0: Бэкап

```bash
cd C:\Users\Валера\Desktop\GIA_Mobile
git branch refactor-portals
git checkout refactor-portals
```

---

### Шаг 1: Миграция базы данных (SQL)

Выполни в PostgreSQL:

```sql
-- 1. Создать таблицу user_roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- 2. Перенести существующие роли (одна роль → одна запись)
INSERT INTO user_roles (user_id, role)
SELECT id, role::text FROM app_user;

-- 3. Добавить новые колонки
ALTER TABLE app_user
    ADD COLUMN IF NOT EXISTS department_id UUID,
    ADD COLUMN IF NOT EXISTS study_group_id UUID,
    ADD COLUMN IF NOT EXISTS primary_portal VARCHAR(30);

-- 4. Удалить старую колонку role (после проверки!)
-- ALTER TABLE app_user DROP COLUMN IF EXISTS role;

-- 5. Обновить роли legacy → новые
UPDATE user_roles SET role = 'GEK_SECRETARY' WHERE role = 'SECRETARY';
UPDATE user_roles SET role = 'GEK_CHAIRMAN' WHERE role = 'CHAIRMAN';
UPDATE user_roles SET role = 'SYSTEM_ADMIN' WHERE role = 'ADMIN';

-- 6. Установить primary_portal
UPDATE app_user SET primary_portal = 'student'
WHERE id IN (SELECT user_id FROM user_roles WHERE role = 'STUDENT');

UPDATE app_user SET primary_portal = 'gek'
WHERE id IN (SELECT user_id FROM user_roles WHERE role IN ('GEK_SECRETARY', 'GEK_CHAIRMAN', 'GEK_MEMBER'));

UPDATE app_user SET primary_portal = 'methodist'
WHERE id IN (SELECT user_id FROM user_roles WHERE role = 'METHODIST');

UPDATE app_user SET primary_portal = 'admin'
WHERE id IN (SELECT user_id FROM user_roles WHERE role IN ('SYSTEM_ADMIN', 'UNIVERSITY_ADMIN'));
```

---

### Шаг 2: Backend — заменить файлы

```bash
# 1. UserRole.java
cp patches_gia_full/UserRole.java backend/src/main/java/com/spbutu/gia/auth/domain/enums/UserRole.java

# 2. AppUser.java (⚠️ СТАРЫЙ AppUser.java будет перезаписан — убедись, что нет custom-полей)
cp patches_gia_full/AppUser.java backend/src/main/java/com/spbutu/gia/auth/domain/entity/AppUser.java

# 3. CustomUserDetails.java (новый файл — заменяет старый UserDetailsServiceImpl логику)
cp patches_gia_full/CustomUserDetails.java backend/src/main/java/com/spbutu/gia/auth/infrastructure/security/CustomUserDetails.java

# 4. SecurityConfig.java
cp patches_gia_full/SecurityConfig.java backend/src/main/java/com/spbutu/gia/auth/infrastructure/config/SecurityConfig.java

# 5. AuthController.java (⚠️ СТАРЫЙ будет перезаписан)
cp patches_gia_full/AuthController.java backend/src/main/java/com/spbutu/gia/auth/web/AuthController.java
```

**Важно:** Если у `AppUser` или `AuthController` были дополнительные поля/методы — слей их вручную.

---

### Шаг 3: Обновить UserDetailsServiceImpl

Найди `UserDetailsServiceImpl.java` и замени возвращаемый тип:

```java
// Было:
return new org.springframework.security.core.userdetails.User(...)

// Стало:
return new CustomUserDetails(user);
```

---

### Шаг 4: Собрать backend

```bash
cd backend
mvn clean install -DskipTests
```

Если ошибки компиляции — скорее всего:
- Где-то ещё обращаются к `user.getRole()` (одна роль) — замени на `user.getRoles()`
- `UserRole` enum изменился — обнови все switch/case

---

### Шаг 5: Frontend — заменить файлы

```bash
# 1. Auth store
cp patches_gia_full/authStore_full.ts frontend-web/src/store/authStore.ts

# 2. PortalSelector (новый компонент)
cp patches_gia_full/PortalSelector.tsx frontend-web/src/components/PortalSelector.tsx

# 3. App.tsx (главный роутер)
cp patches_gia_full/App.tsx frontend-web/src/App.tsx
```

---

### Шаг 6: Обновить API клиент (axios)

В `frontend-web/src/api/axios.ts` или `client.ts` — убедись, что после логина:

```typescript
// Сохраняешь ВСЁ, что пришло с /auth/login:
const { accessToken, refreshToken, user, roles, availablePortals, primaryPortal } = response.data;

useAuthStore.getState().login(
  accessToken,
  refreshToken,
  user,
  roles,
  availablePortals,
  primaryPortal
);
```

---

### Шаг 7: Обновить Sidebar

Используй `Sidebar_new.tsx` из `patches_gia_quickfix/` — там уже группировка по порталам.

Скопируй:
```bash
cp patches_gia_quickfix/Sidebar.tsx frontend-web/src/components/layout/Sidebar.tsx
```

---

### Шаг 8: Создать страницы порталов (заглушки)

Создай папки под новые порталы:

```bash
mkdir frontend-web/src/pages/portals
mkdir frontend-web/src/pages/portals/admin
mkdir frontend-web/src/pages/portals/deanery
mkdir frontend-web/src/pages/portals/department
mkdir frontend-web/src/pages/portals/gek
mkdir frontend-web/src/pages/portals/methodist
mkdir frontend-web/src/pages/portals/student
```

Пример заглушки (`frontend-web/src/pages/portals/deanery/DeaneryOrdersPage.tsx`):

```tsx
import React from 'react';
export const DeaneryOrdersPage: React.FC = () => (
  <div className="p-8">
    <h1 className="text-2xl font-bold">Деканат — Приказы</h1>
    <p className="text-gray-500 mt-4">Страница в разработке</p>
  </div>
);
```

---

### Шаг 9: Перенести существующие контроллеры на `/portal/*`

Это самый долгий этап. Нужно:

1. Скопировать существующие контроллеры в новые пакеты `portal-*/`
2. Изменить `@RequestMapping` с `/meetings` → `/portal/gek/meetings`
3. Обновить `@PreAuthorize` роли

Пример для `GekMeetingController.java`:
```java
@RestController
@RequestMapping("/portal/gek/meetings")
@PreAuthorize("hasAnyRole('GEK_SECRETARY', 'GEK_CHAIRMAN')")
public class GekMeetingController { ... }
```

---

### Шаг 10: Собрать и тестировать

```bash
cd frontend-web
npm install  # если lucide-react не установлен
npm run dev
```

Проверь:
1. Вход → PortalSelector показывает доступные порталы
2. Выбор портала → редирект в нужный раздел
3. Роли работают — ADMIN видит всё, STUDENT только свой портал
4. `/portal/gek/meetings` без токена → 403

---

## Риски и откаты

| Риск | Решение |
|------|---------|
| Ошибки компиляции после замены AppUser | Поискать `getRole()` → заменить на `getRoles()` |
| БД миграция сломала данные | Бэкап перед шагом 1 |
| Frontend не компилируется | Проверить импорты в authStore.ts |
| Порталы не отображаются | Проверить `/auth/me` возвращает `availablePortals` |

**Откат:**
```bash
git checkout main  # или ветка до рефакторинга
```

---

## Далее (не входит в 8-10 дней)

- [ ] Отдельные frontend-приложения (vite build для каждого портала)
- [ ] WebSocket вместо SSE для голосования
- [ ] Деканат: приказы, движение контингента
- [ ] Кафедра: нагрузка, дисциплины, руководители ВКР
- [ ] Методист: импорт из Excel/CSV
- [ ] Студент: отдельное мобильное приложение (Expo)
- [ ] Docker Compose для production

---

*Создано: 2026-05-28*
