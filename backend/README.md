# T-Loyalty Backend Core

Backend Core для хакатонного проекта T-Loyalty Hub / «Моя выгода» — единого раздела лояльности банка для будущих web- и Android-клиентов.

Сервис читает тестовые данные из CSV, собирает профиль клиента, счета, программы лояльности, историю выплат, аналитику, простой прогноз, персональные партнёрские предложения и продуктовые rule-based рекомендации.

## Архитектура

Проект разделён на слои:

- `api/routes` — HTTP endpoints FastAPI без сложных расчётов.
- `schemas` — Pydantic v2 схемы ответов API.
- `models` — внутренние доменные модели.
- `repositories` — чтение и парсинг CSV-файлов.
- `services` — бизнес-логика, агрегации, аналитика, прогноз, cross-sell, gamification, insights и dashboard.
- `core` — конфигурация и обработка ошибок.

Роутеры вызывают сервисы, сервисы используют репозитории. Поэтому CSV-репозитории можно заменить на PostgreSQL-репозитории без переписывания бизнес-логики и API.

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

## Как запустить локально

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

```env
DATA_DIR=data
```

Если путь относительный, он считается относительно папки `backend`.

`financial_segment` нормализуется при чтении CSV. Допустимые значения: `LOW`, `MEDIUM`, `HIGH`; legacy-значение `MIDDLE` автоматически мапится в `MEDIUM`.

## Endpoints

- `GET /api/health` — healthcheck.
- `GET /api/users` — список пользователей с preview-полями.
- `GET /api/users/{user_id}` — пользователь по id.
- `GET /api/users/{user_id}/loyalty/summary` — summary лояльности.
- `GET /api/users/{user_id}/loyalty/analytics` — аналитика по месяцам и программам.
- `GET /api/users/{user_id}/loyalty/forecast` — прогноз выгоды на 30 дней.
- `GET /api/users/{user_id}/offers` — персональные офферы по financial segment.
- `GET /api/users/{user_id}/cross-sell` — рекомендации продуктов экосистемы.
- `GET /api/users/{user_id}/gamification` — уровень, бейджи и персональные челленджи.
- `GET /api/users/{user_id}/ai-insights` — rule-based explainable инсайты.
- `GET /api/users/{user_id}/missed-benefit` — приблизительный potential uplift.
- `GET /api/users/{user_id}/dashboard` — полный dashboard одним запросом.
- `GET /api/demo/profiles` — сценарии пользователей для демонстрации на защите.

Если пользователь не найден, API возвращает:

```json
{
  "detail": "User not found"
}
```

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

Уровень confidence:

- меньше 2 месяцев истории — `low`;
- 2-3 месяца истории — `medium`;
- больше 3 месяцев истории — `high`.

ML-модель не используется. Метод намеренно простой и explainable.

## Business Polish слой

Business Polish добавляет продуктовые блоки поверх существующих `summary`, `analytics`, `forecast` и `offers`. Новые сервисы не читают CSV напрямую и не дублируют базовые расчёты: они переиспользуют уже готовые сервисы и интерпретируют их результаты.

### Cross-sell recommendations

`GET /api/users/{user_id}/cross-sell` возвращает top-5 рекомендаций продуктов экосистемы.

Рекомендации считаются rule-based scoring 0-100. Учитываются:

- `financial_segment`: `LOW`, `MEDIUM`, `HIGH`;
- количество счетов;
- количество операций выплат;
- доминирующая валюта cashback;
- лучшая программа из analytics;
- confidence прогноза;
- наличие или отсутствие конкретной программы у пользователя.

Базовые продуктовые наборы различаются по сегментам. Например, для `LOW` приоритетнее простые продукты и партнёрский cashback, для `HIGH` — Premium, Investments, Business и travel benefits.

### Gamification

`GET /api/users/{user_id}/gamification` возвращает:

- уровень пользователя: Bronze, Silver, Gold, Black Diamond;
- бейджи за активность и состав программ;
- 3-5 персональных челленджей.

Для хакатонной демонстрации `total_cashback_value` используется как условные loyalty points без конвертации валют. Это не финансовый баланс и не реальная программа начисления баллов.

### AI insights

`GET /api/users/{user_id}/ai-insights` имитирует персонального ассистента, но не использует внешний LLM.

Это deterministic `rule_based_ai_insights`:

- нет OpenAI, Claude, GigaChat или других внешних моделей;
- нет API-ключей;
- каждый insight содержит `reason` и `evidence`;
- инсайты строятся только на данных из CSV и результатах локальных сервисов.

Типы инсайтов: `optimization`, `explanation`, `forecast`, `cross_sell`, `risk`, `gamification`.

### Missed benefit

`GET /api/users/{user_id}/missed-benefit` оценивает потенциальный uplift методом `average_monthly_cashback_uplift`.

Для каждой валюты отдельно:

```text
potential_extra_amount = average_monthly_amount * 0.15
```

Рубли, мили и Bravo points не смешиваются и не конвертируются. Это приблизительная продуктовая оценка, а не финансовая гарантия.

### Dashboard score

`dashboard_score` входит в `GET /api/users/{user_id}/dashboard`.

Это engagement/loyalty score, а не кредитный скоринг. Он показывает, насколько активно пользователь использует раздел выгоды:

- `activity` — до 35 баллов;
- `loyalty_diversity` — до 20 баллов;
- `forecast_confidence` — до 15 баллов;
- `offers_relevance` — до 15 баллов;
- `ecosystem_fit` — до 15 баллов.

Статусы: `starting`, `growing`, `strong`, `top`.

### Demo profiles

`GET /api/demo/profiles` автоматически выбирает пользователей для защиты:

- лучший `HIGH` по сумме выплат;
- лучший `MEDIUM`;
- лучший `LOW`;
- пользователь с максимальными милями;
- пользователь с несколькими программами;
- пользователь с наиболее красивым forecast/confidence.

`user_id` не хардкодятся. Если данных мало, endpoint возвращает доступные сценарии без ошибки.

### Dashboard

`GET /api/users/{user_id}/dashboard` теперь объединяет:

- user preview;
- loyalty summary;
- analytics;
- forecast;
- offers;
- cross_sell;
- gamification;
- ai_insights;
- missed_benefit;
- dashboard_score.

## Упрощения

- Валюты cashback не конвертируются друг в друга: рубли, мили и баллы считаются в исходных единицах.
- `share_percent` в аналитике считается по общей сумме без конвертации валют.
- Прогноз не является ML-моделью, используется explainable `average_last_3_months`.
- AI insights не используют внешний LLM, это rule-based explainable engine.
- Dashboard score не является кредитным скорингом и отражает только engagement/loyalty активность.
- Missed benefit считается приблизительно как 15% uplift от среднего месячного cashback по каждой валюте.
- Cross-sell основан на сегменте, программах лояльности и истории выплат.
- Demo profiles нужны для удобной защиты и выбора сценария в будущем frontend.
- Данные берутся из CSV, но архитектура готова к замене CSV-репозиториев на БД.

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
