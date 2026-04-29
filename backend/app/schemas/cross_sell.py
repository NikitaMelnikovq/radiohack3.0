from pydantic import BaseModel

from app.schemas.common_types import FinancialSegment


class CrossSellRecommendation(BaseModel):
    product_code: str
    product_name: str
    category: str
    priority: int
    score: int
    title: str
    description: str
    reason: str
    evidence: list[str]
    cta_label: str


class CrossSellResponse(BaseModel):
    user_id: int
    financial_segment: FinancialSegment
    recommendations: list[CrossSellRecommendation]
