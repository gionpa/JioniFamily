from datetime import date
from typing import List, Optional

from fastapi import APIRouter, Query

from app.api.deps import DB, CurrentUser, ParentUser
from app.schemas.mission import MissionCreate, MissionResponse, MissionUpdate
from app.services import mission_service

router = APIRouter(prefix="/missions", tags=["missions"])


def _to_response(m) -> MissionResponse:
    return MissionResponse(
        id=m.id,
        name=m.name,
        description=m.description,
        reward_coins=m.reward_coins,
        category=m.category,
        is_recurring=m.is_recurring,
        is_active=m.is_active,
        created_by=m.created_by,
        creator_name=m.creator.name if m.creator else None,
        created_at=m.created_at,
        updated_at=m.updated_at,
    )


@router.get("", response_model=List[MissionResponse])
async def list_missions(
    user: CurrentUser,
    db: DB,
    week: Optional[date] = Query(None, description="Any date in the target week"),
):
    missions = await mission_service.list_missions(db, user.family_id, week)
    return [_to_response(m) for m in missions]


@router.post("", response_model=MissionResponse)
async def create_mission(data: MissionCreate, user: ParentUser, db: DB):
    m = await mission_service.create_mission(db, user, data)
    return MissionResponse(
        id=m.id,
        name=m.name,
        description=m.description,
        reward_coins=m.reward_coins,
        category=m.category,
        is_recurring=m.is_recurring,
        is_active=m.is_active,
        created_by=m.created_by,
        created_at=m.created_at,
        updated_at=m.updated_at,
    )


@router.get("/{mission_id}", response_model=MissionResponse)
async def get_mission(mission_id: str, user: CurrentUser, db: DB):
    m = await mission_service.get_mission(db, mission_id)
    return _to_response(m)


@router.put("/{mission_id}", response_model=MissionResponse)
async def update_mission(mission_id: str, data: MissionUpdate, user: ParentUser, db: DB):
    m = await mission_service.update_mission(db, mission_id, user, data)
    return _to_response(m)


@router.delete("/{mission_id}")
async def delete_mission(mission_id: str, user: ParentUser, db: DB):
    await mission_service.delete_mission(db, mission_id, user)
    return {"message": "미션이 삭제되었습니다"}
