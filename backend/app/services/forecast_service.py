from collections import defaultdict

from app.models.analytics import CashbackRecord
from app.schemas.loyalty import ForecastItem, LoyaltyForecast
from app.services.loyalty_service import LoyaltyService, currency_sort_key, round_amount


class ForecastService:
    forecast_period_days = 30
    method = "average_last_3_months"
    explanation = "Прогноз рассчитан как среднее значение выплат за последние 3 месяца по каждой валюте."

    def __init__(self, loyalty_service: LoyaltyService) -> None:
        self.loyalty_service = loyalty_service

    def get_forecast(self, user_id: int) -> LoyaltyForecast:
        records = self.loyalty_service.get_cashback_records(user_id)
        if not records:
            return LoyaltyForecast(
                forecast_period_days=self.forecast_period_days,
                method=self.method,
                items=[],
                explanation=self.explanation,
            )

        months = sorted({record.payout_date.strftime("%Y-%m") for record in records})
        latest_months = months[-3:]
        confidence = self._confidence_for_month_count(len(months))
        monthly_totals_by_currency = self._monthly_totals_for_months(records, latest_months)

        items = [
            ForecastItem(
                currency=currency,
                predicted_amount=round_amount(
                    sum(monthly_totals.get(month, 0.0) for month in latest_months) / len(latest_months)
                ),
                confidence=confidence,
            )
            for currency, monthly_totals in sorted(
                monthly_totals_by_currency.items(),
                key=lambda item: currency_sort_key(item[0]),
            )
        ]

        return LoyaltyForecast(
            forecast_period_days=self.forecast_period_days,
            method=self.method,
            items=items,
            explanation=self.explanation,
        )

    def _confidence_for_month_count(self, month_count: int) -> str:
        if month_count < 2:
            return "low"
        if month_count <= 3:
            return "medium"
        return "high"

    def _monthly_totals_for_months(
        self,
        records: list[CashbackRecord],
        months: list[str],
    ) -> dict[str, dict[str, float]]:
        months_set = set(months)
        monthly_totals_by_currency: dict[str, dict[str, float]] = defaultdict(lambda: defaultdict(float))
        for record in records:
            month = record.payout_date.strftime("%Y-%m")
            if month in months_set:
                monthly_totals_by_currency[record.currency][month] += record.amount
        return monthly_totals_by_currency
