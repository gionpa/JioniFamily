from typing import Annotated

from fastapi import Depends, Header
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.exceptions import Forbidden, InvalidCredentials, TokenExpired
from app.core.security import decode_token
from app.db.session import get_db
from app.models.user import User

DB = Annotated[AsyncSession, Depends(get_db)]


async def get_current_user(
    db: DB,
    authorization: str = Header(...),
) -> User:
    if not authorization.startswith("Bearer "):
        raise InvalidCredentials()
    token = authorization[7:]
    payload = decode_token(token)
    if payload is None:
        raise TokenExpired()
    if payload.get("type") != "access":
        raise InvalidCredentials()
    user_id = payload.get("sub")
    if not user_id:
        raise InvalidCredentials()
    result = await db.execute(select(User).where(User.id == user_id))
    user = result.scalar_one_or_none()
    if user is None:
        raise InvalidCredentials()
    return user


CurrentUser = Annotated[User, Depends(get_current_user)]


async def require_parent(user: CurrentUser) -> User:
    if user.role != "parent":
        raise Forbidden("부모만 접근할 수 있습니다")
    return user


async def require_child(user: CurrentUser) -> User:
    if user.role != "child":
        raise Forbidden("자녀만 접근할 수 있습니다")
    return user


ParentUser = Annotated[User, Depends(require_parent)]
ChildUser = Annotated[User, Depends(require_child)]
