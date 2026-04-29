from datetime import date

from pydantic import BaseModel, ConfigDict


class LoyaltyHistory(BaseModel):
    model_config = ConfigDict(frozen=True)

    transaction_id: int
    account_id: int
    cashback_amount: float
    payout_date: date
