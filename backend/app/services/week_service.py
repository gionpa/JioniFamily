from datetime import date, timedelta
from typing import Optional


def get_week_start(d: Optional[date] = None) -> date:
    """Get Sunday (start of week) for the given date."""
    if d is None:
        from datetime import datetime
        from zoneinfo import ZoneInfo

        d = datetime.now(ZoneInfo("Asia/Seoul")).date()
    # weekday(): Mon=0 .. Sun=6
    # Sunday is start of week: shift so Sun=0
    days_since_sunday = (d.weekday() + 1) % 7
    return d - timedelta(days=days_since_sunday)
