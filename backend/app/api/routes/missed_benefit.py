from fastapi import APIRouter, Depends

from app.api.dependencies import get_missed_benefit_service
from app.schemas.missed_benefit import MissedBenefitResponse
from app.services.missed_benefit_service import MissedBenefitService

router = APIRouter(prefix="/users/{user_id}/missed-benefit", tags=["missed-benefit"])


@router.get("", response_model=MissedBenefitResponse)
def get_missed_benefit(
    user_id: int,
    missed_benefit_service: MissedBenefitService = Depends(get_missed_benefit_service),
) -> MissedBenefitResponse:
    return missed_benefit_service.get_missed_benefit(user_id)
