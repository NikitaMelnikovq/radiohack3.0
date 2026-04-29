from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


PROFILE_FIELDS = {
    "user_id",
    "label",
    "description",
    "financial_segment",
    "highlight_metrics",
    "recommended_demo_flow",
}


def test_demo_profiles_returns_valid_users() -> None:
    users_response = client.get("/api/users")
    profiles_response = client.get("/api/demo/profiles")

    assert users_response.status_code == 200
    assert profiles_response.status_code == 200

    user_ids = {user["id"] for user in users_response.json()}
    profiles = profiles_response.json()["profiles"]

    assert profiles
    for profile in profiles:
        assert PROFILE_FIELDS <= profile.keys()
        assert profile["user_id"] in user_ids
        assert profile["financial_segment"] in {"LOW", "MEDIUM", "HIGH"}
        assert profile["highlight_metrics"]
        assert profile["recommended_demo_flow"]
