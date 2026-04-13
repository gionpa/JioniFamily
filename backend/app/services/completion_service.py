from datetime import date, datetime, timezone
from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from app.core.exceptions import BadRequest, Forbidden, NotFound
from app.models.mission import Mission
from app.models.mission_completion import MissionCompletion
from app.models.user import User
from app.services.week_service import get_week_start


async def list_completions(
    db: AsyncSession,
    family_id: str,
    week: Optional[date] = None,
    status: Optional[str] = None,
) -> List[MissionCompletion]:
    week_start = get_week_start(week)
    stmt = (
        select(MissionCompletion)
        .join(Mission)
        .where(
            Mission.family_id == family_id,
            MissionCompletion.week_start == week_start,
        )
        .options(selectinload(MissionCompletion.mission))
    )
    if status:
        stmt = stmt.where(MissionCompletion.status == status)
    result = await db.execute(stmt)
    return result.scalars().all()


async def submit_mission(db: AsyncSession, mission_id: str, child: User) -> MissionCompletion:
    week_start = get_week_start()
    result = await db.execute(
        select(MissionCompletion).where(
            MissionCompletion.mission_id == mission_id,
            MissionCompletion.child_id == child.id,
            MissionCompletion.week_start == week_start,
        )
    )
    completion = result.scalar_one_or_none()
    if completion is None:
        raise NotFound("이번 주 해당 미션을 찾을 수 없습니다")
    if completion.status not in ("pending", "rejected"):
        raise BadRequest("이미 제출되었거나 승인된 미션입니다")

    completion.status = "submitted"
    completion.submitted_at = datetime.now(timezone.utc)
    await db.commit()
    await db.refresh(completion)
    return completion


async def approve_mission(
    db: AsyncSession,
    completion_id: str,
    reviewer: User,
    comment: Optional[str] = None,
) -> MissionCompletion:
    result = await db.execute(
        select(MissionCompletion)
        .where(MissionCompletion.id == completion_id)
        .options(selectinload(MissionCompletion.mission))
    )
    completion = result.scalar_one_or_none()
    if completion is None:
        raise NotFound("완료 기록을 찾을 수 없습니다")
    if completion.status != "submitted":
        raise BadRequest("제출된 미션만 승인할 수 있습니다")

    # Update completion
    completion.status = "approved"
    completion.reviewed_at = datetime.now(timezone.utc)
    completion.reviewed_by = reviewer.id
    completion.reviewer_comment = comment

    # Award coins to child
    child_result = await db.execute(select(User).where(User.id == completion.child_id))
    child = child_result.scalar_one()
    child.coin_balance += completion.mission.reward_coins

    await db.commit()
    await db.refresh(completion)
    return completion


async def reject_mission(
    db: AsyncSession,
    completion_id: str,
    reviewer: User,
    comment: Optional[str] = None,
) -> MissionCompletion:
    result = await db.execute(
        select(MissionCompletion).where(MissionCompletion.id == completion_id)
    )
    completion = result.scalar_one_or_none()
    if completion is None:
        raise NotFound("완료 기록을 찾을 수 없습니다")
    if completion.status != "submitted":
        raise BadRequest("제출된 미션만 거절할 수 있습니다")

    completion.status = "rejected"
    completion.reviewed_at = datetime.now(timezone.utc)
    completion.reviewed_by = reviewer.id
    completion.reviewer_comment = comment

    await db.commit()
    await db.refresh(completion)
    return completion
