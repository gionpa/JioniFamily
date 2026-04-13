from datetime import date, timedelta
from typing import List, Optional

from sqlalchemy import func, select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.mission import Mission
from app.models.mission_completion import MissionCompletion
from app.schemas.stats import WeeklyStatsResponse
from app.services.week_service import get_week_start


async def get_weekly_stats(
    db: AsyncSession, family_id: str, week: Optional[date] = None
) -> WeeklyStatsResponse:
    week_start = get_week_start(week)
    base = (
        select(MissionCompletion)
        .join(Mission)
        .where(Mission.family_id == family_id, MissionCompletion.week_start == week_start)
    )

    total = await db.execute(select(func.count()).select_from(base.subquery()))
    total_count = total.scalar() or 0

    submitted = await db.execute(
        select(func.count()).select_from(
            base.where(MissionCompletion.status.in_(["submitted", "approved"])).subquery()
        )
    )
    submitted_count = submitted.scalar() or 0

    approved = await db.execute(
        select(func.count()).select_from(
            base.where(MissionCompletion.status == "approved").subquery()
        )
    )
    approved_count = approved.scalar() or 0

    rejected = await db.execute(
        select(func.count()).select_from(
            base.where(MissionCompletion.status == "rejected").subquery()
        )
    )
    rejected_count = rejected.scalar() or 0

    missed = await db.execute(
        select(func.count()).select_from(
            base.where(MissionCompletion.status == "missed").subquery()
        )
    )
    missed_count = missed.scalar() or 0

    # Calculate coins earned this week
    coins_stmt = (
        select(func.coalesce(func.sum(Mission.reward_coins), 0))
        .select_from(MissionCompletion)
        .join(Mission)
        .where(
            Mission.family_id == family_id,
            MissionCompletion.week_start == week_start,
            MissionCompletion.status == "approved",
        )
    )
    coins_result = await db.execute(coins_stmt)
    coins_earned = coins_result.scalar() or 0

    return WeeklyStatsResponse(
        week_start=week_start,
        total_missions=total_count,
        completed_missions=submitted_count,
        approved_missions=approved_count,
        rejected_missions=rejected_count,
        coins_earned=coins_earned,
    )


async def get_history(
    db: AsyncSession, family_id: str, limit: int = 12
) -> List[WeeklyStatsResponse]:
    week_start = get_week_start()
    results = []
    for i in range(limit):
        w = week_start - timedelta(weeks=i)
        stats = await get_weekly_stats(db, family_id, w)
        if stats.total_missions > 0:
            results.append(stats)
    return results
