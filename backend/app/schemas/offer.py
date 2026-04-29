from pydantic import BaseModel


class OfferResponse(BaseModel):
    partner_id: int
    partner_name: str
    short_description: str
    logo_url: str
    brand_color_hex: str
    cashback_percent: float
    financial_segment: str
    reason: str


class OffersResponse(BaseModel):
    user_segment: str
    offers: list[OfferResponse]
