from fastapi import APIRouter, Depends

from app.api.dependencies import get_ai_insights_service
from app.schemas.ai_insights import AIInsightsResponse
from app.services.ai_insights_service import AIInsightsService

router = APIRouter(prefix="/users/{user_id}/ai-insights", tags=["ai-insights"])


@router.get("", response_model=AIInsightsResponse)
def get_ai_insights(
    user_id: int,
    ai_insights_service: AIInsightsService = Depends(get_ai_insights_service),
) -> AIInsightsResponse:
    return ai_insights_service.get_insights(user_id)
