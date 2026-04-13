from typing import List

from fastapi import APIRouter

from app.api.deps import DB, CurrentUser
from app.models.user import User
from app.schemas.user import FamilyMemberResponse, UserResponse

from sqlalchemy import select

router = APIRouter(prefix="/users", tags=["users"])


@router.get("/me", response_model=UserResponse)
async def get_me(user: CurrentUser):
    return user


@router.get("/family", response_model=List[FamilyMemberResponse])
async def get_family(user: CurrentUser, db: DB):
    result = await db.execute(select(User).where(User.family_id == user.family_id))
    return result.scalars().all()
