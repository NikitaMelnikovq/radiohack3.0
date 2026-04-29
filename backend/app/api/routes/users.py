from fastapi import APIRouter, Depends

from app.api.dependencies import get_dashboard_service, get_users_service
from app.schemas.dashboard import DashboardResponse
from app.schemas.user import UserListItem, UserResponse
from app.services.dashboard_service import DashboardService
from app.services.users_service import UsersService

router = APIRouter(prefix="/users", tags=["users"])


@router.get("", response_model=list[UserListItem])
def list_users(
    users_service: UsersService = Depends(get_users_service),
) -> list[UserListItem]:
    return users_service.list_users()


@router.get("/{user_id}", response_model=UserResponse)
def get_user(
    user_id: int,
    users_service: UsersService = Depends(get_users_service),
) -> UserResponse:
    return users_service.get_user(user_id)


@router.get("/{user_id}/dashboard", response_model=DashboardResponse)
def get_dashboard(
    user_id: int,
    dashboard_service: DashboardService = Depends(get_dashboard_service),
) -> DashboardResponse:
    return dashboard_service.get_dashboard(user_id)
