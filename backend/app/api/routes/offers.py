from fastapi import APIRouter, Depends

from app.api.dependencies import get_offers_service
from app.schemas.offer import OffersResponse
from app.services.offers_service import OffersService

router = APIRouter(prefix="/users/{user_id}/offers", tags=["offers"])


@router.get("", response_model=OffersResponse)
def get_user_offers(
    user_id: int,
    offers_service: OffersService = Depends(get_offers_service),
) -> OffersResponse:
    return offers_service.get_personal_offers(user_id)
