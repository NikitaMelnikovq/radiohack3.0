from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_gamification_returns_level_badges_challenges() -> None:
    response = client.get("/api/users/1/gamification")

    assert response.status_code == 200
    payload = response.json()

    assert payload["user_id"] == 1
    assert payload["level"]["code"]
    assert payload["level"]["name"]
    assert 0 <= payload["level"]["progress_percent"] <= 100
    assert isinstance(payload["badges"], list)
    assert isinstance(payload["challenges"], list)
    assert 3 <= len(payload["challenges"]) <= 5


def test_gamification_unknown_user_returns_404() -> None:
    response = client.get("/api/users/999999/gamification")

    assert response.status_code == 404
    assert response.json() == {"detail": "User not found"}
