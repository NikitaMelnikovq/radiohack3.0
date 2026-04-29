from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_users_returns_list_with_preview_fields() -> None:
    response = client.get("/api/users")

    assert response.status_code == 200
    payload = response.json()

    assert len(payload) == 3
    assert payload[0]["id"] == 1
    assert payload[0]["accounts_count"] == 3
    assert payload[0]["total_cashback_value"] == 6800.0


def test_get_user_returns_user_by_id() -> None:
    response = client.get("/api/users/1")

    assert response.status_code == 200
    payload = response.json()
    assert payload["id"] == 1
    assert payload["financial_segment"] == "HIGH"


def test_unknown_user_returns_404() -> None:
    response = client.get("/api/users/999999")

    assert response.status_code == 404
    assert response.json() == {"detail": "User not found"}
