from typing import Optional

from datetime import date, datetime

from sqlalchemy import CheckConstraint, Date, DateTime, ForeignKey, String, Text, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.models.base import Base, new_uuid, utc_now


class MissionCompletion(Base):
    __tablename__ = "mission_completions"

    id: Mapped[str] = mapped_column(String(36), primary_key=True, default=new_uuid)
    mission_id: Mapped[str] = mapped_column(String(36), ForeignKey("missions.id"), nullable=False)
    child_id: Mapped[str] = mapped_column(String(36), ForeignKey("users.id"), nullable=False)
    week_start: Mapped[date] = mapped_column(Date, nullable=False)  # Sunday of week
    status: Mapped[str] = mapped_column(
        String(20), nullable=False, default="pending"
    )  # pending, submitted, approved, rejected, missed
    submitted_at: Mapped[Optional[datetime]] = mapped_column(
        DateTime(timezone=True), nullable=True
    )
    reviewed_at: Mapped[Optional[datetime]] = mapped_column(
        DateTime(timezone=True), nullable=True
    )
    reviewed_by: Mapped[Optional[str]] = mapped_column(
        String(36), ForeignKey("users.id"), nullable=True
    )
    reviewer_comment: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, nullable=False
    )

    __table_args__ = (
        UniqueConstraint("mission_id", "child_id", "week_start", name="uq_mission_child_week"),
        CheckConstraint(
            "status IN ('pending', 'submitted', 'approved', 'rejected', 'missed')",
            name="ck_completion_status",
        ),
    )

    mission = relationship("Mission", back_populates="completions")
    child = relationship("User", foreign_keys=[child_id])
    reviewer = relationship("User", foreign_keys=[reviewed_by])
