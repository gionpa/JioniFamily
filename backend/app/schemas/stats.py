from datetime import date

from pydantic import BaseModel


class WeeklyStatsResponse(BaseModel):
    week_start: date
    total_missions: int
    completed_missions: int
    approved_missions: int
    rejected_missions: int
    coins_earned: int


class HistoryResponse(BaseModel):
    weeks: list[WeeklyStatsResponse]
