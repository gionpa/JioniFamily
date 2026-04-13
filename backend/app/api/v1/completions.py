from datetime import date
from typing import List, Optional

from fastapi import APIRouter, Query

from app.api.deps import DB, ChildUser, CurrentUser, ParentUser
from app.schemas.mission import CompletionResponse, ReviewRequest
from app.services import completion_service

router = APIRouter(tags=["completions"])


@router.get("/missions/completions", response_model=List[CompletionResponse])
async def list_completions(
    user: CurrentUser,
    db: DB,
    week: Optional[date] = Query(None),
    status: Optional[str] = Query(None),
):
    completions = await completion_service.list_completions(
        db, user.family_id, week, status
    )
    return [
        CompletionResponse(
            id=c.id,
            mission_id=c.mission_id,
            mission_name=c.mission.name if c.mission else None,
            mission_reward_coins=c.mission.reward_coins if c.mission else None,
            mission_category=c.mission.category if c.mission else None,
            child_id=c.child_id,
            week_start=c.week_start,
            status=c.status,
            submitted_at=c.submitted_at,
            reviewed_at=c.reviewed_at,
            reviewed_by=c.reviewed_by,
            reviewer_comment=c.reviewer_comment,
        )
        for c in completions
    ]


@router.post("/missions/{mission_id}/submit", response_model=CompletionResponse)
async def submit_mission(mission_id: str, child: ChildUser, db: DB):
    c = await completion_service.submit_mission(db, mission_id, child)
    return CompletionResponse(
        id=c.id,
        mission_id=c.mission_id,
        child_id=c.child_id,
        week_start=c.week_start,
        status=c.status,
        submitted_at=c.submitted_at,
        reviewed_at=c.reviewed_at,
        reviewed_by=c.reviewed_by,
        reviewer_comment=c.reviewer_comment,
    )


@router.post("/completions/{completion_id}/approve", response_model=CompletionResponse)
async def approve_completion(
    completion_id: str, body: ReviewRequest, parent: ParentUser, db: DB
):
    c = await completion_service.approve_mission(db, completion_id, parent, body.comment)
    return CompletionResponse(
        id=c.id,
        mission_id=c.mission_id,
        child_id=c.child_id,
        week_start=c.week_start,
        status=c.status,
        submitted_at=c.submitted_at,
        reviewed_at=c.reviewed_at,
        reviewed_by=c.reviewed_by,
        reviewer_comment=c.reviewer_comment,
    )


@router.post("/completions/{completion_id}/reject", response_model=CompletionResponse)
async def reject_completion(
    completion_id: str, body: ReviewRequest, parent: ParentUser, db: DB
):
    c = await completion_service.reject_mission(db, completion_id, parent, body.comment)
    return CompletionResponse(
        id=c.id,
        mission_id=c.mission_id,
        child_id=c.child_id,
        week_start=c.week_start,
        status=c.status,
        submitted_at=c.submitted_at,
        reviewed_at=c.reviewed_at,
        reviewed_by=c.reviewed_by,
        reviewer_comment=c.reviewer_comment,
    )
