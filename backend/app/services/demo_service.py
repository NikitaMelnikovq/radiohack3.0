from dataclasses import dataclass
from typing import cast

from app.schemas.common_types import FinancialSegment
from app.schemas.demo import DemoProfile, DemoProfilesResponse
from app.schemas.loyalty import LoyaltyForecast, LoyaltySummary
from app.schemas.user import UserListItem
from app.services.forecast_service import ForecastService
from app.services.loyalty_service import LoyaltyService
from app.services.offers_service import OffersService
from app.services.users_service import UsersService


@dataclass(frozen=True)
class DemoCandidate:
    user: UserListItem
    summary: LoyaltySummary
    forecast: LoyaltyForecast
    offers_count: int


class DemoService:
    def __init__(
        self,
        users_service: UsersService,
        loyalty_service: LoyaltyService,
        forecast_service: ForecastService,
        offers_service: OffersService,
    ) -> None:
        self.users_service = users_service
        self.loyalty_service = loyalty_service
        self.forecast_service = forecast_service
        self.offers_service = offers_service

    def get_profiles(self) -> DemoProfilesResponse:
        candidates = self._load_candidates()
        selected: dict[int, DemoCandidate] = {}

        for segment in ("HIGH", "MEDIUM", "LOW"):
            candidate = self._best_by_segment(candidates, segment)
            if candidate:
                selected.setdefault(candidate.user.id, candidate)

        for candidate in (
            self._best_by_miles(candidates),
            self._best_by_program_diversity(candidates),
            self._best_by_forecast(candidates),
        ):
            if candidate:
                selected.setdefault(candidate.user.id, candidate)

        if len(selected) < min(4, len(candidates)):
            for candidate in sorted(candidates, key=lambda item: item.user.total_cashback_value, reverse=True):
                selected.setdefault(candidate.user.id, candidate)
                if len(selected) >= min(6, len(candidates)):
                    break

        profiles = [self._build_profile(candidate) for candidate in list(selected.values())[:6]]
        return DemoProfilesResponse(profiles=profiles)

    def _load_candidates(self) -> list[DemoCandidate]:
        candidates: list[DemoCandidate] = []
        for user in self.users_service.list_users():
            summary = self.loyalty_service.get_summary(user.id)
            forecast = self.forecast_service.get_forecast(user.id)
            offers = self.offers_service.get_personal_offers(user.id)
            candidates.append(
                DemoCandidate(
                    user=user,
                    summary=summary,
                    forecast=forecast,
                    offers_count=len(offers.offers),
                )
            )
        return candidates

    def _best_by_segment(self, candidates: list[DemoCandidate], segment: str) -> DemoCandidate | None:
        segment_candidates = [
            candidate
            for candidate in candidates
            if candidate.user.financial_segment == segment
        ]
        if not segment_candidates:
            return None
        return max(segment_candidates, key=lambda candidate: candidate.user.total_cashback_value)

    def _best_by_miles(self, candidates: list[DemoCandidate]) -> DemoCandidate | None:
        candidates_with_miles = [
            candidate
            for candidate in candidates
            if self._currency_amount(candidate.summary, "miles") > 0
        ]
        if not candidates_with_miles:
            return None
        return max(candidates_with_miles, key=lambda candidate: self._currency_amount(candidate.summary, "miles"))

    def _best_by_program_diversity(self, candidates: list[DemoCandidate]) -> DemoCandidate | None:
        if not candidates:
            return None
        return max(
            candidates,
            key=lambda candidate: (
                len({account.loyalty_program for account in candidate.summary.accounts}),
                candidate.user.total_cashback_value,
            ),
        )

    def _best_by_forecast(self, candidates: list[DemoCandidate]) -> DemoCandidate | None:
        candidates_with_forecast = [candidate for candidate in candidates if candidate.forecast.items]
        if not candidates_with_forecast:
            return None
        confidence_rank = {"low": 0, "medium": 1, "high": 2}
        return max(
            candidates_with_forecast,
            key=lambda candidate: (
                max(confidence_rank.get(item.confidence, 0) for item in candidate.forecast.items),
                sum(item.predicted_amount for item in candidate.forecast.items),
            ),
        )

    def _build_profile(self, candidate: DemoCandidate) -> DemoProfile:
        segment = cast(FinancialSegment, candidate.user.financial_segment)
        return DemoProfile(
            user_id=candidate.user.id,
            label=self._label(candidate),
            description=self._description(candidate),
            financial_segment=segment,
            highlight_metrics=self._highlight_metrics(candidate),
            recommended_demo_flow=self._demo_flow(candidate),
        )

    def _label(self, candidate: DemoCandidate) -> str:
        if candidate.user.financial_segment == "HIGH":
            return "Премиальный клиент"
        if self._currency_amount(candidate.summary, "miles") > 0:
            return "Travel-сценарий"
        if len({account.loyalty_program for account in candidate.summary.accounts}) > 1:
            return "Клиент с несколькими программами"
        if candidate.user.financial_segment == "MEDIUM":
            return "Клиент среднего сегмента"
        return "Стартовый сценарий"

    def _description(self, candidate: DemoCandidate) -> str:
        if candidate.user.financial_segment == "HIGH":
            return "HIGH-сегмент, активная история и хороший кандидат для Premium, Investments и travel benefits."
        if candidate.user.financial_segment == "MEDIUM":
            return "MEDIUM-сегмент, подходит для демонстрации next best action и роста выгоды."
        if candidate.summary.total_transactions == 0:
            return "Пользователь с небольшой историей, удобен для демонстрации стартовых рекомендаций."
        return "LOW-сегмент с базовыми программами и персональными офферами."

    def _highlight_metrics(self, candidate: DemoCandidate) -> list[str]:
        metrics = [
            f"Сегмент: {candidate.user.financial_segment}",
            f"Сумма выплат: {candidate.user.total_cashback_value}",
            f"Счетов: {candidate.user.accounts_count}",
        ]
        miles = self._currency_amount(candidate.summary, "miles")
        if miles > 0:
            metrics.append(f"Мили: {miles}")
        if candidate.offers_count:
            metrics.append("Есть персональные офферы")
        if candidate.forecast.items:
            metrics.append(f"Прогноз: {candidate.forecast.items[0].confidence}")
        return metrics[:5]

    def _demo_flow(self, candidate: DemoCandidate) -> list[str]:
        flow = ["Открыть dashboard", "Показать summary и аналитику"]
        if candidate.forecast.items:
            flow.append("Показать прогноз")
        if candidate.user.financial_segment == "HIGH":
            flow.append("Показать Premium cross-sell")
        else:
            flow.append("Показать персональные рекомендации")
        flow.append("Показать AI insights")
        return flow

    def _currency_amount(self, summary: LoyaltySummary, currency: str) -> float:
        return next(
            (item.amount for item in summary.totals_by_currency if item.currency == currency),
            0.0,
        )
