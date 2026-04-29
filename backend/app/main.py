from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.router import api_router
from app.core.config import Settings, get_settings
from app.core.errors import register_exception_handlers
from app.repositories.accounts_repository import AccountsRepository
from app.repositories.csv_loader import CsvLoader
from app.repositories.loyalty_history_repository import LoyaltyHistoryRepository
from app.repositories.loyalty_programs_repository import LoyaltyProgramsRepository
from app.repositories.offers_repository import OffersRepository
from app.repositories.users_repository import UsersRepository
from app.services.ai_insights_service import AIInsightsService
from app.services.analytics_service import AnalyticsService
from app.services.cross_sell_service import CrossSellService
from app.services.dashboard_service import DashboardService
from app.services.dashboard_score_service import DashboardScoreService
from app.services.demo_service import DemoService
from app.services.forecast_service import ForecastService
from app.services.gamification_service import GamificationService
from app.services.loyalty_service import LoyaltyService
from app.services.missed_benefit_service import MissedBenefitService
from app.services.offers_service import OffersService
from app.services.users_service import UsersService


def create_app(settings: Settings | None = None) -> FastAPI:
    settings = settings or get_settings()

    app = FastAPI(
        title=settings.app_title,
        version=settings.app_version,
        docs_url="/docs",
        redoc_url="/redoc",
    )

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins,
        allow_credentials="*" not in settings.cors_origins,
        allow_methods=["*"],
        allow_headers=["*"],
    )
    register_exception_handlers(app)

    loader = CsvLoader(settings.resolved_data_dir)
    users_repository = UsersRepository(loader)
    accounts_repository = AccountsRepository(loader)
    programs_repository = LoyaltyProgramsRepository(loader)
    history_repository = LoyaltyHistoryRepository(loader)
    offers_repository = OffersRepository(loader)

    users_service = UsersService(users_repository, accounts_repository, history_repository)
    loyalty_service = LoyaltyService(
        users_repository,
        accounts_repository,
        programs_repository,
        history_repository,
    )
    analytics_service = AnalyticsService(loyalty_service)
    forecast_service = ForecastService(loyalty_service)
    offers_service = OffersService(users_repository, offers_repository)
    cross_sell_service = CrossSellService(
        users_service,
        loyalty_service,
        analytics_service,
        forecast_service,
    )
    missed_benefit_service = MissedBenefitService(analytics_service, offers_service)
    gamification_service = GamificationService(
        users_service,
        loyalty_service,
        analytics_service,
        forecast_service,
        offers_service,
        cross_sell_service,
    )
    dashboard_score_service = DashboardScoreService(
        loyalty_service,
        analytics_service,
        forecast_service,
        offers_service,
        cross_sell_service,
    )
    ai_insights_service = AIInsightsService(
        users_service,
        analytics_service,
        forecast_service,
        offers_service,
        cross_sell_service,
        missed_benefit_service,
        gamification_service,
    )
    demo_service = DemoService(
        users_service,
        loyalty_service,
        forecast_service,
        offers_service,
    )
    dashboard_service = DashboardService(
        users_service,
        loyalty_service,
        analytics_service,
        forecast_service,
        offers_service,
        cross_sell_service,
        gamification_service,
        ai_insights_service,
        missed_benefit_service,
        dashboard_score_service,
    )

    app.state.users_service = users_service
    app.state.loyalty_service = loyalty_service
    app.state.analytics_service = analytics_service
    app.state.forecast_service = forecast_service
    app.state.offers_service = offers_service
    app.state.cross_sell_service = cross_sell_service
    app.state.gamification_service = gamification_service
    app.state.ai_insights_service = ai_insights_service
    app.state.missed_benefit_service = missed_benefit_service
    app.state.dashboard_score_service = dashboard_score_service
    app.state.demo_service = demo_service
    app.state.dashboard_service = dashboard_service

    app.include_router(api_router, prefix=settings.api_prefix)
    return app


app = create_app()
