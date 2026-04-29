from fastapi.testclient import TestClient

from app.main import app


client = TestClient(app)


def test_swagger_docs_open() -> None:
    response = client.get("/docs")

    assert response.status_code == 200
    assert "Swagger UI" in response.text


def test_openapi_contains_business_polish_paths() -> None:
    response = client.get("/openapi.json")

    assert response.status_code == 200
    paths = response.json()["paths"]

    expected_paths = {
        "/api/users/{user_id}/cross-sell",
        "/api/users/{user_id}/gamification",
        "/api/users/{user_id}/ai-insights",
        "/api/users/{user_id}/missed-benefit",
        "/api/demo/profiles",
    }
    assert expected_paths <= paths.keys()
