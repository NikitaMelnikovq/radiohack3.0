from typing import Literal

from pydantic import BaseModel

from app.schemas.common_types import Confidence

InsightType = Literal[
    "optimization",
    "explanation",
    "forecast",
    "cross_sell",
    "risk",
    "gamification",
]


class AIInsight(BaseModel):
    insight_id: str
    type: InsightType
    priority: int
    title: str
    description: str
    reason: str
    evidence: list[str]
    confidence: Confidence
    cta_label: str


class QuickQuestion(BaseModel):
    question: str
    answer: str


class AIInsightsResponse(BaseModel):
    user_id: int
    method: str
    title: str
    summary: str
    insights: list[AIInsight]
    quick_questions: list[QuickQuestion]
