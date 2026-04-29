from collections import defaultdict

from app.models.analytics import CashbackRecord
from app.schemas.loyalty import (
    BestProgram,
    CurrencyAmount,
    LoyaltyAnalytics,
    MonthlyDynamicItem,
    ProgramBreakdownItem,
)
from app.services.loyalty_service import LoyaltyService, currency_sort_key, round_amount


class AnalyticsService:
    def __init__(self, loyalty_service: LoyaltyService) -> None:
        self.loyalty_service = loyalty_service

    def get_analytics(self, user_id: int) -> LoyaltyAnalytics:
        records = self.loyalty_service.get_cashback_records(user_id)
        if not records:
            return LoyaltyAnalytics(
                monthly_dynamics=[],
                program_breakdown=[],
                best_program=None,
                average_monthly_cashback=[],
            )

        return LoyaltyAnalytics(
            monthly_dynamics=self._build_monthly_dynamics(records),
            program_breakdown=self._build_program_breakdown(records),
            best_program=self._build_best_program(records),
            average_monthly_cashback=self._build_average_monthly_cashback(records),
        )

    def _build_monthly_dynamics(self, records: list[CashbackRecord]) -> list[MonthlyDynamicItem]:
        monthly_totals: dict[tuple[str, str], float] = defaultdict(float)
        for record in records:
            month = record.payout_date.strftime("%Y-%m")
            monthly_totals[(month, record.currency)] += record.amount

        return [
            MonthlyDynamicItem(month=month, currency=currency, amount=round_amount(amount))
            for (month, currency), amount in sorted(
                monthly_totals.items(),
                key=lambda item: (item[0][0], currency_sort_key(item[0][1])),
            )
        ]

    def _build_program_breakdown(self, records: list[CashbackRecord]) -> list[ProgramBreakdownItem]:
        program_totals: dict[tuple[str, str], float] = defaultdict(float)
        for record in records:
            program_totals[(record.loyalty_program, record.currency)] += record.amount

        total_amount = sum(program_totals.values())
        return [
            ProgramBreakdownItem(
                loyalty_program=program,
                currency=currency,
                amount=round_amount(amount),
                share_percent=round_amount(amount / total_amount * 100) if total_amount else 0.0,
            )
            for (program, currency), amount in sorted(
                program_totals.items(),
                key=lambda item: (-item[1], item[0][0], currency_sort_key(item[0][1])),
            )
        ]

    def _build_best_program(self, records: list[CashbackRecord]) -> BestProgram | None:
        program_totals: dict[tuple[str, str], float] = defaultdict(float)
        for record in records:
            program_totals[(record.loyalty_program, record.currency)] += record.amount

        if not program_totals:
            return None

        (program, currency), amount = max(
            program_totals.items(),
            key=lambda item: (item[1], item[0][0]),
        )
        return BestProgram(
            loyalty_program=program,
            currency=currency,
            amount=round_amount(amount),
        )

    def _build_average_monthly_cashback(self, records: list[CashbackRecord]) -> list[CurrencyAmount]:
        monthly_totals_by_currency: dict[str, dict[str, float]] = defaultdict(lambda: defaultdict(float))
        for record in records:
            month = record.payout_date.strftime("%Y-%m")
            monthly_totals_by_currency[record.currency][month] += record.amount

        averages: list[CurrencyAmount] = []
        for currency, monthly_totals in sorted(
            monthly_totals_by_currency.items(),
            key=lambda item: currency_sort_key(item[0]),
        ):
            amount = sum(monthly_totals.values()) / len(monthly_totals)
            averages.append(CurrencyAmount(currency=currency, amount=round_amount(amount)))
        return averages
