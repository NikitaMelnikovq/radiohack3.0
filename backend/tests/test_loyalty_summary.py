from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_loyalty_summary_calculates_totals_by_currency() -> None:
    response = client.get("/api/users/1/loyalty/summary")

    assert response.status_code == 200
    payload = response.json()
    totals = {item["currency"]: item["amount"] for item in payload["totals_by_currency"]}

    assert totals == {
        "rub": 3000.0,
        "miles": 3500.0,
        "bravo-points": 300.0,
    }
    assert payload["total_transactions"] == 7
    assert payload["last_payout_date"] == "2025-04-10"


def test_loyalty_summary_handles_user_without_history() -> None:
    response = client.get("/api/users/3/loyalty/summary")

    assert response.status_code == 200
    payload = response.json()

    assert payload["accounts"]
    assert payload["totals_by_currency"] == []
    assert payload["total_transactions"] == 0
    assert payload["last_payout_date"] is None
