from datetime import date, timedelta
from typing import Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload
from zoneinfo import ZoneInfo

from app.core.exceptions import Forbidden, NotFound
from app.models.mission import Mission
from app.models.mission_completion import MissionCompletion
from app.models.user import User
from app.schemas.mission import MissionCreate, MissionUpdate
from app.services.week_service import get_week_start


async def close_past_completions(db: AsyncSession, family_id: str, current_week_start: date):
    """Mark all uncompleted missions from past weeks as 'missed'."""
    stmt = (
        select(MissionCompletion)
        .join(Mission)
        .where(
            Mission.family_id == family_id,
            MissionCompletion.week_start < current_week_start,
            MissionCompletion.status.in_(["pending", "submitted"]),
        )
    )
    result = await db.execute(stmt)
    completions = result.scalars().all()

    for c in completions:
        c.status = "missed"

    if completions:
        await db.commit()


async def list_missions(db: AsyncSession, family_id: str, week: Optional[date] = None):
    week_start = get_week_start(week)

    # Close out past weeks' uncompleted missions
    await close_past_completions(db, family_id, week_start)

    stmt = (
        select(Mission)
        .where(Mission.family_id == family_id, Mission.is_active == True)
        .options(selectinload(Mission.creator))
    )
    result = await db.execute(stmt)
    missions = result.scalars().all()

    # Lazy-create weekly completion records
    child_stmt = select(User).where(User.family_id == family_id, User.role == "child")
    child_result = await db.execute(child_stmt)
    children = child_result.scalars().all()

    for mission in missions:
        # Recurring: create completions every week
        # Non-recurring: only in their creation week
        mission_created = mission.created_at
        if mission_created.tzinfo is not None:
            mission_created_date = mission_created.astimezone(ZoneInfo("Asia/Seoul")).date()
        else:
            mission_created_date = mission_created.date()
        creation_week = get_week_start(mission_created_date)

        should_create = mission.is_recurring or creation_week == week_start

        if should_create:
            for child in children:
                existing = await db.execute(
                    select(MissionCompletion).where(
                        MissionCompletion.mission_id == mission.id,
                        MissionCompletion.child_id == child.id,
                        MissionCompletion.week_start == week_start,
                    )
                )
                if existing.scalar_one_or_none() is None:
                    completion = MissionCompletion(
                        mission_id=mission.id,
                        child_id=child.id,
                        week_start=week_start,
                    )
                    db.add(completion)
    await db.commit()

    return missions


async def create_mission(db: AsyncSession, user: User, data: MissionCreate) -> Mission:
    mission = Mission(
        family_id=user.family_id,
        created_by=user.id,
        name=data.name,
        description=data.description,
        reward_coins=data.reward_coins,
        category=data.category,
        is_recurring=data.is_recurring,
    )
    db.add(mission)
    await db.commit()
    await db.refresh(mission)
    return mission


async def get_mission(db: AsyncSession, mission_id: str) -> Mission:
    result = await db.execute(
        select(Mission).where(Mission.id == mission_id).options(selectinload(Mission.creator))
    )
    mission = result.scalar_one_or_none()
    if mission is None:
        raise NotFound("미션을 찾을 수 없습니다")
    return mission


async def update_mission(
    db: AsyncSession, mission_id: str, user: User, data: MissionUpdate
) -> Mission:
    mission = await get_mission(db, mission_id)
    if mission.family_id != user.family_id:
        raise Forbidden()
    update_data = data.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(mission, key, value)
    await db.commit()
    await db.refresh(mission)
    return mission


async def delete_mission(db: AsyncSession, mission_id: str, user: User):
    mission = await get_mission(db, mission_id)
    if mission.family_id != user.family_id:
        raise Forbidden()
    mission.is_active = False
    await db.commit()
