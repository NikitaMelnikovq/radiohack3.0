from dataclasses import dataclass
from typing import cast

from app.schemas.common_types import Confidence, FinancialSegment
from app.schemas.cross_sell import CrossSellRecommendation, CrossSellResponse
from app.schemas.loyalty import LoyaltyAnalytics, LoyaltyForecast, LoyaltySummary
from app.services.analytics_service import AnalyticsService
from app.services.forecast_service import ForecastService
from app.services.loyalty_service import LoyaltyService
from app.services.users_service import UsersService


@dataclass(frozen=True)
class ProductDefinition:
    code: str
    name: str
    category: str
    title: str
    description: str
    cta_label: str


PRODUCT_CATALOG: dict[str, ProductDefinition] = {
    "t_mobile": ProductDefinition(
        code="t_mobile",
        name="Т-Мобайл",
        category="ecosystem",
        title="Добавьте связь в экосистему и получайте больше поводов для выгоды",
        description="Простой продукт для ежедневного использования, который хорошо подходит клиентам с небольшой историей лояльности.",
        cta_label="Посмотреть продукт",
    ),
    "black_card": ProductDefinition(
        code="black_card",
        name="Карта Black",
        category="ecosystem",
        title="Используйте Black как базу для регулярного cashback",
        description="Базовая карта помогает чаще получать рублёвый cashback и быстрее накапливать историю выгоды.",
        cta_label="Посмотреть карту",
    ),
    "partner_cashback": ProductDefinition(
        code="partner_cashback",
        name="Партнёрский cashback",
        category="ecosystem",
        title="Активируйте партнёрские предложения для быстрой выгоды",
        description="Подходящие акции партнёров могут дать быстрый прирост выгоды без смены привычных продуктов.",
        cta_label="Открыть предложения",
    ),
    "subscription_basic": ProductDefinition(
        code="subscription_basic",
        name="Базовая подписка",
        category="ecosystem",
        title="Подключите базовую подписку для регулярных бонусов",
        description="Подписка подходит для старта: она помогает чаще видеть персональные предложения и бонусные механики.",
        cta_label="Посмотреть подписку",
    ),
    "t_investments": ProductDefinition(
        code="t_investments",
        name="Т-Инвестиции",
        category="ecosystem",
        title="Инвестируйте свободный остаток и усиливайте выгоду",
        description="Активная история лояльности и рублёвые выплаты делают инвестиционный продукт логичным следующим шагом.",
        cta_label="Посмотреть продукт",
    ),
    "all_airlines": ProductDefinition(
        code="all_airlines",
        name="All Airlines",
        category="ecosystem",
        title="Добавьте больше выгоды в путешествиях",
        description="Продукт с милями помогает конвертировать регулярные траты в будущие поездки и travel-бонусы.",
        cta_label="Посмотреть All Airlines",
    ),
    "premium_cashback_categories": ProductDefinition(
        code="premium_cashback_categories",
        name="Повышенные категории cashback",
        category="ecosystem",
        title="Усильте рублёвый кэшбэк повышенными категориями",
        description="Если рублёвая выгода уже заметна, повышенные категории помогают увеличить результат в следующем месяце.",
        cta_label="Выбрать категории",
    ),
    "subscription_plus": ProductDefinition(
        code="subscription_plus",
        name="Подписка Plus",
        category="ecosystem",
        title="Расширьте набор бонусов через подписку Plus",
        description="Подписка Plus подходит клиентам со стабильной активностью и несколькими сценариями получения выгоды.",
        cta_label="Посмотреть подписку",
    ),
    "premium": ProductDefinition(
        code="premium",
        name="Premium",
        category="ecosystem",
        title="Получите больше преимуществ с Premium",
        description="Сочетание высокого финансового статуса и активности в накоплении бонусов показывает, что вам  отлично подойдет премиальный сервис.",
        cta_label="Посмотреть Premium",
    ),
    "t_business": ProductDefinition(
        code="t_business",
        name="Т-Бизнес",
        category="ecosystem",
        title="Бизнес-продукты как следующий шаг",
        description="Для клиентов с высоким финансовым статусом бизнес-продукты могут быть полезным расширением экосистемы.",
        cta_label="Посмотреть Т-Бизнес",
    ),
    "travel_benefits": ProductDefinition(
        code="travel_benefits",
        name="Travel benefits",
        category="ecosystem",
        title="Поездки с выгодой",
        description="Активное использование программы All Airlines совместно с дополнительными бонусами для путешественников может расширить вашу выгоду.",
        cta_label="Посмотреть travel-бонусы",
    ),
}

SEGMENT_PRODUCTS: dict[str, list[str]] = {
    "LOW": ["t_mobile", "black_card", "partner_cashback", "subscription_basic"],
    "MEDIUM": ["t_investments", "all_airlines", "premium_cashback_categories", "subscription_plus"],
    "HIGH": ["premium", "t_investments", "t_business", "travel_benefits", "all_airlines"],
}

ADVANCED_PRODUCTS = {"premium", "t_investments", "t_business", "travel_benefits"}
SIMPLE_PRODUCTS = {"t_mobile", "black_card", "partner_cashback", "subscription_basic"}


class CrossSellService:
    def __init__(
        self,
        users_service: UsersService,
        loyalty_service: LoyaltyService,
        analytics_service: AnalyticsService,
        forecast_service: ForecastService,
    ) -> None:
        self.users_service = users_service
        self.loyalty_service = loyalty_service
        self.analytics_service = analytics_service
        self.forecast_service = forecast_service

    def get_recommendations(self, user_id: int) -> CrossSellResponse:
        user_preview = self.users_service.get_user_preview(user_id)
        summary = self.loyalty_service.get_summary(user_id)
        analytics = self.analytics_service.get_analytics(user_id)
        forecast = self.forecast_service.get_forecast(user_id)
        segment = cast(FinancialSegment, user_preview.financial_segment)

        recommendations = [
            self._build_recommendation(
                product_code=product_code,
                priority=priority,
                segment=segment,
                summary=summary,
                analytics=analytics,
                forecast=forecast,
            )
            for priority, product_code in enumerate(SEGMENT_PRODUCTS.get(segment, []), start=1)
        ]

        recommendations.sort(key=lambda recommendation: (-recommendation.score, recommendation.priority))
        return CrossSellResponse(
            user_id=user_id,
            financial_segment=segment,
            recommendations=recommendations[:5],
        )

    def _build_recommendation(
        self,
        product_code: str,
        priority: int,
        segment: FinancialSegment,
        summary: LoyaltySummary,
        analytics: LoyaltyAnalytics,
        forecast: LoyaltyForecast,
    ) -> CrossSellRecommendation:
        product = PRODUCT_CATALOG[product_code]
        dominant_currency = self._dominant_currency(summary)
        active_programs = {account.loyalty_program for account in summary.accounts}
        forecast_confidence = self._forecast_confidence(forecast)
        score = self._score_product(
            product_code,
            segment,
            summary,
            analytics,
            dominant_currency,
            forecast_confidence,
            active_programs,
        )

        return CrossSellRecommendation(
            product_code=product.code,
            product_name=product.name,
            category=product.category,
            priority=priority,
            score=score,
            title=self._title_for_product(product, product_code, active_programs),
            description=product.description,
            reason=self._reason_for_product(product_code, segment, active_programs),
            evidence=self._evidence(summary, analytics, dominant_currency, forecast_confidence),
            cta_label=product.cta_label,
        )

    def _score_product(
        self,
        product_code: str,
        segment: FinancialSegment,
        summary: LoyaltySummary,
        analytics: LoyaltyAnalytics,
        dominant_currency: str | None,
        forecast_confidence: Confidence,
        active_programs: set[str],
    ) -> int:
        score = 45
        score += 20 if product_code in SEGMENT_PRODUCTS.get(segment, []) else 0
        score += min(len(summary.accounts) * 4, 12)
        score += min(summary.total_transactions * 2, 16)

        score += self._currency_score(product_code, dominant_currency)
        score += self._best_program_score(product_code, analytics.best_program.loyalty_program if analytics.best_program else None)
        score += self._forecast_score(product_code, forecast_confidence)
        score += self._program_presence_score(product_code, segment, active_programs)

        if segment == "HIGH" and product_code in {"premium", "t_investments", "t_business"}:
            score += 8
        if segment == "LOW" and summary.total_transactions < 2 and product_code in SIMPLE_PRODUCTS:
            score += 8
        if segment == "MEDIUM" and dominant_currency == "rub" and product_code in {"t_investments", "premium_cashback_categories"}:
            score += 8

        return max(0, min(100, score))

    def _currency_score(self, product_code: str, dominant_currency: str | None) -> int:
        if dominant_currency == "miles" and product_code in {"travel_benefits", "all_airlines"}:
            return 16
        if dominant_currency == "rub" and product_code in {"t_investments", "premium_cashback_categories", "black_card"}:
            return 10
        if dominant_currency == "bravo-points" and product_code in {"partner_cashback", "subscription_basic", "subscription_plus"}:
            return 10
        return 0

    def _best_program_score(self, product_code: str, best_program: str | None) -> int:
        if best_program == "All Airlines" and product_code in {"travel_benefits", "all_airlines"}:
            return 12
        if best_program == "Black" and product_code in {"t_investments", "premium_cashback_categories", "black_card"}:
            return 8
        if best_program == "Bravo" and product_code in {"partner_cashback", "subscription_basic", "subscription_plus"}:
            return 8
        return 0

    def _forecast_score(self, product_code: str, confidence: Confidence) -> int:
        if confidence == "high" and product_code in ADVANCED_PRODUCTS:
            return 6
        if confidence == "medium":
            return 4
        if confidence == "low" and product_code in SIMPLE_PRODUCTS:
            return 6
        return 0

    def _program_presence_score(
        self,
        product_code: str,
        segment: FinancialSegment,
        active_programs: set[str],
    ) -> int:
        if product_code == "all_airlines":
            return -8 if "All Airlines" in active_programs else (10 if segment in {"MEDIUM", "HIGH"} else 0)
        if product_code == "black_card":
            return -6 if "Black" in active_programs else 10
        return 0

    def _dominant_currency(self, summary: LoyaltySummary) -> str | None:
        if not summary.totals_by_currency:
            return None
        return max(summary.totals_by_currency, key=lambda item: item.amount).currency

    def _forecast_confidence(self, forecast: LoyaltyForecast) -> Confidence:
        if not forecast.items:
            return "low"
        confidence_order: dict[str, int] = {"low": 0, "medium": 1, "high": 2}
        return cast(
            Confidence,
            max(forecast.items, key=lambda item: confidence_order.get(item.confidence, 0)).confidence,
        )

    def _title_for_product(
        self,
        product: ProductDefinition,
        product_code: str,
        active_programs: set[str],
    ) -> str:
        if product_code == "all_airlines" and "All Airlines" in active_programs:
            return "Усильте использование All Airlines"
        if product_code == "black_card" and "Black" in active_programs:
            return "Усильте регулярный кэшбэк по Black"
        return product.title

    def _reason_for_product(
        self,
        product_code: str,
        segment: FinancialSegment,
        active_programs: set[str],
    ) -> str:
        if product_code == "all_airlines" and "All Airlines" in active_programs:
            return "Программа уже активна, поэтому рекомендация сфокусирована на усилении использования, а не на подключении с нуля."
        return "Рекомендация основана на вашем финансовом статусе, количестве счетов, активности по лояльности и структуре выплат."

    def _evidence(
        self,
        summary: LoyaltySummary,
        analytics: LoyaltyAnalytics,
        dominant_currency: str | None,
        forecast_confidence: Confidence,
    ) -> list[str]:
        evidence = [
            f"Финансовый сегмент: {summary.user.financial_segment}",
            f"Активных счетов: {len(summary.accounts)}",
            f"История выплат: {summary.total_transactions} операций",
        ]
        if dominant_currency:
            evidence.append(f"Доминирующая валюта: {dominant_currency}")
        if analytics.best_program:
            evidence.append(f"Лучшая программа: {analytics.best_program.loyalty_program}")
        evidence.append(f"Уверенность прогноза: {forecast_confidence}")
        return evidence
