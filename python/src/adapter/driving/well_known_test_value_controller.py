import logging

from fastapi import APIRouter

from src.adapter.driving.result import Result
from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()

@router.get("/.well-known/test/value/datetime")
def test_value_datetime():
    """测试日期时间转换"""
    # date = datetime.now().date() + timedelta(days=1)
    return Result.ok(data={
        "date1": value_utility.to_date_or_none("2025-11-11"),
        "date2": value_utility.to_date_or_none("2025-01-01"),
        "date3": value_utility.to_date_or_none("2025-1-1"),
        "time1": value_utility.to_time_or_none("12:12:12"),
        "time2": value_utility.to_time_or_none("12:1:1"),
    })
