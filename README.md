# T-Loyalty Hub «Моя выгода»

## Описание

T-Loyalty Hub «Моя выгода» — это прототип единого раздела лояльности для клиентов банка, созданный в рамках хакатона. Проект объединяет все механики лояльности банка в одном месте и делает выгоды для пользователя прозрачными и наглядными.

## Команда

**RTF Wizards**
Мы студенты 3 курса ИРИТ-РТФ (УрФУ).

## Стек проекта

- Backend: FastAPI.
- Frontend: React + Vite + TypeScript.
- Mobile: Android + Kotlin.
- Data layer: CSV-файлы в `backend/data`.

## Компоненты

- `backend/` — серверная часть на FastAPI: реализует бизнес-логику, API, обработку данных лояльности, аналитику, прогнозы, персональные офферы и AI-инсайты.
- `frontend/` — веб-клиент на React + Vite: пользовательский интерфейс для просмотра выгод, аналитики, предложений и выбора демо-пользователя.
- `mobile/` — мобильное приложение на Android/Kotlin: отдельный клиент для доступа к разделу лояльности с мобильных устройств.
- `docs/` — документация по проекту.
- `docker-compose.yml` — конфигурация для одновременного запуска backend и frontend в Docker, обеспечивает быструю сборку.

## Требования

- Docker и Docker Compose plugin.
- Node.js 20.
- Python 3.11.
- Android Studio.

## Быстрый запуск (Docker)

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

## Локальный запуск

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

## Mobile (APK)

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

Секреты для деплоя: `SERVER_HOST`, `SERVER_USER`, `SERVER_SSH_KEY`, `SERVER_PORT`, `DEPLOY_PATH`.
