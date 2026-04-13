from datetime import datetime

from sqlalchemy import CheckConstraint, DateTime, ForeignKey, Integer, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.models.base import Base, new_uuid, utc_now


class User(Base):
    __tablename__ = "users"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    family_id: Mapped[str] = mapped_column(String(36), ForeignKey("families.id"), nullable=False)
    name: Mapped[str] = mapped_column(String(50), nullable=False)
    role: Mapped[str] = mapped_column(String(10), nullable=False)  # 'parent' or 'child'
    avatar_key: Mapped[str] = mapped_column(String(30), nullable=False)  # 'mom', 'dad', 'jioni'
    pin_hash: Mapped[str] = mapped_column(String(128), nullable=False)
    coin_balance: Mapped[int] = mapped_column(Integer, nullable=False, default=0)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, nullable=False
    )

    __table_args__ = (
        CheckConstraint("role IN ('parent', 'child')", name="ck_user_role"),
        CheckConstraint("coin_balance >= 0", name="ck_coin_balance_positive"),
    )

    family = relationship("Family", back_populates="users")
