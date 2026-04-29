from fastapi import APIRouter, Depends

from app.api.dependencies import get_cross_sell_service
from app.schemas.cross_sell import CrossSellResponse
from app.services.cross_sell_service import CrossSellService

router = APIRouter(prefix="/users/{user_id}/cross-sell", tags=["cross-sell"])


@router.get("", response_model=CrossSellResponse)
def get_cross_sell(
    user_id: int,
    cross_sell_service: CrossSellService = Depends(get_cross_sell_service),
) -> CrossSellResponse:
    return cross_sell_service.get_recommendations(user_id)
