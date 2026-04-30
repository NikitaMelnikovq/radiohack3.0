from dataclasses import dataclass

from app.schemas.cross_sell import CrossSellResponse
from app.schemas.gamification import (
    GamificationResponse,
    LoyaltyBadge,
    LoyaltyChallenge,
    LoyaltyLevel,
)
from app.schemas.loyalty import LoyaltyAnalytics, LoyaltyForecast, LoyaltySummary
from app.schemas.offer import OffersResponse
from app.services.analytics_service import AnalyticsService
from app.services.cross_sell_service import CrossSellService
from app.services.forecast_service import ForecastService
from app.services.loyalty_service import LoyaltyService, round_amount
from app.services.offers_service import OffersService
from app.services.users_service import UsersService


@dataclass(frozen=True)
class LevelDefinition:
    code: str
    name: str
    min_points: int
    max_points: int | None


LEVELS = [
    LevelDefinition("bronze", "Bronze", 0, 5_000),
    LevelDefinition("silver", "Silver", 5_000, 15_000),
    LevelDefinition("gold", "Gold", 15_000, 30_000),
    LevelDefinition("black_diamond", "Black Diamond", 30_000, None),
]


class GamificationService:
    def __init__(
        self,
        users_service: UsersService,
        loyalty_service: LoyaltyService,
        analytics_service: AnalyticsService,
        forecast_service: ForecastService,
        offers_service: OffersService,
        cross_sell_service: CrossSellService,
    ) -> None:
        self.users_service = users_service
        self.loyalty_service = loyalty_service
        self.analytics_service = analytics_service
        self.forecast_service = forecast_service
        self.offers_service = offers_service
        self.cross_sell_service = cross_sell_service

    def get_gamification(self, user_id: int) -> GamificationResponse:
        user_preview = self.users_service.get_user_preview(user_id)
        summary = self.loyalty_service.get_summary(user_id)
        analytics = self.analytics_service.get_analytics(user_id)
        forecast = self.forecast_service.get_forecast(user_id)
        offers = self.offers_service.get_personal_offers(user_id)
        cross_sell = self.cross_sell_service.get_recommendations(user_id)

        level = self._build_level(int(round(user_preview.total_cashback_value)))
        badges = self._build_badges(summary, analytics, offers)
        challenges = self._build_challenges(level, summary, analytics, forecast, offers, cross_sell)

        return GamificationResponse(
            user_id=user_id,
            level=level,
            badges=badges,
            challenges=challenges,
        )

    def _build_level(self, points: int) -> LoyaltyLevel:
        current_level = LEVELS[-1]
        next_level: LevelDefinition | None = None
        for index, level in enumerate(LEVELS):
            if level.max_points is None or points < level.max_points:
                current_level = level
                next_level = LEVELS[index + 1] if index + 1 < len(LEVELS) else None
                break

        if current_level.max_points is None:
            progress_percent = 100.0
            points_to_next_level = 0
        else:
            level_size = current_level.max_points - current_level.min_points
            progress_percent = round_amount((points - current_level.min_points) / level_size * 100)
            points_to_next_level = max(current_level.max_points - points, 0)

        return LoyaltyLevel(
            code=current_level.code,
            name=current_level.name,
            current_points=points,
            next_level=next_level.name if next_level else None,
            points_to_next_level=points_to_next_level,
            progress_percent=max(0.0, min(100.0, progress_percent)),
        )

    def _build_badges(
        self,
        summary: LoyaltySummary,
        analytics: LoyaltyAnalytics,
        offers: OffersResponse,
    ) -> list[LoyaltyBadge]:
        badges: list[LoyaltyBadge] = []
        months = {item.month for item in analytics.monthly_dynamics}
        currencies = {account.cashback_currency for account in summary.accounts}
        programs = {account.loyalty_program for account in summary.accounts}

        if summary.total_transactions > 0:
            badges.append(self._badge("first_cashback", "Первая выгода", "Есть хотя бы одна выплата cashback."))
        if len(months) >= 3:
            badges.append(self._badge("stable_cashback", "Стабильная выгода", "Получали выплаты минимум в 3 разных месяца."))
        if len(programs) > 1:
            badges.append(self._badge("multi_program", "Несколько программ", "Используете больше одной программы лояльности."))
        if "miles" in currencies:
            badges.append(self._badge("traveler", "Путешественник", "У вас есть программа с начислением миль."))
        if "bravo-points" in currencies:
            badges.append(self._badge("bravo_user", "Bravo-пользователь", "У вас есть программа с баллами Bravo."))
        if summary.user.financial_segment == "HIGH":
            badges.append(self._badge("high_value_client", "Премиальный потенциал", "Финансовый сегмент пользователя — HIGH."))
        if any(offer.cashback_percent >= 10 for offer in offers.offers):
            badges.append(self._badge("offer_hunter", "Охотник за выгодой", "Есть персональные офферы с cashback от 10%."))

        return badges

    def _build_challenges(
        self,
        level: LoyaltyLevel,
        summary: LoyaltySummary,
        analytics: LoyaltyAnalytics,
        forecast: LoyaltyForecast,
        offers: OffersResponse,
        cross_sell: CrossSellResponse,
    ) -> list[LoyaltyChallenge]:
        challenges: list[LoyaltyChallenge] = []

        if level.next_level:
            challenges.append(
                LoyaltyChallenge(
                    challenge_id="reach_next_level",
                    title=f"Доберите {level.points_to_next_level} баллов до уровня {level.next_level}",
                    description="Получайте выплаты по активным программам и используйте персональные предложения.",
                    reward_text="+1000 баллов прогресса",
                    progress_percent=int(level.progress_percent),
                    difficulty="medium" if level.points_to_next_level <= 10_000 else "hard",
                )
            )

        if offers.offers:
            challenges.append(
                LoyaltyChallenge(
                    challenge_id="activate_two_partner_offers",
                    title="Активируйте 2 партнёрских предложения",
                    description="Выберите офферы с максимальным кэшбэком и проверьте прирост выгоды.",
                    reward_text="+500 баллов прогресса",
                    progress_percent=min(len(offers.offers) * 20, 80),
                    difficulty="easy",
                )
            )

        if cross_sell.recommendations:
            top_recommendation = cross_sell.recommendations[0]
            challenges.append(
                LoyaltyChallenge(
                    challenge_id="try_ecosystem_next_step",
                    title=f"Попробуйте {top_recommendation.product_name}",
                    description="Рекомендация выбрана по сегменту, программам лояльности и активности выплат.",
                    reward_text="+700 баллов прогресса",
                    progress_percent=min(top_recommendation.score // 2, 50),
                    difficulty="medium",
                )
            )

        if forecast.items:
            challenges.append(
                LoyaltyChallenge(
                    challenge_id="increase_cashback_next_month",
                    title="Увеличьте выгоду на 15%",
                    description="Используйте прогноз и подходящие офферы, чтобы улучшить результат следующего месяца.",
                    reward_text="+500 баллов прогресса",
                    progress_percent=self._forecast_progress(forecast),
                    difficulty="medium",
                )
            )
        elif summary.total_transactions == 0:
            challenges.append(
                LoyaltyChallenge(
                    challenge_id="get_first_cashback",
                    title="Получите первую выплату cashback",
                    description="Начните с базовой программы или партнёрского предложения из вашей подборки.",
                    reward_text="+300 баллов прогресса",
                    progress_percent=0,
                    difficulty="easy",
                )
            )

        challenges.append(
            LoyaltyChallenge(
                challenge_id="check_forecast_next_month",
                title="Проверьте прогноз в следующем месяце",
                description="Вернитесь в раздел лояльности после новых выплат, чтобы увидеть обновлённую динамику.",
                reward_text="+200 баллов прогресса",
                progress_percent=0,
                difficulty="easy",
            )
        )

        if len(challenges) < 3:
            challenges.append(
                LoyaltyChallenge(
                    challenge_id="open_loyalty_dashboard",
                    title="Откройте dashboard выгоды",
                    description="Посмотрите summary, аналитику и персональные рекомендации в одном месте.",
                    reward_text="+100 баллов прогресса",
                    progress_percent=0,
                    difficulty="easy",
                )
            )

        return challenges[:5]

    def _forecast_progress(self, forecast: LoyaltyForecast) -> int:
        confidence = forecast.items[0].confidence if forecast.items else "low"
        return {"high": 45, "medium": 30, "low": 15}.get(confidence, 15)

    def _badge(self, code: str, title: str, description: str) -> LoyaltyBadge:
        return LoyaltyBadge(code=code, title=title, description=description)
