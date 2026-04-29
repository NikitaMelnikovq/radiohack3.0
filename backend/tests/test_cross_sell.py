from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


RECOMMENDATION_FIELDS = {
    "product_code",
    "product_name",
    "category",
    "priority",
    "score",
    "title",
    "description",
    "reason",
    "evidence",
    "cta_label",
}


def test_cross_sell_returns_segment_based_recommendations() -> None:
    response = client.get("/api/users/1/cross-sell")

    assert response.status_code == 200
    payload = response.json()

    assert payload["user_id"] == 1
    assert payload["financial_segment"] == "HIGH"
    assert payload["recommendations"]
    for recommendation in payload["recommendations"]:
        assert RECOMMENDATION_FIELDS <= recommendation.keys()
        assert recommendation["reason"]
        assert recommendation["evidence"]
        assert 0 <= recommendation["score"] <= 100


def test_cross_sell_sorted_by_score() -> None:
    response = client.get("/api/users/1/cross-sell")

    assert response.status_code == 200
    recommendations = response.json()["recommendations"]
    sort_keys = [(-item["score"], item["priority"]) for item in recommendations]

    assert sort_keys == sorted(sort_keys)


def test_cross_sell_unknown_user_returns_404() -> None:
    response = client.get("/api/users/999999/cross-sell")

    assert response.status_code == 404
    assert response.json() == {"detail": "User not found"}
