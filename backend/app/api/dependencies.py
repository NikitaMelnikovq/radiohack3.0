from typing import cast

from fastapi import Request

from app.services.ai_insights_service import AIInsightsService
from app.services.analytics_service import AnalyticsService
from app.services.cross_sell_service import CrossSellService
from app.services.dashboard_service import DashboardService
from app.services.demo_service import DemoService
from app.services.forecast_service import ForecastService
from app.services.gamification_service import GamificationService
from app.services.loyalty_service import LoyaltyService
from app.services.missed_benefit_service import MissedBenefitService
from app.services.offers_service import OffersService
from app.services.users_service import UsersService


def get_users_service(request: Request) -> UsersService:
    return cast(UsersService, request.app.state.users_service)


def get_loyalty_service(request: Request) -> LoyaltyService:
    return cast(LoyaltyService, request.app.state.loyalty_service)


def get_analytics_service(request: Request) -> AnalyticsService:
    return cast(AnalyticsService, request.app.state.analytics_service)


def get_forecast_service(request: Request) -> ForecastService:
    return cast(ForecastService, request.app.state.forecast_service)


def get_offers_service(request: Request) -> OffersService:
    return cast(OffersService, request.app.state.offers_service)


def get_dashboard_service(request: Request) -> DashboardService:
    return cast(DashboardService, request.app.state.dashboard_service)


def get_cross_sell_service(request: Request) -> CrossSellService:
    return cast(CrossSellService, request.app.state.cross_sell_service)


def get_gamification_service(request: Request) -> GamificationService:
    return cast(GamificationService, request.app.state.gamification_service)


def get_ai_insights_service(request: Request) -> AIInsightsService:
    return cast(AIInsightsService, request.app.state.ai_insights_service)


def get_missed_benefit_service(request: Request) -> MissedBenefitService:
    return cast(MissedBenefitService, request.app.state.missed_benefit_service)


def get_demo_service(request: Request) -> DemoService:
    return cast(DemoService, request.app.state.demo_service)
