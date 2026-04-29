from pydantic import BaseModel

from app.schemas.ai_insights import AIInsightsResponse
from app.schemas.cross_sell import CrossSellResponse
from app.schemas.dashboard_score import LoyaltyDashboardScore
from app.schemas.gamification import GamificationResponse
from app.schemas.loyalty import LoyaltyAnalytics, LoyaltyForecast, LoyaltySummary
from app.schemas.missed_benefit import MissedBenefitResponse
from app.schemas.offer import OffersResponse
from app.schemas.user import UserListItem


class DashboardResponse(BaseModel):
    user: UserListItem
    loyalty_summary: LoyaltySummary
    analytics: LoyaltyAnalytics
    forecast: LoyaltyForecast
    offers: OffersResponse
    cross_sell: CrossSellResponse
    gamification: GamificationResponse
    ai_insights: AIInsightsResponse
    missed_benefit: MissedBenefitResponse
    dashboard_score: LoyaltyDashboardScore
