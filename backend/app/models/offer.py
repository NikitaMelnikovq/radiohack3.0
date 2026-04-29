from pydantic import BaseModel, ConfigDict


class Offer(BaseModel):
    model_config = ConfigDict(frozen=True)

    partner_id: int
    partner_name: str
    short_description: str
    logo_url: str
    brand_color_hex: str
    cashback_percent: float
    financial_segment: str
