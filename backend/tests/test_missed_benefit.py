from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_missed_benefit_does_not_mix_currencies() -> None:
    response = client.get("/api/users/1/missed-benefit")

    assert response.status_code == 200
    payload = response.json()
    currencies = [item["currency"] for item in payload["items"]]

    assert payload["method"] == "average_monthly_cashback_uplift"
    assert payload["uplift_factor"] == 0.15
    assert len(currencies) == len(set(currencies))
    assert set(currencies) == {"rub", "miles", "bravo-points"}


def test_missed_benefit_calculates_uplift_with_rounding() -> None:
    response = client.get("/api/users/1/missed-benefit")

    assert response.status_code == 200
    for item in response.json()["items"]:
        assert item["potential_extra_amount"] == round(item["average_monthly_amount"] * 0.15, 2)


def test_missed_benefit_handles_user_without_history() -> None:
    response = client.get("/api/users/3/missed-benefit")

    assert response.status_code == 200
    payload = response.json()

    assert payload["items"] == []
    assert payload["explanation"]


def test_missed_benefit_unknown_user_returns_404() -> None:
    response = client.get("/api/users/999999/missed-benefit")

    assert response.status_code == 404
    assert response.json() == {"detail": "User not found"}
