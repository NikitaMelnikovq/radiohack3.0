from pydantic import BaseModel


class UserResponse(BaseModel):
    id: int
    full_name: str
    email: str
    phone_number: str
    financial_segment: str


class UserListItem(UserResponse):
    accounts_count: int
    total_cashback_value: float
