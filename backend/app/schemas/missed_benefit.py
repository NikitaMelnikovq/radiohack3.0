from pydantic import BaseModel


class MissedBenefitItem(BaseModel):
    currency: str
    average_monthly_amount: float
    potential_extra_amount: float


class MissedBenefitResponse(BaseModel):
    user_id: int
    method: str
    uplift_factor: float
    items: list[MissedBenefitItem]
    top_offer_cashback_percent: float | None
    explanation: str
