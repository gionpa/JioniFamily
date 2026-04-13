from __future__ import annotations

from datetime import date, datetime
from typing import List, Optional

from pydantic import BaseModel, Field


class MissionCreate(BaseModel):
    name: str = Field(..., max_length=100)
    description: Optional[str] = Field(None, max_length=500)
    reward_coins: int = Field(..., ge=1, le=100)
    category: str = Field("other", pattern=r"^(study|exercise|chores|other)$")
    is_recurring: bool = False


class MissionUpdate(BaseModel):
    name: Optional[str] = Field(None, max_length=100)
    description: Optional[str] = Field(None, max_length=500)
    reward_coins: Optional[int] = Field(None, ge=1, le=100)
    category: Optional[str] = Field(None, pattern=r"^(study|exercise|chores|other)$")
    is_recurring: Optional[bool] = None
    is_active: Optional[bool] = None


class MissionResponse(BaseModel):
    id: str
    name: str
    description: Optional[str]
    reward_coins: int
    category: str
    is_recurring: bool
    is_active: bool
    created_by: str
    creator_name: Optional[str] = None
    created_at: datetime
    updated_at: datetime

    model_config = {"from_attributes": True}


class CompletionResponse(BaseModel):
    id: str
    mission_id: str
    mission_name: Optional[str] = None
    mission_reward_coins: Optional[int] = None
    mission_category: Optional[str] = None
    child_id: str
    week_start: date
    status: str
    submitted_at: Optional[datetime] = None
    reviewed_at: Optional[datetime] = None
    reviewed_by: Optional[str] = None
    reviewer_comment: Optional[str] = None

    model_config = {"from_attributes": True}


class ReviewRequest(BaseModel):
    comment: Optional[str] = Field(None, max_length=200)
