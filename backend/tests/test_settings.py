from app.core.config import Settings


def test_settings_accepts_plain_cors_origin(monkeypatch):
    monkeypatch.setenv("CORS_ORIGINS", "http://localhost:3000")

    settings = Settings()

    assert settings.cors_origins == ["http://localhost:3000"]


def test_settings_accepts_json_cors_origins(monkeypatch):
    monkeypatch.setenv("CORS_ORIGINS", '["http://localhost:3000", "http://127.0.0.1:5173"]')

    settings = Settings()

    assert settings.cors_origins == ["http://localhost:3000", "http://127.0.0.1:5173"]
