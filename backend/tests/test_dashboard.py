from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


DASHBOARD_BLOCKS = {
    "user",
    "loyalty_summary",
    "analytics",
    "forecast",
    "offers",
    "cross_sell",
    "gamification",
    "ai_insights",
    "missed_benefit",
    "dashboard_score",
}


def test_dashboard_contains_core_and_business_polish_blocks() -> None:
    response = client.get("/api/users/1/dashboard")

    assert response.status_code == 200
    payload = response.json()

    assert DASHBOARD_BLOCKS <= payload.keys()
    assert payload["cross_sell"]["recommendations"]
    assert payload["gamification"]["challenges"]
    assert payload["ai_insights"]["insights"]
    assert payload["missed_benefit"]["items"]


def test_dashboard_score_contract_and_range() -> None:
    response = client.get("/api/users/1/dashboard")

    assert response.status_code == 200
    dashboard_score = response.json()["dashboard_score"]

    assert 0 <= dashboard_score["score"] <= 100
    assert dashboard_score["status"] in {"starting", "growing", "strong", "top"}
    assert dashboard_score["factors"]
    assert dashboard_score["next_best_action"]["title"]


def test_dashboard_handles_user_without_history() -> None:
    response = client.get("/api/users/3/dashboard")

    assert response.status_code == 200
    payload = response.json()

    assert payload["forecast"]["items"] == []
    assert payload["missed_benefit"]["items"] == []
    assert payload["ai_insights"]["insights"]


def test_dashboard_unknown_user_returns_404() -> None:
    response = client.get("/api/users/999999/dashboard")

    assert response.status_code == 404
    assert response.json() == {"detail": "User not found"}
