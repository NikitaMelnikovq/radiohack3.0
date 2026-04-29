from pathlib import Path

from fastapi.testclient import TestClient

from app.core.config import Settings
from app.main import create_app


def test_empty_history_and_empty_offers_do_not_break_business_endpoints(tmp_path: Path) -> None:
    _write_csv_dataset(tmp_path)
    client = TestClient(create_app(Settings(data_dir=tmp_path)))

    summary_response = client.get("/api/users/1/loyalty/summary")
    forecast_response = client.get("/api/users/1/loyalty/forecast")
    offers_response = client.get("/api/users/1/offers")
    ai_insights_response = client.get("/api/users/1/ai-insights")
    missed_benefit_response = client.get("/api/users/1/missed-benefit")
    cross_sell_response = client.get("/api/users/1/cross-sell")
    dashboard_response = client.get("/api/users/1/dashboard")

    assert summary_response.status_code == 200
    assert summary_response.json()["total_transactions"] == 0
    assert forecast_response.status_code == 200
    assert forecast_response.json()["items"] == []
    assert offers_response.status_code == 200
    assert offers_response.json()["offers"] == []
    assert ai_insights_response.status_code == 200
    assert ai_insights_response.json()["insights"]
    assert missed_benefit_response.status_code == 200
    assert missed_benefit_response.json()["items"] == []
    assert cross_sell_response.status_code == 200
    assert cross_sell_response.json()["recommendations"]
    assert dashboard_response.status_code == 200
    assert dashboard_response.json()["offers"]["offers"] == []


def test_middle_financial_segment_is_normalized_to_medium(tmp_path: Path) -> None:
    _write_csv_dataset_with_middle_segment(tmp_path)
    client = TestClient(create_app(Settings(data_dir=tmp_path)))

    user_response = client.get("/api/users/1")
    offers_response = client.get("/api/users/1/offers")
    cross_sell_response = client.get("/api/users/1/cross-sell")

    assert user_response.status_code == 200
    assert user_response.json()["financial_segment"] == "MEDIUM"
    assert offers_response.status_code == 200
    assert offers_response.json()["user_segment"] == "MEDIUM"
    assert offers_response.json()["offers"][0]["financial_segment"] == "MEDIUM"
    assert cross_sell_response.status_code == 200
    assert cross_sell_response.json()["financial_segment"] == "MEDIUM"


def _write_csv_dataset(data_dir: Path) -> None:
    (data_dir / "Users.csv").write_text(
        "id,email,phone_number,full_name,financial_segment\n"
        "1,test@example.com,+79990000000,Тестовый Пользователь,HIGH\n",
        encoding="utf-8",
    )
    (data_dir / "Accounts.csv").write_text(
        "account_id,user_id,loyalty_program_id,current_balance\n"
        "1,1,1,1000.0\n",
        encoding="utf-8",
    )
    (data_dir / "LoyaltyPrograms.csv").write_text(
        "loyalty_program_id,loyalty_program_name,cashback_currency\n"
        "1,Black,rub\n",
        encoding="utf-8",
    )
    (data_dir / "LoyaltyHistory.csv").write_text(
        "transaction_id,account_id,cashback_amount,payout_date\n",
        encoding="utf-8",
    )
    (data_dir / "Offers.csv").write_text(
        "partner_id,partner_name,short_description,logo_url,brand_color_hex,cashback_percent,financial_segment\n",
        encoding="utf-8",
    )


def _write_csv_dataset_with_middle_segment(data_dir: Path) -> None:
    (data_dir / "Users.csv").write_text(
        "id,email,phone_number,full_name,financial_segment\n"
        "1,middle@example.com,+79990000001,Middle Пользователь,MIDDLE\n",
        encoding="utf-8",
    )
    (data_dir / "Accounts.csv").write_text(
        "account_id,user_id,loyalty_program_id,current_balance\n"
        "1,1,1,1000.0\n",
        encoding="utf-8",
    )
    (data_dir / "LoyaltyPrograms.csv").write_text(
        "loyalty_program_id,loyalty_program_name,cashback_currency\n"
        "1,Black,rub\n",
        encoding="utf-8",
    )
    (data_dir / "LoyaltyHistory.csv").write_text(
        "transaction_id,account_id,cashback_amount,payout_date\n"
        "1,1,100.0,2025-01-01\n",
        encoding="utf-8",
    )
    (data_dir / "Offers.csv").write_text(
        "partner_id,partner_name,short_description,logo_url,brand_color_hex,cashback_percent,financial_segment\n"
        "1,Middle Offer,Тестовый оффер,https://example.com/logo.png,#111111,5,MIDDLE\n",
        encoding="utf-8",
    )
