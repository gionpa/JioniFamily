from datetime import date
from typing import Optional

from fastapi import APIRouter, Query

from app.api.deps import DB, CurrentUser
from app.schemas.stats import HistoryResponse, WeeklyStatsResponse
from app.services import stats_service

router = APIRouter(prefix="/stats", tags=["stats"])


@router.get("/weekly", response_model=WeeklyStatsResponse)
async def weekly_stats(
    user: CurrentUser,
    db: DB,
    week: Optional[date] = Query(None),
):
    return await stats_service.get_weekly_stats(db, user.family_id, week)


@router.get("/history", response_model=HistoryResponse)
async def history(
    user: CurrentUser,
    db: DB,
    limit: int = Query(12, ge=1, le=52),
):
    weeks = await stats_service.get_history(db, user.family_id, limit)
    return HistoryResponse(weeks=weeks)
