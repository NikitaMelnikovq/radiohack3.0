from pydantic import BaseModel, ConfigDict


class Account(BaseModel):
    model_config = ConfigDict(frozen=True)

    account_id: int
    user_id: int
    loyalty_program_id: int
    current_balance: float
