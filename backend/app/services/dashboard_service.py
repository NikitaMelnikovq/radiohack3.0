from app.schemas.dashboard import DashboardResponse
from app.services.ai_insights_service import AIInsightsService
from app.services.analytics_service import AnalyticsService
from app.services.cross_sell_service import CrossSellService
from app.services.dashboard_score_service import DashboardScoreService
from app.services.forecast_service import ForecastService
from app.services.gamification_service import GamificationService
from app.services.loyalty_service import LoyaltyService
from app.services.missed_benefit_service import MissedBenefitService
from app.services.offers_service import OffersService
from app.services.users_service import UsersService


class DashboardService:
    def __init__(
        self,
        users_service: UsersService,
        loyalty_service: LoyaltyService,
        analytics_service: AnalyticsService,
        forecast_service: ForecastService,
        offers_service: OffersService,
        cross_sell_service: CrossSellService,
        gamification_service: GamificationService,
        ai_insights_service: AIInsightsService,
        missed_benefit_service: MissedBenefitService,
        dashboard_score_service: DashboardScoreService,
    ) -> None:
        self.users_service = users_service
        self.loyalty_service = loyalty_service
        self.analytics_service = analytics_service
        self.forecast_service = forecast_service
        self.offers_service = offers_service
        self.cross_sell_service = cross_sell_service
        self.gamification_service = gamification_service
        self.ai_insights_service = ai_insights_service
        self.missed_benefit_service = missed_benefit_service
        self.dashboard_score_service = dashboard_score_service

    def get_dashboard(self, user_id: int) -> DashboardResponse:
        return DashboardResponse(
            user=self.users_service.get_user_preview(user_id),
            loyalty_summary=self.loyalty_service.get_summary(user_id),
            analytics=self.analytics_service.get_analytics(user_id),
            forecast=self.forecast_service.get_forecast(user_id),
            offers=self.offers_service.get_personal_offers(user_id),
            cross_sell=self.cross_sell_service.get_recommendations(user_id),
            gamification=self.gamification_service.get_gamification(user_id),
            ai_insights=self.ai_insights_service.get_insights(user_id),
            missed_benefit=self.missed_benefit_service.get_missed_benefit(user_id),
            dashboard_score=self.dashboard_score_service.get_dashboard_score(user_id),
        )
