from app.schemas.missed_benefit import MissedBenefitItem, MissedBenefitResponse
from app.services.analytics_service import AnalyticsService
from app.services.loyalty_service import round_amount
from app.services.offers_service import OffersService


class MissedBenefitService:
    method = "average_monthly_cashback_uplift"
    uplift_factor = 0.15

    def __init__(
        self,
        analytics_service: AnalyticsService,
        offers_service: OffersService,
    ) -> None:
        self.analytics_service = analytics_service
        self.offers_service = offers_service

    def get_missed_benefit(self, user_id: int) -> MissedBenefitResponse:
        analytics = self.analytics_service.get_analytics(user_id)
        offers = self.offers_service.get_personal_offers(user_id)
        top_offer_cashback_percent = (
            max((offer.cashback_percent for offer in offers.offers), default=None)
        )

        items = [
            MissedBenefitItem(
                currency=item.currency,
                average_monthly_amount=item.amount,
                potential_extra_amount=round_amount(item.amount * self.uplift_factor),
            )
            for item in analytics.average_monthly_cashback
        ]

        explanation = (
            "Потенциальная выгода рассчитана как 15% от среднего месячного cashback по каждой валюте. Валюты не конвертируются."
            if items
            else "Истории выплат пока нет, поэтому потенциальная выгода не рассчитана. Валюты не конвертируются."
        )

        return MissedBenefitResponse(
            user_id=user_id,
            method=self.method,
            uplift_factor=self.uplift_factor,
            items=items,
            top_offer_cashback_percent=top_offer_cashback_percent,
            explanation=explanation,
        )
