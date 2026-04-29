from typing import Literal

from pydantic import BaseModel


DashboardScoreStatus = Literal["starting", "growing", "strong", "top"]


class DashboardScoreFactor(BaseModel):
    code: str
    label: str
    value: int
    max_value: int
    explanation: str


class DashboardNextBestAction(BaseModel):
    title: str
    description: str
    cta_label: str


class LoyaltyDashboardScore(BaseModel):
    score: int
    status: DashboardScoreStatus
    title: str
    description: str
    factors: list[DashboardScoreFactor]
    next_best_action: DashboardNextBestAction
