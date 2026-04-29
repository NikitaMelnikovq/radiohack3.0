from fastapi import APIRouter

from app.api.routes import ai_insights, cross_sell, demo, gamification, health, loyalty, missed_benefit, offers, users

api_router = APIRouter()
api_router.include_router(health.router)
api_router.include_router(users.router)
api_router.include_router(loyalty.router)
api_router.include_router(offers.router)
api_router.include_router(cross_sell.router)
api_router.include_router(gamification.router)
api_router.include_router(ai_insights.router)
api_router.include_router(missed_benefit.router)
api_router.include_router(demo.router)
