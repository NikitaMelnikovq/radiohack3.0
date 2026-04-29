# T-Loyalty Hub / Моя выгода

## Описание

T-Loyalty Hub / «Моя выгода» — хакатонный проект единого раздела лояльности банка. Он показывает выгоду по рублям, милям и Браво, аналитику, прогноз, персональные офферы, cross-sell рекомендации, rule-based AI insights и gamification.

## Архитектура

Стек проекта:

- Backend: FastAPI.
- Frontend: React + Vite + TypeScript.
- Mobile: Android + Kotlin.
- Data layer: CSV-файлы в `backend/data`.

Поток данных:

```text
User
 -> Frontend React / Android
 -> Backend FastAPI
 -> CSV repositories
 -> Business services
 -> Dashboard API
```

## Компоненты

- `backend/` — FastAPI API и бизнес-сервисы.
- `frontend/` — React web client.
- `mobile/` — Android/Kotlin client, не контейнеризируется.
- `docs/` — инструкции, screenshots и место для APK.
- `docker-compose.yml` — запуск web + backend одной командой.

## Требования

- Docker и Docker Compose plugin.
- Node.js 20, если нужен local frontend dev.
- Python 3.11, если нужен local backend dev.
- Android Studio, если нужен mobile build.

## Быстрый запуск через Docker

`.env` опционален для запуска, но рекомендуется для явной конфигурации:

```bash
cp .env.example .env
docker compose up --build
```

Открыть:

```text
Frontend:       http://localhost:3000
Backend health: http://localhost:3000/api/health
Swagger:        http://localhost:3000/docs
OpenAPI:        http://localhost:3000/openapi.json
```

Остановка:

```bash
docker compose down
```

Логи:

```bash
docker compose logs -f backend
docker compose logs -f frontend
```

Проверка состояния:

```bash
docker compose ps
```

## Локальный запуск без Docker

Backend:

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Windows PowerShell:

```powershell
cd backend
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Frontend:

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Для local dev frontend использует:

```env
VITE_API_BASE_URL=http://127.0.0.1:8000
```

Для Docker/prod используется относительный путь:

```env
VITE_API_BASE_URL=/api
```

Если переменная не задана, frontend безопасно использует default `/api`.

## API endpoints

- `GET /api/health`
- `GET /api/demo/profiles`
- `GET /api/users`
- `GET /api/users/{user_id}/dashboard`
- `GET /api/users/{user_id}/loyalty/summary`
- `GET /api/users/{user_id}/loyalty/analytics`
- `GET /api/users/{user_id}/loyalty/forecast`
- `GET /api/users/{user_id}/offers`
- `GET /api/users/{user_id}/cross-sell`
- `GET /api/users/{user_id}/ai-insights`
- `GET /api/users/{user_id}/gamification`
- `GET /api/users/{user_id}/missed-benefit`

## Фичи

- выбор демо-пользователя;
- выгода по валютам;
- аналитика по месяцам и программам;
- explainable forecast;
- офферы по `financial_segment`;
- cross-sell рекомендации;
- rule-based AI insights;
- gamification;
- dashboard score;
- missed benefit.

## Упрощения

- данные читаются из CSV;
- валюты не конвертируются;
- прогноз использует explainable average, не ML;
- AI insights rule-based, без внешнего LLM;
- dashboard score — engagement/loyalty score, не кредитный скоринг;
- офферы не активируются реально.

## Mobile / APK

Android-клиент находится в `mobile/`.

Debug APK после сборки:

```text
mobile/app/build/outputs/apk/debug/app-debug.apk
```

Рекомендуемый путь для демо-пакета:

```text
docs/apk/t-loyalty-debug.apk
```

Команда сборки:

```bash
cd mobile
./gradlew assembleDebug
```

Windows:

```bat
gradlew.bat assembleDebug
```

На момент упаковки APK в репозитории не найден. Для демо можно собрать `app-debug.apk` и положить копию в `docs/apk/t-loyalty-debug.apk`.

## Тесты

Backend:

```bash
cd backend
python -m compileall app
pytest -v
```

Frontend:

```bash
cd frontend
npm run build
```

Docker:

```bash
docker compose build
```

## CI/CD

`.github/workflows/ci.yml` запускается на `push` в `main`/`master` и на `pull_request`.

Проверки:

- backend: `pip install`, `python -m compileall app`, `pytest -v`;
- frontend: `npm ci` или `npm install`, `npm run build`;
- docker: `docker compose build`.

`.github/workflows/deploy.example.yml` — безопасный шаблон деплоя на сервер через SSH. Он запускается вручную через `workflow_dispatch`. Если нужен автодеплой из `main`, раскомментируйте блок `push` в workflow.

Secrets для деплоя:

- `SERVER_HOST`
- `SERVER_USER`
- `SERVER_SSH_KEY`
- `SERVER_PORT`
- `DEPLOY_PATH`

Third-party deploy action не используется: workflow вызывает `ssh` из shell. Приватные ключи нельзя хранить в репозитории.

## Деплой на сервер через Docker Compose

Общий сценарий:

1. Подготовить сервер.
2. Установить Docker и Docker Compose plugin.
3. Склонировать репозиторий в `/opt/t-loyalty-hub`.
4. Создать `.env`.
5. Запустить `docker compose up -d --build`.
6. Настроить reverse proxy/Nginx, если нужен домен.
7. Открыть порт `3000` или проксировать на `80/443`.

Команды:

```bash
sudo mkdir -p /opt/t-loyalty-hub
sudo chown $USER:$USER /opt/t-loyalty-hub
cd /opt/t-loyalty-hub
git clone <repo-url> .
cp .env.example .env
docker compose up -d --build
```

Проверка:

```bash
docker compose ps
curl http://localhost:3000/api/health
```

Подробная инструкция: `docs/DEPLOYMENT.md`.

## Troubleshooting

- Frontend не видит backend: проверьте, что Docker/prod build использует `VITE_API_BASE_URL=/api`.
- `127.0.0.1:8000` в production build: пересоберите frontend через `docker compose build frontend`.
- CORS: для Docker frontend ходит через nginx same-origin `/api`; для local dev проверьте `CORS_ORIGINS`.
- Port already in use: освободите `3000` или измените mapping в `docker-compose.yml`.
- Docker daemon not running: запустите Docker и проверьте права пользователя.
- CSV files not found: в контейнере должен быть `DATA_DIR=/app/data`, CSV лежат в `backend/data`.
- Android Emulator: используйте backend URL `http://10.0.2.2:8000`, а не `127.0.0.1`.
