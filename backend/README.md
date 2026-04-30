# T-Loyalty Backend Core

Backend Core для хакатонного проекта T-Loyalty Hub «Моя выгода» — единого раздела лояльности банка.

Сервис читает тестовые данные из CSV, собирает профиль клиента, счета, программы лояльности, историю выплат, аналитику, простой прогноз, персональные партнёрские предложения и продуктовые рекомендации.

## Архитектура


Проект разделён на слои, каждый из которых отвечает за отдельную область ответственности:

- `api/routes` — определяет HTTP endpoints FastAPI, принимает и валидирует запросы, формирует ответы.
- `schemas` — Pydantic схемы для валидации входных данных и формирования структурированных ответов API, обеспечивают автогенерацию OpenAPI.
- `models` — внутренние доменные модели, отражающие бизнес-сущности и их связи, используются для передачи данных между слоями приложения.
- `repositories` — слой доступа к данным: отвечает за чтение, парсинг и агрегацию информации из CSV-файлов, абстрагирует источник данных от остального приложения.
- `services` — реализует бизнес-логику: агрегирует данные из репозиториев, выполняет аналитику, расчёты прогнозов, формирует персональные офферы,рекомендации, AI-инсайты и собирает итоговый дашборд.
- `core` — содержит конфигурацию приложения, обработку ошибок, вспомогательные утилиты и базовые настройки, необходимые для работы всех остальных слоёв.

## Структура папок

```text
backend/
  app/
    main.py
    core/
    models/
    schemas/
    repositories/
    services/
    api/
      routes/
  data/
    Users.csv
    Accounts.csv
    LoyaltyPrograms.csv
    LoyaltyHistory.csv
    Offers.csv
  tests/
  README.md
  requirements.txt
  .env.example
```

## Локальный запуск

Linux/macOS:

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

Windows CMD:

```bat
cd backend
python -m venv .venv
.venv\Scripts\activate.bat
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Проверочные URL:

```text
http://127.0.0.1:8000/api/health
http://127.0.0.1:8000/docs
http://127.0.0.1:8000/openapi.json
```

## Docker

Backend входит в общий Docker Compose из корня проекта:

```bash
cd ..
docker compose up --build
```

## CSV-файлы

По умолчанию сервис читает CSV из папки `backend/data/`.

Ожидаемые файлы:

- `Users.csv`
- `Accounts.csv`
- `LoyaltyPrograms.csv`
- `LoyaltyHistory.csv`
- `Offers.csv`

Путь можно переопределить через `.env`:

```bash
cp .env.example .env
```

## Endpoints

- `GET /api/health` — проверка работоспособности сервиса (healthcheck), возвращает статус и имя сервиса.
- `GET /api/users` — список всех пользователей с краткой информацией для выбора профиля.
- `GET /api/users/{user_id}` — подробная информация о пользователе по его id.
- `GET /api/users/{user_id}/loyalty/summary` — агрегированная сводка по лояльности пользователя: балансы, суммы выплат, количество операций по программам.
- `GET /api/users/{user_id}/loyalty/analytics` — аналитика по месяцам и программам.
- `GET /api/users/{user_id}/loyalty/forecast` — прогноз выгоды на 30 дней.
- `GET /api/users/{user_id}/offers` — персональные офферы и акции для пользователя с учетом его финансового сегмента.
- `GET /api/users/{user_id}/cross-sell` — рекомендации продуктов экосистемы.
- `GET /api/users/{user_id}/gamification` — уровень пользователя, бейджи и персональные челленджи.
- `GET /api/users/{user_id}/ai-insights` — AI-инсайты: объяснения, советы, прогнозы и рекомендации на основе данных пользователя.
- `GET /api/users/{user_id}/missed-benefit` — оценка потенциальной недополученной выгоды (сколько пользователь мог бы получить дополнительно).
- `GET /api/users/{user_id}/dashboard` — полный дашборд пользователя.
- `GET /api/demo/profiles` — готовые профили пользователей для демонстрации.

## Логика summary

`summary` связывает данные по цепочке:

```text
Users -> Accounts -> LoyaltyPrograms -> LoyaltyHistory
```

Для каждого счёта считается:

- название программы лояльности;
- валюта cashback;
- текущий баланс;
- сумма всех выплат по счёту;
- количество операций выплат.

`totals_by_currency` группирует `cashback_amount` по валюте программы лояльности. `last_payout_date` — самая поздняя дата выплаты по всем счетам пользователя. Если истории выплат нет, массивы с начислениями остаются пустыми, счётчики равны нулю, `last_payout_date` возвращается как `null`.

## Логика аналитики

`monthly_dynamics` группирует выплаты по месяцу `YYYY-MM` и валюте.

`program_breakdown` показывает вклад каждой программы в общую сумму выплат. `share_percent` считается от общей суммы всех программ без конвертации валют.

`best_program` — программа с максимальной суммой `cashback_amount`.

`average_monthly_cashback` считает среднее месячное значение для каждой валюты по месяцам, где в этой валюте были выплаты.

## Логика прогноза

Прогноз использует метод `average_last_3_months`:

- берутся последние 3 месяца истории пользователя;
- выплаты группируются по валюте;
- для каждой валюты считается среднее месячное значение;
- месяцы без выплат в конкретной валюте учитываются как `0` внутри выбранного периода.

Уровень:

- меньше 2 месяцев истории — `low`;
- 2-3 месяца истории — `medium`;
- больше 3 месяцев истории — `high`.

ML-модель не используется.

### Перекрестные рекомендации

`GET /api/users/{user_id}/cross-sell` возвращает топ 5 рекомендаций продуктов экосистемы.

Рекомендации считаются подсчет очков 0-100 на основе правил. Учитываются:

- `financial_segment`: `LOW`, `MEDIUM`, `HIGH`;
- количество счетов;
- количество операций выплат;
- доминирующая валюта cashback;
- лучшая программа из analytics;
- confidence прогноза;
- наличие или отсутствие конкретной программы у пользователя.

Базовые продуктовые наборы различаются по сегментам. Например, для `LOW` приоритетнее простые продукты и партнёрский cashback, для `HIGH` — Premium, Investments, Business и travel benefits.

### Геймификация

`GET /api/users/{user_id}/gamification` возвращает:

- уровень пользователя: Bronze, Silver, Gold, Black Diamond;
- бейджи за активность и состав программ;
- 3-5 персональных челленджей.

### AI инсайты

`GET /api/users/{user_id}/ai-insights` имитирует персонального ассистента, но не использует внешний LLM.

Это deterministic `rule_based_ai_insights`:

- нет внешних моделей;
- нет API-ключей;
- каждый insight содержит `reason` и `evidence`;
- инсайты строятся только на данных из CSV и результатах локальных сервисов.

Типы инсайтов: `optimization`, `explanation`, `forecast`, `cross_sell`, `risk`, `gamification`.

### Упущенная выгода

`GET /api/users/{user_id}/missed-benefit` оценивает потенциальный подъем методом `average_monthly_cashback_uplift`.

Рубли, мили и Bravo points не смешиваются и не конвертируются.

### Дашборд

`dashboard_score` входит в `GET /api/users/{user_id}/dashboard`.

 Он показывает, насколько активно пользователь использует раздел выгоды:

- `activity` — до 35 баллов;
- `loyalty_diversity` — до 20 баллов;
- `forecast_confidence` — до 15 баллов;
- `offers_relevance` — до 15 баллов;
- `ecosystem_fit` — до 15 баллов.

Статусы: `starting`, `growing`, `strong`, `top`.

## Как запустить тесты

Из папки `backend`:

```bash
cd backend
pytest
```

Расширенный запуск:

```bash
pytest -v
```

Проверка импортов:

```bash
python -m compileall app
```

Запуск отдельных групп тестов:

```bash
pytest tests/test_health.py -v
pytest tests/test_users.py -v
pytest tests/test_loyalty_summary.py -v
pytest tests/test_loyalty_analytics.py -v
pytest tests/test_offers.py -v
pytest tests/test_forecast.py -v
pytest tests/test_cross_sell.py -v
pytest tests/test_gamification.py -v
pytest tests/test_ai_insights.py -v
pytest tests/test_missed_benefit.py -v
pytest tests/test_dashboard.py -v
pytest tests/test_demo_profiles.py -v
pytest tests/test_api_smoke.py -v
pytest tests/test_openapi.py -v
pytest tests/test_edge_cases.py -v
```

Smoke-проверка через `curl` после запуска `uvicorn app.main:app --reload`:

```bash
curl http://127.0.0.1:8000/api/health
curl http://127.0.0.1:8000/api/users
curl http://127.0.0.1:8000/api/demo/profiles
```

Если в окружении установлен `ruff`, можно дополнительно выполнить:

```bash
ruff check app tests
```
