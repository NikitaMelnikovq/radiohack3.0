from app.core.errors import NotFoundError
from app.repositories.accounts_repository import AccountsRepository
from app.repositories.loyalty_history_repository import LoyaltyHistoryRepository
from app.repositories.users_repository import UsersRepository
from app.schemas.user import UserListItem, UserResponse


class UsersService:
    def __init__(
        self,
        users_repository: UsersRepository,
        accounts_repository: AccountsRepository,
        history_repository: LoyaltyHistoryRepository,
    ) -> None:
        self.users_repository = users_repository
        self.accounts_repository = accounts_repository
        self.history_repository = history_repository

    def list_users(self) -> list[UserListItem]:
        return [
            self._build_user_preview(user.id)
            for user in self.users_repository.list_all()
        ]

    def get_user(self, user_id: int) -> UserResponse:
        user = self.users_repository.get_by_id(user_id)
        if user is None:
            raise NotFoundError("User not found")

        return UserResponse(
            id=user.id,
            full_name=user.full_name,
            email=user.email,
            phone_number=user.phone_number,
            financial_segment=user.financial_segment,
        )

    def get_user_preview(self, user_id: int) -> UserListItem:
        user = self.users_repository.get_by_id(user_id)
        if user is None:
            raise NotFoundError("User not found")
        return self._build_user_preview(user.id)

    def _build_user_preview(self, user_id: int) -> UserListItem:
        user = self.users_repository.get_by_id(user_id)
        if user is None:
            raise NotFoundError("User not found")

        accounts = self.accounts_repository.list_by_user_id(user.id)
        histories = self.history_repository.list_by_account_ids([account.account_id for account in accounts])
        total_cashback_value = sum(history.cashback_amount for history in histories)

        return UserListItem(
            id=user.id,
            full_name=user.full_name,
            email=user.email,
            phone_number=user.phone_number,
            financial_segment=user.financial_segment,
            accounts_count=len(accounts),
            total_cashback_value=round(total_cashback_value, 2),
        )
