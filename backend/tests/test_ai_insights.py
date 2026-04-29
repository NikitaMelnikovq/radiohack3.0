from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


INSIGHT_FIELDS = {
    "insight_id",
    "type",
    "priority",
    "title",
    "description",
    "reason",
    "evidence",
    "confidence",
    "cta_label",
}


def test_ai_insights_are_explainable() -> None:
    response = client.get("/api/users/1/ai-insights")

    assert response.status_code == 200
    payload = response.json()

    assert payload["method"] == "rule_based_ai_insights"
    assert payload["insights"]
    assert payload["quick_questions"]
    for insight in payload["insights"]:
        assert INSIGHT_FIELDS <= insight.keys()
        assert insight["reason"]
        assert insight["evidence"]
        assert insight["confidence"] in {"low", "medium", "high"}


def test_ai_insights_for_user_without_history_do_not_invent_history_facts() -> None:
    response = client.get("/api/users/3/ai-insights")

    assert response.status_code == 200
    payload = response.json()
    insight_ids = {insight["insight_id"] for insight in payload["insights"]}

    assert "best_program_focus" not in insight_ids
    assert "forecast_explanation" not in insight_ids
    assert all(insight["evidence"] for insight in payload["insights"])


def test_ai_insights_unknown_user_returns_404() -> None:
    response = client.get("/api/users/999999/ai-insights")

    assert response.status_code == 404
    assert response.json() == {"detail": "User not found"}
