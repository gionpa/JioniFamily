from typing import Optional

from datetime import datetime

from sqlalchemy import Boolean, DateTime, ForeignKey, Integer, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.models.base import Base, TimestampMixin, new_uuid, utc_now


class Mission(TimestampMixin, Base):
    __tablename__ = "missions"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    family_id: Mapped[str] = mapped_column(String(36), ForeignKey("families.id"), nullable=False)
    created_by: Mapped[str] = mapped_column(String(36), ForeignKey("users.id"), nullable=False)
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    reward_coins: Mapped[int] = mapped_column(Integer, nullable=False)
    category: Mapped[str] = mapped_column(String(20), nullable=False, default="other")
    is_recurring: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    recurrence_days: Mapped[Optional[str]] = mapped_column(
        String(20), nullable=True
    )  # comma-separated "0,2,4" for Mon,Wed,Fri
    is_active: Mapped[bool] = mapped_column(Boolean, nullable=False, default=True)

    family = relationship("Family", back_populates="missions")
    creator = relationship("User", foreign_keys=[created_by])
    completions = relationship("MissionCompletion", back_populates="mission", lazy="selectin")
