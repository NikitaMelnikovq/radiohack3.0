from fastapi import APIRouter, Depends

from app.api.dependencies import get_gamification_service
from app.schemas.gamification import GamificationResponse
from app.services.gamification_service import GamificationService

router = APIRouter(prefix="/users/{user_id}/gamification", tags=["gamification"])


@router.get("", response_model=GamificationResponse)
def get_gamification(
    user_id: int,
    gamification_service: GamificationService = Depends(get_gamification_service),
) -> GamificationResponse:
    return gamification_service.get_gamification(user_id)
