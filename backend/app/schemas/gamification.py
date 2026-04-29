from typing import Literal

from pydantic import BaseModel


class LoyaltyLevel(BaseModel):
    code: str
    name: str
    current_points: int
    next_level: str | None
    points_to_next_level: int
    progress_percent: float


class LoyaltyBadge(BaseModel):
    code: str
    title: str
    description: str


class LoyaltyChallenge(BaseModel):
    challenge_id: str
    title: str
    description: str
    reward_text: str
    progress_percent: int
    difficulty: Literal["easy", "medium", "hard"]


class GamificationResponse(BaseModel):
    user_id: int
    level: LoyaltyLevel
    badges: list[LoyaltyBadge]
    challenges: list[LoyaltyChallenge]
