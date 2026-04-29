# T-Loyalty Android

Android demo app для хакатонного проекта T-Loyalty Hub / "Моя выгода".

## Описание

Приложение показывает мобильный сценарий выбора демо-пользователя и раздела "Моя выгода": dashboard, аналитику лояльности, персональные офферы, AI insights, "Путь выгоды" и cross-sell рекомендации. Данные загружаются из локального FastAPI backend.

## Стек

- Kotlin
- Jetpack Compose
- Material 3
- Compose Navigation
- ViewModel, StateFlow, Coroutines
- Retrofit, OkHttp logging interceptor
- Gson
- Coil для `logo_url`
- Gradle Kotlin DSL
- minSdk 26, compileSdk/targetSdk 36

## Архитектура

Поток данных:

```text
Compose Screen -> ViewModel -> LoyaltyRepository -> Retrofit LoyaltyApi -> FastAPI backend
```

DI сделан вручную через `AppContainer`, без Hilt. DTO остаются в data layer, UI получает domain models.

## Как запустить backend

Из корня репозитория:

```bash
cd backend
uvicorn app.main:app --reload
```

Проверка backend:

```bash
curl http://127.0.0.1:8000/api/health
curl http://127.0.0.1:8000/api/demo/profiles
```

## Как открыть проект в Android Studio

```text
Open -> mobile
```

Если Gradle не видит Android SDK в терминале, задайте `ANDROID_HOME` или откройте проект из Android Studio.

## Base URL

Base URL задаётся в одном месте:

```kotlin
core/config/ApiConfig.kt
```

По умолчанию:

```text
http://10.0.2.2:8000
```

Для Android Emulator используйте:

```text
http://10.0.2.2:8000
```

Для реального устройства используйте IP компьютера в локальной сети:

```text
http://<LAN_IP>:8000
```

`127.0.0.1` внутри Android Emulator указывает на сам эмулятор, а не на компьютер.

## Cleartext HTTP

Backend локальный и работает по HTTP. В `res/xml/network_security_config.xml` разрешён cleartext traffic только для локальной разработки:

- `10.0.2.2`
- `localhost`
- `127.0.0.1`
- примеры LAN IP: `192.168.0.100`, `192.168.1.100`

Для реального устройства добавьте свой LAN IP в network security config или используйте HTTPS. Глобальный `usesCleartextTraffic=true` намеренно не включён.

## Основные экраны

- Demo profiles: `GET /api/demo/profiles`
- Dashboard: `GET /api/users/{user_id}/dashboard`
- Analytics: `GET /api/users/{user_id}/loyalty/analytics`
- Offers: `GET /api/users/{user_id}/offers`
- Assistant: `GET /api/users/{user_id}/ai-insights`
- Gamification: `GET /api/users/{user_id}/gamification`

Dashboard использует один агрегированный endpoint и не делает лишние запросы за блоками, которые уже есть в ответе.

## Troubleshooting

### Backend unavailable

Проверьте, что FastAPI запущен:

```bash
curl http://127.0.0.1:8000/api/health
```

### Wrong baseUrl

Для Android Emulator нужен `http://10.0.2.2:8000`, а не `http://127.0.0.1:8000`.

### Cleartext HTTP blocked

Проверьте `app/src/main/res/xml/network_security_config.xml`. Для реального устройства добавьте IP компьютера в локальной сети.

### Emulator cannot use 127.0.0.1

`127.0.0.1` внутри эмулятора означает сам Android Emulator. Хост-машина доступна как `10.0.2.2`.

## Debug APK

```bash
cd mobile
./gradlew assembleDebug
```

Windows:

```bat
gradlew.bat assembleDebug
```
