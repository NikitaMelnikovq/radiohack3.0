from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_all_public_endpoints_smoke() -> None:
    users_response = client.get("/api/users")
    assert users_response.status_code == 200
    user_id = users_response.json()[0]["id"]

    endpoints = [
        "/api/health",
        f"/api/users/{user_id}",
        f"/api/users/{user_id}/loyalty/summary",
        f"/api/users/{user_id}/loyalty/analytics",
        f"/api/users/{user_id}/loyalty/forecast",
        f"/api/users/{user_id}/offers",
        f"/api/users/{user_id}/dashboard",
        f"/api/users/{user_id}/cross-sell",
        f"/api/users/{user_id}/gamification",
        f"/api/users/{user_id}/ai-insights",
        f"/api/users/{user_id}/missed-benefit",
        "/api/demo/profiles",
    ]

    for endpoint in endpoints:
        response = client.get(endpoint)
        assert response.status_code == 200, endpoint
