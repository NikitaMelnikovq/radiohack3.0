from datetime import date

from pydantic import BaseModel, ConfigDict


class CashbackRecord(BaseModel):
    model_config = ConfigDict(frozen=True)

    account_id: int
    loyalty_program: str
    currency: str
    amount: float
    payout_date: date
