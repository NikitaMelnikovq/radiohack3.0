# Deployment Guide

Данный документ содержит пошаговую инструкцию по развертыванию T-Loyalty Hub «Моя выгода» на сервере с использованием Docker Compose. Обратите внимание: мобильное приложение не входит в контейнеризацию и разворачивается отдельно.

## 1. Требования к серверу

- Linux VPS или bare metal сервер.
- 1 CPU / 1 GB RAM минимум для демо, лучше 2 GB RAM.
- Открытый порт `3000` или reverse proxy на `80/443`.
- Доступ по SSH.
- Git.
- Docker Engine и Docker Compose plugin.

## 2. Установка Docker

Ubuntu/Debian:

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl git
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo tee /etc/apt/keyrings/docker.asc > /dev/null
sudo chmod a+r /etc/apt/keyrings/docker.asc
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker $USER
```

После добавления пользователя в группу `docker` перелогиньтесь в SSH-сессию.

Проверка:

```bash
docker --version
docker compose version
```

## 3. Клонирование репозитория

```bash
sudo mkdir -p /opt/t-loyalty-hub
sudo chown $USER:$USER /opt/t-loyalty-hub
cd /opt/t-loyalty-hub
git clone <repo-url> .
```

## 4. Настройка .env

```bash
cd /opt/t-loyalty-hub
cp .env.example .env
```

Базовые значения для Docker:

```env
SERVICE_NAME=t-loyalty-backend
APP_TITLE=T-Loyalty Backend Core
APP_VERSION=0.1.0
API_PREFIX=/api
DATA_DIR=/app/data
CORS_ORIGINS=http://localhost:3000
VITE_API_BASE_URL=/api
```

Если используется домен, замените `CORS_ORIGINS` на внешний origin, например `https://loyalty.example.com`.

## 5. Первый запуск

```bash
cd /opt/t-loyalty-hub
docker compose up -d --build
```

Проверка:

```bash
docker compose ps
curl http://localhost:3000/api/health
curl http://localhost:3000/api/demo/profiles
```

Frontend будет доступен на:

```text
http://<server-ip>:3000
```

## 6. Обновление версии

```bash
cd /opt/t-loyalty-hub
git pull
docker compose up -d --build
docker image prune -f
```

## 7. Просмотр логов

```bash
cd /opt/t-loyalty-hub
docker compose logs -f
docker compose logs -f backend
docker compose logs -f frontend
```

## 8. Перезапуск

```bash
cd /opt/t-loyalty-hub
docker compose restart
```

Полная остановка:

```bash
docker compose down
```

## 9. Домен через Nginx reverse proxy

Пример `/etc/nginx/sites-available/t-loyalty-hub`:

```nginx
server {
    listen 80;
    server_name loyalty.example.com;

    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Активация:

```bash
sudo ln -s /etc/nginx/sites-available/t-loyalty-hub /etc/nginx/sites-enabled/t-loyalty-hub
sudo nginx -t
sudo systemctl reload nginx
```

После подключения домена обновите `.env`:

```env
CORS_ORIGINS=https://loyalty.example.com
```

И перезапустите compose:

```bash
docker compose up -d --build
```

## 10. HTTPS через certbot

```bash
sudo apt-get update
sudo apt-get install -y certbot python3-certbot-nginx
sudo certbot --nginx -d loyalty.example.com
```

Проверка автообновления:

```bash
sudo certbot renew --dry-run
```

## 11. GitHub Actions deploy secrets

Для `.github/workflows/deploy.example.yml` нужны secrets:

- `SERVER_HOST` — IP или домен сервера.
- `SERVER_USER` — SSH-пользователь.
- `SERVER_SSH_KEY` — приватный SSH-ключ без passphrase или с поддержкой CI.
- `SERVER_PORT` — SSH-порт, опционально, по умолчанию `22`.
- `DEPLOY_PATH` — путь проекта на сервере, опционально, по умолчанию `/opt/t-loyalty-hub`.

Приватные ключи нельзя коммитить в репозиторий.

## 12. Rollback

```bash
cd /opt/t-loyalty-hub
git log --oneline
git checkout <commit>
docker compose up -d --build
```

После проверки можно вернуться на основную ветку:

```bash
git checkout main
docker compose up -d --build
```

## Troubleshooting

- `port is already allocated`: освободите порт `3000` или измените mapping в `docker-compose.yml`.
- `CSV files not found`: проверьте, что в образ попала папка `backend/data`, а `DATA_DIR=/app/data`.
- `frontend не видит backend`: в Docker build должен использоваться `VITE_API_BASE_URL=/api`, а nginx проксирует `/api` в service `backend`.
- `Docker daemon not running`: запустите Docker service и проверьте права пользователя.
- Android Emulator не ходит на `127.0.0.1`: для мобильного клиента используйте `http://10.0.2.2:8000`.
