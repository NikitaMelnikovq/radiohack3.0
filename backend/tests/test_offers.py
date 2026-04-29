from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_offers_returns_only_user_segment_offers_sorted_by_cashback() -> None:
    response = client.get("/api/users/1/offers")

    assert response.status_code == 200
    payload = response.json()

    assert payload["user_segment"] == "HIGH"
    assert [offer["financial_segment"] for offer in payload["offers"]] == ["HIGH", "HIGH", "HIGH"]
    assert [offer["cashback_percent"] for offer in payload["offers"]] == [12.0, 9.0, 7.0]
    assert all("HIGH" in offer["reason"] for offer in payload["offers"])


def test_offers_unknown_user_returns_404() -> None:
    response = client.get("/api/users/999999/offers")

    assert response.status_code == 404
    assert response.json() == {"detail": "User not found"}
