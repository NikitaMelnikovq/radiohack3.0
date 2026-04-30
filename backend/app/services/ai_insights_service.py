from typing import cast

from app.schemas.ai_insights import AIInsight, AIInsightsResponse, QuickQuestion
from app.schemas.common_types import Confidence
from app.schemas.cross_sell import CrossSellResponse
from app.schemas.gamification import GamificationResponse
from app.schemas.loyalty import LoyaltyAnalytics, LoyaltyForecast
from app.schemas.missed_benefit import MissedBenefitResponse
from app.schemas.offer import OffersResponse
from app.services.analytics_service import AnalyticsService
from app.services.cross_sell_service import CrossSellService
from app.services.forecast_service import ForecastService
from app.services.gamification_service import GamificationService
from app.services.missed_benefit_service import MissedBenefitService
from app.services.offers_service import OffersService
from app.services.users_service import UsersService


class AIInsightsService:
    method = "rule_based_ai_insights"
    title = "Персональные инсайты по вашей выгоде"

    def __init__(
        self,
        users_service: UsersService,
        analytics_service: AnalyticsService,
        forecast_service: ForecastService,
        offers_service: OffersService,
        cross_sell_service: CrossSellService,
        missed_benefit_service: MissedBenefitService,
        gamification_service: GamificationService,
    ) -> None:
        self.users_service = users_service
        self.analytics_service = analytics_service
        self.forecast_service = forecast_service
        self.offers_service = offers_service
        self.cross_sell_service = cross_sell_service
        self.missed_benefit_service = missed_benefit_service
        self.gamification_service = gamification_service

    def get_insights(self, user_id: int) -> AIInsightsResponse:
        user = self.users_service.get_user_preview(user_id)
        analytics = self.analytics_service.get_analytics(user_id)
        forecast = self.forecast_service.get_forecast(user_id)
        offers = self.offers_service.get_personal_offers(user_id)
        cross_sell = self.cross_sell_service.get_recommendations(user_id)
        missed_benefit = self.missed_benefit_service.get_missed_benefit(user_id)
        gamification = self.gamification_service.get_gamification(user_id)

        insights = self._build_insights(analytics, forecast, offers, cross_sell, missed_benefit, gamification)
        return AIInsightsResponse(
            user_id=user_id,
            method=self.method,
            title=self.title,
            summary=f"Мы нашли {len(insights)} способов увеличить или лучше понять вашу выгоду в следующем месяце.",
            insights=insights,
            quick_questions=self._quick_questions(user.financial_segment, analytics, forecast, offers, cross_sell, gamification),
        )

    def _build_insights(
        self,
        analytics: LoyaltyAnalytics,
        forecast: LoyaltyForecast,
        offers: OffersResponse,
        cross_sell: CrossSellResponse,
        missed_benefit: MissedBenefitResponse,
        gamification: GamificationResponse,
    ) -> list[AIInsight]:
        insights: list[AIInsight] = []

        if analytics.best_program:
            insights.append(self._best_program_focus(analytics))
        if forecast.items:
            insights.append(self._forecast_explanation(forecast))
        if offers.offers:
            insights.append(self._offer_activation_tip(offers))
        if cross_sell.recommendations:
            insights.append(self._cross_sell_next_step(cross_sell))
        if missed_benefit.items:
            insights.append(self._missed_benefit_tip(missed_benefit))

        insights.append(self._segment_explanation(offers))
        if gamification.level.next_level:
            insights.append(self._gamification_tip(gamification))

        insights.sort(key=lambda insight: insight.priority)
        return insights[:7]

    def _best_program_focus(self, analytics: LoyaltyAnalytics) -> AIInsight:
        best_program = analytics.best_program
        assert best_program is not None
        confidence = self._confidence_by_months(analytics)
        return AIInsight(
            insight_id="best_program_focus",
            type="optimization",
            priority=1,
            title=f"Сделайте упор на {best_program.loyalty_program}",
            description="Эта программа принесла вам больше всего выгоды среди активных программ.",
            reason=f"{best_program.loyalty_program} является самой выгодной программой по сумме выплат.",
            evidence=[
                f"Лучшая программа: {best_program.loyalty_program}",
                f"Валюта: {best_program.currency}",
                f"Сумма выплат по программе: {best_program.amount}",
            ],
            confidence=confidence,
            cta_label="Посмотреть аналитику",
        )

    def _forecast_explanation(self, forecast: LoyaltyForecast) -> AIInsight:
        confidence = self._forecast_confidence(forecast)
        evidence = [
            f"Метод: {forecast.method}",
            f"Период прогноза: {forecast.forecast_period_days} дней",
        ]
        evidence.extend(
            f"{item.currency}: {item.predicted_amount}"
            for item in forecast.items[:3]
        )
        return AIInsight(
            insight_id="forecast_explanation",
            type="forecast",
            priority=2,
            title="Прогноз построен по среднему за последние месяцы",
            description="Ожидаемая выгода рассчитана отдельно по каждой валюте без конвертации.",
            reason="Сервис прогнозов использует среднее значение выплат за последние 3 месяца.",
            evidence=evidence,
            confidence=confidence,
            cta_label="Посмотреть прогноз",
        )

    def _offer_activation_tip(self, offers: OffersResponse) -> AIInsight:
        top_offer = offers.offers[0]
        return AIInsight(
            insight_id="offer_activation_tip",
            type="optimization",
            priority=3,
            title=f"Начните с оффера {top_offer.partner_name}",
            description="Это самый высокий процент кэшбэка среди предложений вашего сегмента.",
            reason="Офферы фильтруются по financial_segment и сортируются по cashback_percent.",
            evidence=[
                f"Сегмент пользователя: {offers.user_segment}",
                f"Партнёр: {top_offer.partner_name}",
                f"Cashback: {top_offer.cashback_percent}%",
            ],
            confidence="high",
            cta_label="Открыть офферы",
        )

    def _cross_sell_next_step(self, cross_sell: CrossSellResponse) -> AIInsight:
        recommendation = cross_sell.recommendations[0]
        return AIInsight(
            insight_id="cross_sell_next_step",
            type="cross_sell",
            priority=4,
            title=f"Следующий продукт: {recommendation.product_name}",
            description=recommendation.description,
            reason=recommendation.reason,
            evidence=[
                f"Product score: {recommendation.score}",
                f"Приоритет: {recommendation.priority}",
                *recommendation.evidence[:3],
            ],
            confidence=self._confidence_from_score(recommendation.score),
            cta_label=recommendation.cta_label,
        )

    def _missed_benefit_tip(self, missed_benefit: MissedBenefitResponse) -> AIInsight:
        evidence = [
            f"{item.currency}: +{item.potential_extra_amount}"
            for item in missed_benefit.items[:3]
        ]
        if missed_benefit.top_offer_cashback_percent is not None:
            evidence.append(f"Лучший cashback оффера: {missed_benefit.top_offer_cashback_percent}%")
        return AIInsight(
            insight_id="missed_benefit_tip",
            type="risk",
            priority=5,
            title="Есть потенциал добрать выгоду",
            description="Мы оценили приблизительный uplift как 15% от среднего месячного cashback по каждой валюте.",
            reason="Missed benefit считается от average_monthly_cashback без смешивания валют.",
            evidence=evidence,
            confidence="medium",
            cta_label="Посмотреть потенциал",
        )

    def _segment_explanation(self, offers: OffersResponse) -> AIInsight:
        return AIInsight(
            insight_id="segment_explanation",
            type="explanation",
            priority=6,
            title="Почему показаны эти предложения",
            description="Подборка офферов соответствует финансовому сегменту пользователя.",
            reason="Offers service фильтрует партнёрские предложения по financial_segment пользователя.",
            evidence=[
                f"Финансовый сегмент: {offers.user_segment}",
                f"Подходящих офферов: {len(offers.offers)}",
            ],
            confidence="high",
            cta_label="Посмотреть офферы",
        )

    def _gamification_tip(self, gamification: GamificationResponse) -> AIInsight:
        level = gamification.level
        return AIInsight(
            insight_id="loyalty_level_tip",
            type="gamification",
            priority=7,
            title=f"До уровня {level.next_level} осталось {level.points_to_next_level} баллов",
            description="Уровень считается по накопленной выгоде как условным loyalty points без конвертации валют.",
            reason="Gamification service использует total_cashback_value как explainable points-механику.",
            evidence=[
                f"Текущий уровень: {level.name}",
                f"Текущие баллы: {level.current_points}",
                f"Прогресс: {level.progress_percent}%",
            ],
            confidence="medium",
            cta_label="Посмотреть челленджи",
        )

    def _quick_questions(
        self,
        segment: str,
        analytics: LoyaltyAnalytics,
        forecast: LoyaltyForecast,
        offers: OffersResponse,
        cross_sell: CrossSellResponse,
        gamification: GamificationResponse,
    ) -> list[QuickQuestion]:
        best_program_answer = (
            f"Сейчас больше всего выгоды дала программа {analytics.best_program.loyalty_program} в валюте {analytics.best_program.currency}."
            if analytics.best_program
            else "Истории выплат пока недостаточно, чтобы выбрать лучшую программу."
        )
        forecast_answer = (
            "Прогноз по валютам: " + ", ".join(f"{item.currency}: {item.predicted_amount}" for item in forecast.items)
            if forecast.items
            else "Прогноз появится после накопления истории выплат."
        )
        top_recommendation = cross_sell.recommendations[0] if cross_sell.recommendations else None
        top_offer = offers.offers[0] if offers.offers else None

        return [
            QuickQuestion(
                question="Почему мне показали эти акции?",
                answer=f"Мы показываем акции, которые соответствуют финансовому сегменту {segment}, и сортируем их по cashback_percent.",
            ),
            QuickQuestion(
                question="Какую программу мне выгоднее использовать?",
                answer=best_program_answer,
            ),
            QuickQuestion(
                question="Сколько я могу получить в следующем месяце?",
                answer=forecast_answer,
            ),
            QuickQuestion(
                question="Как увеличить cashback?",
                answer=(
                    f"Начните с оффера {top_offer.partner_name} с cashback {top_offer.cashback_percent}%."
                    if top_offer
                    else "Начните с подключения подходящей программы и накопления первой истории выплат."
                ),
            ),
            QuickQuestion(
                question="Почему мне рекомендован этот продукт?",
                answer=(
                    f"{top_recommendation.product_name} получил score {top_recommendation.score} на основе сегмента, счетов и истории выплат."
                    if top_recommendation
                    else "Рекомендации появятся, когда будет достаточно данных по пользователю."
                ),
            ),
            QuickQuestion(
                question="Что значит мой уровень выгоды?",
                answer=f"Уровень {gamification.level.name} считается по сумме накопленной выгоды как условным loyalty points.",
            ),
        ]

    def _confidence_by_months(self, analytics: LoyaltyAnalytics) -> Confidence:
        months_count = len({item.month for item in analytics.monthly_dynamics})
        if months_count < 2:
            return "low"
        if months_count <= 3:
            return "medium"
        return "high"

    def _forecast_confidence(self, forecast: LoyaltyForecast) -> Confidence:
        if not forecast.items:
            return "low"
        confidence_order = {"low": 0, "medium": 1, "high": 2}
        return cast(
            Confidence,
            max(forecast.items, key=lambda item: confidence_order.get(item.confidence, 0)).confidence,
        )

    def _confidence_from_score(self, score: int) -> Confidence:
        if score >= 80:
            return "high"
        if score >= 55:
            return "medium"
        return "low"
