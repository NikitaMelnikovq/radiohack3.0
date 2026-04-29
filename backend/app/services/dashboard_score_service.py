from app.schemas.cross_sell import CrossSellResponse
from app.schemas.dashboard_score import (
    DashboardNextBestAction,
    DashboardScoreFactor,
    LoyaltyDashboardScore,
)
from app.schemas.loyalty import LoyaltyAnalytics, LoyaltyForecast, LoyaltySummary
from app.schemas.offer import OffersResponse
from app.services.analytics_service import AnalyticsService
from app.services.cross_sell_service import CrossSellService
from app.services.forecast_service import ForecastService
from app.services.loyalty_service import LoyaltyService
from app.services.offers_service import OffersService


class DashboardScoreService:
    def __init__(
        self,
        loyalty_service: LoyaltyService,
        analytics_service: AnalyticsService,
        forecast_service: ForecastService,
        offers_service: OffersService,
        cross_sell_service: CrossSellService,
    ) -> None:
        self.loyalty_service = loyalty_service
        self.analytics_service = analytics_service
        self.forecast_service = forecast_service
        self.offers_service = offers_service
        self.cross_sell_service = cross_sell_service

    def get_dashboard_score(self, user_id: int) -> LoyaltyDashboardScore:
        summary = self.loyalty_service.get_summary(user_id)
        analytics = self.analytics_service.get_analytics(user_id)
        forecast = self.forecast_service.get_forecast(user_id)
        offers = self.offers_service.get_personal_offers(user_id)
        cross_sell = self.cross_sell_service.get_recommendations(user_id)

        factors = [
            self._activity_factor(summary, analytics),
            self._loyalty_diversity_factor(summary),
            self._forecast_confidence_factor(forecast),
            self._offers_relevance_factor(offers),
            self._ecosystem_fit_factor(cross_sell),
        ]
        score = min(sum(factor.value for factor in factors), 100)
        status = self._status(score)

        return LoyaltyDashboardScore(
            score=score,
            status=status,
            title=self._title(status),
            description=self._description(status),
            factors=factors,
            next_best_action=self._next_best_action(offers, cross_sell, forecast),
        )

    def _activity_factor(
        self,
        summary: LoyaltySummary,
        analytics: LoyaltyAnalytics,
    ) -> DashboardScoreFactor:
        months_count = len({item.month for item in analytics.monthly_dynamics})
        value = min(summary.total_transactions * 2, 20) + min(months_count * 5, 15)
        return DashboardScoreFactor(
            code="activity",
            label="Активность",
            value=value,
            max_value=35,
            explanation="Учитывается количество выплат и месяцев с выплатами.",
        )

    def _loyalty_diversity_factor(self, summary: LoyaltySummary) -> DashboardScoreFactor:
        programs_count = len({account.loyalty_program for account in summary.accounts})
        currencies_count = len({account.cashback_currency for account in summary.accounts})
        value = min(programs_count * 6, 14) + min(currencies_count * 3, 6)
        return DashboardScoreFactor(
            code="loyalty_diversity",
            label="Разнообразие программ",
            value=value,
            max_value=20,
            explanation="Учитывается количество программ лояльности и валют cashback.",
        )

    def _forecast_confidence_factor(self, forecast: LoyaltyForecast) -> DashboardScoreFactor:
        confidence = forecast.items[0].confidence if forecast.items else "low"
        value = {"high": 15, "medium": 10, "low": 5}.get(confidence, 0) if forecast.items else 0
        return DashboardScoreFactor(
            code="forecast_confidence",
            label="Прогноз",
            value=value,
            max_value=15,
            explanation="Учитывается достаточность истории для explainable forecast.",
        )

    def _offers_relevance_factor(self, offers: OffersResponse) -> DashboardScoreFactor:
        top_cashback = max((offer.cashback_percent for offer in offers.offers), default=0.0)
        if top_cashback >= 10:
            value = 15
        elif top_cashback >= 7:
            value = 12
        elif top_cashback >= 5:
            value = 9
        elif offers.offers:
            value = 6
        else:
            value = 0
        return DashboardScoreFactor(
            code="offers_relevance",
            label="Офферы",
            value=value,
            max_value=15,
            explanation="Учитывается наличие персональных офферов и максимальный cashback_percent.",
        )

    def _ecosystem_fit_factor(self, cross_sell: CrossSellResponse) -> DashboardScoreFactor:
        top_score = cross_sell.recommendations[0].score if cross_sell.recommendations else 0
        value = round(top_score / 100 * 15)
        return DashboardScoreFactor(
            code="ecosystem_fit",
            label="Экосистема",
            value=value,
            max_value=15,
            explanation="Учитывается релевантность лучшей cross-sell рекомендации.",
        )

    def _status(self, score: int) -> str:
        if score <= 39:
            return "starting"
        if score <= 69:
            return "growing"
        if score <= 89:
            return "strong"
        return "top"

    def _title(self, status: str) -> str:
        return {
            "starting": "Выгода только начинает накапливаться",
            "growing": "Выгода растёт",
            "strong": "Выгода используется активно",
            "top": "Выгода раскрыта почти полностью",
        }[status]

    def _description(self, status: str) -> str:
        return {
            "starting": "История выплат пока небольшая, поэтому лучше начать с простых программ и офферов.",
            "growing": "У пользователя уже есть активность, но остаётся потенциал в программах, офферах и экосистеме.",
            "strong": "У пользователя стабильная история выплат, несколько программ лояльности и подходящие предложения партнёров.",
            "top": "Пользователь активно использует лояльность, прогноз и экосистемные рекомендации имеют высокий fit.",
        }[status]

    def _next_best_action(
        self,
        offers: OffersResponse,
        cross_sell: CrossSellResponse,
        forecast: LoyaltyForecast,
    ) -> DashboardNextBestAction:
        top_offer = offers.offers[0] if offers.offers else None
        if top_offer and top_offer.cashback_percent >= 10:
            return DashboardNextBestAction(
                title="Активировать предложение с повышенным cashback",
                description="Это может увеличить выгоду в следующем месяце.",
                cta_label="Перейти к офферам",
            )
        if cross_sell.recommendations:
            recommendation = cross_sell.recommendations[0]
            return DashboardNextBestAction(
                title=f"Посмотреть {recommendation.product_name}",
                description="Это самый релевантный следующий шаг по данным лояльности и сегменту.",
                cta_label=recommendation.cta_label,
            )
        if not forecast.items:
            return DashboardNextBestAction(
                title="Начать накапливать историю выплат",
                description="После первых выплат прогноз и рекомендации станут точнее.",
                cta_label="Открыть программы",
            )
        return DashboardNextBestAction(
            title="Проверить аналитику выгоды",
            description="Динамика по месяцам поможет выбрать программу для следующего месяца.",
            cta_label="Посмотреть аналитику",
        )
