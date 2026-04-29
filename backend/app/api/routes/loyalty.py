from fastapi import APIRouter, Depends

from app.api.dependencies import (
    get_analytics_service,
    get_forecast_service,
    get_loyalty_service,
)
from app.schemas.loyalty import LoyaltyAnalytics, LoyaltyForecast, LoyaltySummary
from app.services.analytics_service import AnalyticsService
from app.services.forecast_service import ForecastService
from app.services.loyalty_service import LoyaltyService

router = APIRouter(prefix="/users/{user_id}/loyalty", tags=["loyalty"])


@router.get("/summary", response_model=LoyaltySummary)
def get_loyalty_summary(
    user_id: int,
    loyalty_service: LoyaltyService = Depends(get_loyalty_service),
) -> LoyaltySummary:
    return loyalty_service.get_summary(user_id)


@router.get("/analytics", response_model=LoyaltyAnalytics)
def get_loyalty_analytics(
    user_id: int,
    analytics_service: AnalyticsService = Depends(get_analytics_service),
) -> LoyaltyAnalytics:
    return analytics_service.get_analytics(user_id)


@router.get("/forecast", response_model=LoyaltyForecast)
def get_loyalty_forecast(
    user_id: int,
    forecast_service: ForecastService = Depends(get_forecast_service),
) -> LoyaltyForecast:
    return forecast_service.get_forecast(user_id)
