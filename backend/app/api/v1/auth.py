from fastapi import APIRouter

from app.api.deps import DB, CurrentUser
from app.core.exceptions import BadRequest, InvalidCredentials, TokenExpired
from app.core.security import (
    create_access_token,
    create_refresh_token,
    decode_token,
    hash_pin,
    verify_pin,
)
from app.models.user import User
from app.schemas.auth import ChangePinRequest, LoginRequest, RefreshRequest, TokenResponse

from sqlalchemy import select

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post("/login", response_model=TokenResponse)
async def login(req: LoginRequest, db: DB):
    result = await db.execute(select(User).where(User.avatar_key == req.avatar_key))
    user = result.scalar_one_or_none()
    if user is None or not verify_pin(req.pin, user.pin_hash):
        raise InvalidCredentials()
    return TokenResponse(
        access_token=create_access_token(user.id),
        refresh_token=create_refresh_token(user.id),
    )


@router.post("/refresh", response_model=TokenResponse)
async def refresh(req: RefreshRequest, db: DB):
    payload = decode_token(req.refresh_token)
    if payload is None or payload.get("type") != "refresh":
        raise TokenExpired()
    user_id = payload.get("sub")
    result = await db.execute(select(User).where(User.id == user_id))
    user = result.scalar_one_or_none()
    if user is None:
        raise InvalidCredentials()
    return TokenResponse(
        access_token=create_access_token(user.id),
        refresh_token=create_refresh_token(user.id),
    )


@router.post("/logout")
async def logout(user: CurrentUser):
    # Stateless JWT — client discards tokens
    return {"message": "로그아웃 되었습니다"}


@router.put("/pin")
async def change_pin(req: ChangePinRequest, user: CurrentUser, db: DB):
    if not verify_pin(req.old_pin, user.pin_hash):
        raise BadRequest("현재 PIN이 올바르지 않습니다")
    user.pin_hash = hash_pin(req.new_pin)
    await db.commit()
    return {"message": "PIN이 변경되었습니다"}
