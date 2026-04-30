# T-Loyalty Android


Демо-приложение для Android для хакатонного проекта T-Loyalty Hub «Моя выгода». Мобильное приложение реализует сценарий выбора демо-пользователя и отображения единого раздела лояльности банка. Пользователь видит свой дашборд, аналитику по программам лояльности, персональные офферы, AI-инсайты, «Путь выгоды» и рекомендации.

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

## Debug APK

```bash
cd mobile
./gradlew assembleDebug
```

Windows:

```bat
gradlew.bat assembleDebug
```

Ожидаемый путь после debug-сборки:

```text
mobile/app/build/outputs/apk/debug/app-debug.apk
```

Рекомендуемый путь для демо-пакета в репозитории:

```text
docs/apk/t-loyalty-debug.apk
```

APK не требуется для Docker/CI backend+frontend и не собирается в основном CI workflow.
