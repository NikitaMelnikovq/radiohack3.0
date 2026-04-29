from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_forecast_returns_items_and_confidence() -> None:
    response = client.get("/api/users/1/loyalty/forecast")

    assert response.status_code == 200
    payload = response.json()
    assert payload["forecast_period_days"] == 30
    assert payload["method"] == "average_last_3_months"

    items_by_currency = {item["currency"]: item for item in payload["items"]}
    assert items_by_currency["rub"]["confidence"] == "high"
    assert items_by_currency["rub"]["predicted_amount"] == 666.67
    assert items_by_currency["miles"]["predicted_amount"] == 1166.67
    assert items_by_currency["bravo-points"]["predicted_amount"] == 100.0
    assert all(item["confidence"] in {"low", "medium", "high"} for item in payload["items"])
    assert len(items_by_currency) == len(payload["items"])


def test_forecast_handles_user_without_history() -> None:
    response = client.get("/api/users/3/loyalty/forecast")

    assert response.status_code == 200
    payload = response.json()

    assert payload["method"] == "average_last_3_months"
    assert payload["items"] == []
