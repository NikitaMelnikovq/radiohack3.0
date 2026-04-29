from fastapi import APIRouter, Depends

from app.api.dependencies import get_demo_service
from app.schemas.demo import DemoProfilesResponse
from app.services.demo_service import DemoService

router = APIRouter(prefix="/demo", tags=["demo"])


@router.get("/profiles", response_model=DemoProfilesResponse)
def get_demo_profiles(
    demo_service: DemoService = Depends(get_demo_service),
) -> DemoProfilesResponse:
    return demo_service.get_profiles()
