"""Seed initial data: 1 family + 3 users + sample missions."""

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.security import hash_pin
from app.models.family import Family
from app.models.mission import Mission
from app.models.user import User


async def seed_data(db: AsyncSession):
    """Create default family, users, and sample missions if they don't exist."""
    result = await db.execute(select(Family))
    if result.scalar_one_or_none() is not None:
        return  # Already seeded

    family = Family(name="지온이네 가족")
    db.add(family)
    await db.flush()

    mom = User(
        family_id=family.id,
        name="엄마",
        role="parent",
        avatar_key="mom",
        pin_hash=hash_pin("1234"),
    )
    dad = User(
        family_id=family.id,
        name="아빠",
        role="parent",
        avatar_key="dad",
        pin_hash=hash_pin("5678"),
    )
    jioni = User(
        family_id=family.id,
        name="지온이",
        role="child",
        avatar_key="jioni",
        pin_hash=hash_pin("0000"),
    )
    db.add_all([mom, dad, jioni])
    await db.flush()

    # Sample missions
    missions = [
        Mission(
            family_id=family.id,
            created_by=mom.id,
            name="수학 공부하기",
            description="수학 문제집 2페이지 풀기",
            reward_coins=10,
            category="study",
            is_recurring=True,
        ),
        Mission(
            family_id=family.id,
            created_by=mom.id,
            name="방 청소하기",
            description="방 정리정돈 하기",
            reward_coins=15,
            category="chores",
            is_recurring=True,
        ),
        Mission(
            family_id=family.id,
            created_by=dad.id,
            name="줄넘기 100회",
            description="밖에서 줄넘기 100번 하기",
            reward_coins=20,
            category="exercise",
            is_recurring=False,
        ),
    ]
    db.add_all(missions)
    await db.commit()
    print(f"Seeded family '{family.name}' with 3 users and {len(missions)} missions")
    print("  엄마 (PIN: 1234), 아빠 (PIN: 5678), 지온이 (PIN: 0000)")
