# T-Loyalty Frontend

Web-приложение для хакатонного проекта T-Loyalty Hub / «Моя выгода». Показывает единый раздел лояльности банка: cashback, мили, Браво, офферы, прогноз, cross-sell рекомендации, AI insights и игровой путь выгоды.

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

## Основные страницы

- `/` — выбор демо-профиля.
- `/users/:userId/dashboard` — главный dashboard «Моя выгода».
- `/users/:userId/analytics` — графики и аналитика.
- `/users/:userId/offers` — персональные офферы.
- `/users/:userId/assistant` — rule-based AI Loyalty Assistant.
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

Главный dashboard использует `GET /api/users/{user_id}/dashboard`, чтобы не делать много запросов.

## Production build

```bash
npm run build
npm run preview
```

## Troubleshooting

### Backend not available

Если frontend показывает ошибку подключения:

```text
Не удалось подключиться к backend
```

Проверьте, что FastAPI запущен:

```bash
cd backend
uvicorn app.main:app --reload
curl http://127.0.0.1:8000/api/health
```

### CORS

Backend уже настроен с CORS middleware. Если меняете порт или host, проверьте `VITE_API_BASE_URL` и backend CORS settings.

### Wrong VITE_API_BASE_URL

Если backend запущен не на `8000`, укажите правильный URL:

```env
VITE_API_BASE_URL=http://127.0.0.1:8010
```

После изменения `.env` перезапустите `npm run dev`.
