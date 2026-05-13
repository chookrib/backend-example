import logging
from datetime import datetime, time

from fastapi import APIRouter

from src.adapter.driving.result import Result
from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()


def format_datetime(value: datetime | None) -> str | None:
    if value is None:
        return None
    return value.strftime("%Y-%m-%d %H:%M:%S.%f")

def format_time(value: time | None) -> str | None:
    if value is None:
        return None
    return value.strftime("%H:%M:%S.%f")

@router.get("/api/test/value/to-datetime")
def test_value_to_datetime():
    """测试日期时间转换"""
    datetime_dict = {
        "yyyy": format_datetime(value_utility.to_datetime_or_none("2026")),
        "yyyy-MM": format_datetime(value_utility.to_datetime_or_none("2026-01")),
        "yyyy-MM-dd": format_datetime(value_utility.to_datetime_or_none("2026-01-02")),
        "yyyy-MM-dd HH": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03")),
        "yyyy-MM-dd HH:mm": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04")),
        "yyyy-MM-dd HH:mm:ss": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05")),
        "yyyy-MM-dd HH:mm:ss.f": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.1")),
        "yyyy-MM-dd HH:mm:ss.ff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.12")),
        "yyyy-MM-dd HH:mm:ss.fff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.123")),
        "yyyy-MM-dd HH:mm:ss.ffff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.1234")),
        "yyyy-MM-dd HH:mm:ss.fffff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.12345")),
        "yyyy-MM-dd HH:mm:ss.ffffff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.123456")),
        "yyyy-MM-dd HH:mm:ss.fffffff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.1234567")),
        "yyyy-MM-dd HH:mm:ss.ffffffff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.12345678")),
        "yyyy-MM-dd HH:mm:ss.fffffffff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.123456789")),
        "yyyy-MM-dd HH:mm:ss.ffffffffff": format_datetime(value_utility.to_datetime_or_none("2026-01-02 03:04:05.0123456789")),
        "HH": format_datetime(value_utility.to_datetime_or_none("03")),
        "HH:mm": format_datetime(value_utility.to_datetime_or_none("03:04")),
        "HH:mm:ss": format_datetime(value_utility.to_datetime_or_none("03:04:05")),
        "HH:mm:ss.f": format_datetime(value_utility.to_datetime_or_none("03:04:05.1")),
        "HH:mm:ss.ff": format_datetime(value_utility.to_datetime_or_none("03:04:05.12")),
        "HH:mm:ss.fff": format_datetime(value_utility.to_datetime_or_none("03:04:05.123")),
        "HH:mm:ss.ffff": format_datetime(value_utility.to_datetime_or_none("03:04:05.1234")),
        "HH:mm:ss.fffff": format_datetime(value_utility.to_datetime_or_none("03:04:05.12345")),
        "HH:mm:ss.ffffff": format_datetime(value_utility.to_datetime_or_none("03:04:05.123456")),
        "HH:mm:ss.fffffff": format_datetime(value_utility.to_datetime_or_none("03:04:05.1234567")),
        "HH:mm:ss.ffffffff": format_datetime(value_utility.to_datetime_or_none("03:04:05.12345678")),
        "HH:mm:ss.fffffffff": format_datetime(value_utility.to_datetime_or_none("03:04:05.123456789")),
        "HH:mm:ss.ffffffffff": format_datetime(value_utility.to_datetime_or_none("03:04:05.0123456789")),
    }

    date_dict = {
        "yyyy": value_utility.to_date_or_none("2026"),
        "yyyy-MM": value_utility.to_date_or_none("2026-01"),
        "yyyy-MM-dd": value_utility.to_date_or_none("2026-01-02"),
        "yyyy-MM-dd HH": value_utility.to_date_or_none("2026-01-02 03"),
        "yyyy-MM-dd HH:mm": value_utility.to_date_or_none("2026-01-02 03:04"),
        "yyyy-MM-dd HH:mm:ss": value_utility.to_date_or_none("2026-01-02 03:04:05"),
        "yyyy-MM-dd HH:mm:ss.f": value_utility.to_date_or_none("2026-01-02 03:04:05.1"),
        "yyyy-MM-dd HH:mm:ss.ff": value_utility.to_date_or_none("2026-01-02 03:04:05.12"),
        "yyyy-MM-dd HH:mm:ss.fff": value_utility.to_date_or_none("2026-01-02 03:04:05.123"),
        "yyyy-MM-dd HH:mm:ss.ffff": value_utility.to_date_or_none("2026-01-02 03:04:05.1234"),
        "yyyy-MM-dd HH:mm:ss.fffff": value_utility.to_date_or_none("2026-01-02 03:04:05.12345"),
        "yyyy-MM-dd HH:mm:ss.ffffff": value_utility.to_date_or_none("2026-01-02 03:04:05.123456"),
        "yyyy-MM-dd HH:mm:ss.fffffff": value_utility.to_date_or_none("2026-01-02 03:04:05.1234567"),
        "yyyy-MM-dd HH:mm:ss.ffffffff": value_utility.to_date_or_none("2026-01-02 03:04:05.12345678"),
        "yyyy-MM-dd HH:mm:ss.fffffffff": value_utility.to_date_or_none("2026-01-02 03:04:05.123456789"),
        "yyyy-MM-dd HH:mm:ss.ffffffffff": value_utility.to_date_or_none("2026-01-02 03:04:05.0123456789"),
        "HH": value_utility.to_date_or_none("03"),
        "HH:mm": value_utility.to_date_or_none("03:04"),
        "HH:mm:ss": value_utility.to_date_or_none("03:04:05"),
        "HH:mm:ss.f": value_utility.to_date_or_none("03:04:05.1"),
        "HH:mm:ss.ff": value_utility.to_date_or_none("03:04:05.12"),
        "HH:mm:ss.fff": value_utility.to_date_or_none("03:04:05.123"),
        "HH:mm:ss.ffff": value_utility.to_date_or_none("03:04:05.1234"),
        "HH:mm:ss.fffff": value_utility.to_date_or_none("03:04:05.12345"),
        "HH:mm:ss.ffffff": value_utility.to_date_or_none("03:04:05.123456"),
        "HH:mm:ss.fffffff": value_utility.to_date_or_none("03:04:05.1234567"),
        "HH:mm:ss.ffffffff": value_utility.to_date_or_none("03:04:05.12345678"),
        "HH:mm:ss.fffffffff": value_utility.to_date_or_none("03:04:05.123456789"),
        "HH:mm:ss.ffffffffff": value_utility.to_date_or_none("03:04:05.0123456789"),
    }

    time_dict = {
        "yyyy": format_time(value_utility.to_time_or_none("2026")),
        "yyyy-MM": format_time(value_utility.to_time_or_none("2026-01")),
        "yyyy-MM-dd": format_time(value_utility.to_time_or_none("2026-01-02")),
        "yyyy-MM-dd HH": format_time(value_utility.to_time_or_none("2026-01-02 03")),
        "yyyy-MM-dd HH:mm": format_time(value_utility.to_time_or_none("2026-01-02 03:04")),
        "yyyy-MM-dd HH:mm:ss": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05")),
        "yyyy-MM-dd HH:mm:ss.f": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.1")),
        "yyyy-MM-dd HH:mm:ss.ff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.12")),
        "yyyy-MM-dd HH:mm:ss.fff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.123")),
        "yyyy-MM-dd HH:mm:ss.ffff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.1234")),
        "yyyy-MM-dd HH:mm:ss.fffff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.12345")),
        "yyyy-MM-dd HH:mm:ss.ffffff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.123456")),
        "yyyy-MM-dd HH:mm:ss.fffffff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.1234567")),
        "yyyy-MM-dd HH:mm:ss.ffffffff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.12345678")),
        "yyyy-MM-dd HH:mm:ss.fffffffff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.123456789")),
        "yyyy-MM-dd HH:mm:ss.ffffffffff": format_time(value_utility.to_time_or_none("2026-01-02 03:04:05.0123456789")),
        "HH": format_time(value_utility.to_time_or_none("03")),
        "HH:mm": format_time(value_utility.to_time_or_none("03:04")),
        "HH:mm:ss": format_time(value_utility.to_time_or_none("03:04:05")),
        "HH:mm:ss.f": format_time(value_utility.to_time_or_none("03:04:05.1")),
        "HH:mm:ss.ff": format_time(value_utility.to_time_or_none("03:04:05.12")),
        "HH:mm:ss.fff": format_time(value_utility.to_time_or_none("03:04:05.123")),
        "HH:mm:ss.ffff": format_time(value_utility.to_time_or_none("03:04:05.1234")),
        "HH:mm:ss.fffff": format_time(value_utility.to_time_or_none("03:04:05.12345")),
        "HH:mm:ss.ffffff": format_time(value_utility.to_time_or_none("03:04:05.123456")),
        "HH:mm:ss.fffffff": format_time(value_utility.to_time_or_none("03:04:05.1234567")),
        "HH:mm:ss.ffffffff": format_time(value_utility.to_time_or_none("03:04:05.12345678")),
        "HH:mm:ss.fffffffff": format_time(value_utility.to_time_or_none("03:04:05.123456789")),
        "HH:mm:ss.ffffffffff": format_time(value_utility.to_time_or_none("03:04:05.0123456789")),
    }

    return Result.ok(data={
        "toDateTime": datetime_dict,
        "toDate": date_dict,
        "toTime": time_dict
    })
