from pydantic import BaseModel, ConfigDict


class LoyaltyProgram(BaseModel):
    model_config = ConfigDict(frozen=True)

    loyalty_program_id: int
    loyalty_program_name: str
    cashback_currency: str
