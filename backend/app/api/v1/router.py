from fastapi import APIRouter

from app.api.v1 import auth, completions, missions, stats, users

api_router = APIRouter(prefix="/api/v1")
api_router.include_router(auth.router)
api_router.include_router(users.router)
api_router.include_router(completions.router)  # Before missions to avoid {mission_id} catching "completions"
api_router.include_router(missions.router)
api_router.include_router(stats.router)
