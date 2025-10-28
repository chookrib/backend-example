from datetime import datetime, date, time
from decimal import Decimal


def is_blank(value) -> bool:
    """判断字符串是否为 None 或空字符串"""
    if value is None:
        return True
    s = str(value).strip()  # 去空格
    return not s


# ======================================================================================================================


# def to_string_or_empty(value) -> str:
#     """转 string，失败返回空字符串"""
#     if value is None:
#         return ""
#     return str(value).strip()  # 去空格
#
#
# def to_string_or_default(value, default: str) -> str:
#     """转 string，失败返回默认值"""
#     if value is None:
#         return default
#     return str(value).strip()  # 去空格


# ======================================================================================================================

def to_bool_or_none(value) -> bool | None:
    """转 bool，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, bool):
        return value
    b = str(value).strip().lower()
    if b in ('true', '1', 't', 'y', 'yes', 'on'):
        return True
    elif b in ('false', '0', 'f', 'n', 'no', 'off'):
        return False
    return None


def to_bool_or_default(value, default: bool) -> bool:
    """转 bool，失败返回默认值"""
    b = to_bool_or_none(value)
    if b is None:
        return default
    return b


# ======================================================================================================================

def to_int_or_none(value) -> int | None:
    """转 int，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, int):
        return value
    try:
        return int(value)
    except Exception:
        return None


def to_int_or_default(value, default: int) -> int:
    """转 int，失败返回默认值"""
    i = to_int_or_none(value)
    if i is None:
        return default
    return i


# ======================================================================================================================

def to_decimal_or_none(value) -> Decimal | None:
    """转 decimal，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, Decimal):
        return value
    try:
        return Decimal(str(value))  # 需要 str()，否则 Decimal128 无法解析
    except Exception:
        return None


def to_decimal_or_default(value, default: Decimal) -> Decimal:
    """转 decimal，失败返回默认值"""
    d = to_decimal_or_none(value)
    if d is None:
        return default
    return d


# ======================================================================================================================

def format_datetime(value: datetime) -> str:
    """格式化 datetime"""
    return value.strftime("%Y-%m-%d %H:%M:%S")


def to_datetime_or_none(value) -> datetime | None:
    """转 datetime ，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, datetime):
        return value
    try:
        # return datetime.fromisoformat(value)
        return datetime.strptime(str(value), "%Y-%m-%d %H:%M:%S")
    except Exception:
        return None


def to_datetime_or_default(value, default: datetime) -> datetime:
    """转 datetime，失败返回默认值"""
    dt = to_datetime_or_none(value)
    if dt is None:
        return default
    return dt


# ======================================================================================================================


def format_date(value: date) -> str:
    """格式化 date"""
    return value.strftime("%Y-%m-%d")


def to_date_or_none(value) -> date | None:
    """转 date ，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, date):
        return value
    try:
        return datetime.strptime(str(value), "%Y-%m-%d").date()
    except Exception:
        return None


def to_date_or_default(value, default: date) -> date:
    """转 date，失败返回默认值"""
    d = to_date_or_none(value)
    if d is None:
        return default
    return d


# ======================================================================================================================


def format_time(value: time) -> str:
    """格式化 time"""
    return value.strftime("%H:%M:%S")


def to_time_or_none(value) -> time | None:
    """转 time ，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, time):
        return value
    try:
        return datetime.strptime(str(value), "%H:%M:%S").time()
    except Exception:
        return None


def to_time_or_default(value, default: time) -> time:
    """转 time，失败返回默认值"""
    t = to_time_or_none(value)
    if t is None:
        return default
    return t
