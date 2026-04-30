# T-Loyalty Frontend

Веб-клиент для хакатонного проекта T-Loyalty Hub / «Моя выгода». Приложение реализует единый раздел лояльности банка, где пользователь видит всю свою выгоду: кэшбэк (рубли), мили, баллы «Браво», персональные офферы, прогноз начислений, рекомендации, AI-инсайты в едином интерфейсе.

## Стек

- React
- TypeScript
- Vite
- Tailwind CSS
- Recharts
- lucide-react
- clsx
- framer-motion

## Структура

```text
frontend/
  src/
    app/
    api/
    components/
      layout/
      ui/
      charts/
      loyalty/
    pages/
    hooks/
    lib/
    styles/
  .env.example
  package.json
  README.md
```

## Как запустить backend

Из корня проекта:

```bash
cd backend
uvicorn app.main:app --reload
```

Backend по умолчанию должен быть доступен на:

```text
http://127.0.0.1:8000
```

Если порт `8000` занят, запустите backend на другом порту и обновите `VITE_API_BASE_URL` в frontend `.env`.

## Как запустить frontend

Из корня проекта:

```bash
cd frontend
npm install
npm run dev
```

Vite покажет локальный URL, обычно:

```text
http://127.0.0.1:5173
```

## .env

Создайте `.env` из примера:

```bash
cp .env.example .env
```

```env
VITE_API_BASE_URL=http://127.0.0.1:8000
```

Для local dev используйте прямой backend URL:

```env
VITE_API_BASE_URL=http://127.0.0.1:8000
```

Для Docker/prod build используется относительный URL через nginx proxy:

```env
VITE_API_BASE_URL=/api
```

Если переменная не задана, frontend использует безопасный default `/api`.

## Основные страницы

- `/` — выбор демо-профиля.
- `/users/:userId/dashboard` — главный дашборд «Моя выгода».
- `/users/:userId/analytics` — графики и аналитика.
- `/users/:userId/offers` — персональные офферы.
- `/users/:userId/assistant` — AI ассистент.
- `/users/:userId/gamification` — «Путь выгоды».

## API endpoints

Frontend использует существующий backend:

- `GET /api/demo/profiles`
- `GET /api/users/{user_id}/dashboard`
- `GET /api/users/{user_id}/loyalty/analytics`
- `GET /api/users/{user_id}/offers`
- `GET /api/users/{user_id}/ai-insights`
- `GET /api/users/{user_id}/gamification`
- `GET /api/users/{user_id}/cross-sell`
- `GET /api/users/{user_id}/missed-benefit`

## Production build

```bash
npm run build
npm run preview
```

## Docker

Из корня проекта:

```bash
docker compose up --build
```

Frontend собирается multi-stage Dockerfile: `node:20-alpine` выполняет `npm ci` и `npm run build`, затем `nginx:alpine` отдаёт `dist`.

Nginx:

- отдаёт React SPA;
- проксирует `/api` в backend service `backend:8000`;
- проксирует `/docs` и `/openapi.json`;
- поддерживает refresh на nested routes через `try_files`.

Проверка:

```bash
curl http://localhost:3000/api/health
```