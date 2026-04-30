
## Данные
Accounts: account_id, user_id, loyalty_program_id, current_balance.
LoyaltyHistory: transaction_id, account_id, cashback_amount, payout_date.
LoyaltyPrograms: loyalty_program_id, loyalty_program_name, cashback_currency.
Offers: partner_id, partner_name, short_description, logo_url, brand_color_hex, cashback_percent, financial_segment.
Users: id, email, phone_number, full_name, financial_segment.


## Главный экран — «Моя выгода» (Dashboard)

### Блок «Финансы»

**Что показываем:**

- Общая сумма кэшбэка:
    - рубли
    - баллы
    - мили
- - опционально: «эквивалент в рублях»

**Логика расчёта:**

- Берём `Accounts` пользователя
- Для каждого `account_id` (Их может быть несколько):
    - находим `loyalty_program_id`
    - определяем валюту (`LoyaltyPrograms.cashback_currency`)
- Суммируем `current_balance` по валютам:
    - RUB отдельно
    - BRAVO отдельно
    - MILES отдельно

(опционально)

- Конверсия:
    - Не сказано, допустим: 1 миля = 1 рубль, 1 балл = 1 рубль

---

### Блок «По программам»

**Что показываем:**

- 3 карточки:
    - Black
    - Платинум
    - All Airlines
- На карточке:
    - текущий баланс
    - тип валюты
- Внутри:
	- Описание программы
	- История с фильтрацией только по этой программе

**Логика:**
- Группировка `Accounts` по `loyalty_program_id`
- Маппинг на название программы из `LoyaltyPrograms`

---

### Блок «Прогноз»

**Что показываем:**

- «Ожидаемый кэшбэк в следующем периоде: X»
- Можно разбить по программам

**Логика (простая, MVP):**

- Берём `LoyaltyHistory` по пользователю
- Считаем:
    - средний месячный кэшбэк:
        - группировка по месяцам (`payout_date`)
        - среднее значение
- Прогноз = среднее за прошлые месяцы

---

### 2.4. Блок «История (preview)»

**Что показываем:**
- Последние 5–10 начислений
- Поля:
    - дата (`payout_date`)
    - сумма (`cashback_amount`)
    - программа

**Логика:**

- Join:
    - `LoyaltyHistory` → `Accounts` → `LoyaltyPrograms`
- Сортировка по дате (desc)

---

### Блок «Предложения»

**Что показываем:**

- Карточки офферов:
    - `partner_name`
    - `% cashback`
    - описание
    - цвет/логотип

**Логика:**

- Фильтр:
    - `Offers.financial_segment == Users.financial_segment`
- Если пусто — fallback на общий список

---

## Экран «История» (детализация)

**Что есть:**

- Полный список начислений

**Функции:**

- Фильтр:
    - по программе
    - по периоду
- Сортировка

**Логика:**

- Те же данные, что в preview, но без ограничения
- Фильтрация на уровне backend или frontend

---

## Экран программы

**Что есть:**

- Детали конкретной программы:
    - баланс
    - история только по ней

**Логика:**
- Фильтр по `loyalty_program_id`

---

## API 

- `/users`
- `/dashboard?user_id=`
    - возвращает:
        - summary
        - balances by program
        - forecast
        - recent history
        - offers
- `/history?user_id=&filters=`

---

## Ключевая логика (сводка)

1. **Агрегация**
    - `Accounts.current_balance` → текущая выгода
2. **История**
    - `LoyaltyHistory` → фактические выплаты
3. **Программы**
    - `loyalty_program_id` → тип кэшбэка
4. **Персонализация**
    - `Users.financial_segment` → фильтр офферов
5. **Прогноз**
    - среднее по прошлым выплатам