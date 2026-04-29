from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_loyalty_analytics_returns_monthly_dynamics_and_best_program() -> None:
    response = client.get("/api/users/1/loyalty/analytics")

    assert response.status_code == 200
    payload = response.json()

    assert payload["monthly_dynamics"]
    assert all(len(item["month"]) == 7 for item in payload["monthly_dynamics"])
    assert all(item["month"][4] == "-" for item in payload["monthly_dynamics"])
    assert payload["program_breakdown"]
    assert all("share_percent" in item for item in payload["program_breakdown"])
    assert payload["best_program"] is not None
    assert payload["best_program"]["loyalty_program"] == "All Airlines"


def test_loyalty_analytics_handles_user_without_history() -> None:
    response = client.get("/api/users/3/loyalty/analytics")

    assert response.status_code == 200
    payload = response.json()

    assert payload["monthly_dynamics"] == []
    assert payload["program_breakdown"] == []
    assert payload["best_program"] is None
    assert payload["average_monthly_cashback"] == []
