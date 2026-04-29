from pydantic import BaseModel

from app.schemas.common_types import FinancialSegment


class DemoProfile(BaseModel):
    user_id: int
    label: str
    description: str
    financial_segment: FinancialSegment
    highlight_metrics: list[str]
    recommended_demo_flow: list[str]


class DemoProfilesResponse(BaseModel):
    profiles: list[DemoProfile]
