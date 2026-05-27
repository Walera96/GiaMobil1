# Frontend Web — ГИА СПбУТУИЭ

## Стек
- React 19 + TypeScript
- Vite
- Tailwind CSS
- TanStack Query (React Query)
- Zustand
- React Router v7
- Axios
- Lucide React (иконки)

## Установка и запуск

```bash
cd frontend-web
npm install
npm run dev
```

Приложение доступно по адресу `http://localhost:5173`.

## Переменные окружения

Скопируй `.env.example` в `.env` и при необходимости измени `VITE_API_URL`:

```bash
cp .env.example .env
```

## Структура

- `src/api/` — Axios instance и API-модули (auth, meetings, voting, protocols)
- `src/store/` — Zustand store (auth)
- `src/hooks/` — Кастомные хуки (useAuth, useMeetings, useVoting, useSse)
- `src/components/ui/` — Базовые UI-компоненты (Button, Card, Badge, Input, Modal, Toast)
- `src/components/layout/` — Layout (AppLayout, Sidebar, Header)
- `src/components/features/` — Фича-компоненты (MeetingCard, AgendaTable, VoteBadge и др.)
- `src/pages/` — Страницы приложения
- `src/types/` — TypeScript-интерфейсы
- `src/utils/` — Утилиты (форматтеры)
