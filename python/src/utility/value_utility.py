from datetime import datetime
from decimal import Decimal


def to_str_or_empty(value) -> str:
    """取字符串值，失败返回空字符串"""
    if value is None:
        return ""
    return str(value).strip()  # 去空格


def to_str_or_default(value, default: str) -> str:
    """取字符串值，失败返回默认值"""
    if value is None:
        return default
    return str(value).strip()  # 去空格


def to_str_req(value, name: str = "") -> str:
    """取字符串值，失败抛出异常"""
    if value is None:
        raise Exception(f"取字符串值 {name} 失败" if name else f"取字符串值失败")

    s = str(value).strip()  # 去空格
    if not s:
        raise Exception(f"取字符串值 {name} 失败" if name else f"取字符串值失败")

    return s


# ======================================================================================================================

def to_int_or_none(value) -> int | None:
    """取整数值，失败返回None"""
    if value is None:
        return None
    if isinstance(value, int):
        return value
    try:
        return int(value)
    except Exception:
        return None


def to_int_or_default(value, default: int) -> int:
    """取整数值，失败返回默认值"""
    i = to_int_or_none(value)
    if i is None:
        return default
    return i


def to_int_req(value, name: str = "") -> int:
    """取整数值，失败抛出异常"""
    i = to_int_or_none(value)
    if i is None:
        raise Exception(f"取整数值 {name} 失败" if name else f"取整数值失败")
    return i


# ======================================================================================================================

def to_bool_or_none(value) -> bool | None:
    """取布尔值，失败返回None"""
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
    """取布尔值，失败返回默认值"""
    b = to_bool_or_none(value)
    if b is None:
        return default
    return b


def to_bool_req(value, name: str = "") -> bool:
    """取布尔值，失败抛出异常"""
    b = to_bool_or_none(value)
    if b is None:
        raise Exception(f"取布尔值 {name} 失败" if name else f"取布尔值失败")
    return b


# ======================================================================================================================

def to_datetime_str(value: datetime) -> str:
    """日期时间转字符串"""
    return value.strftime("%Y-%m-%d %H:%M:%S")


def to_datetime_or_none(value) -> datetime | None:
    """取日期时间，失败返回None"""
    if value is None:
        return None
    if isinstance(value, datetime):
        return value
    try:
        return datetime.fromisoformat(value)
    except Exception:
        return None


def to_datetime_or_default(value, default: datetime) -> datetime:
    """取日期时间，失败返回默认值"""
    dt = to_datetime_or_none(value)
    if dt is None:
        return default
    return dt


def to_datetime_req(value, name: str = "") -> datetime:
    """取日期时间，失败抛出异常"""
    dt = to_datetime_or_none(value)
    if dt is None:
        raise Exception(f"取日期时间值 {name} 失败" if name else f"取日期时间值失败")
    return dt


# ======================================================================================================================

def to_decimal_or_none(value) -> Decimal | None:
    """取Decimal值，失败返回None"""
    if value is None:
        return None
    if isinstance(value, Decimal):
        return value
    try:
        return Decimal(str(value))  # 需要 str()，否则 Decimal128 无法解析
    except Exception:
        return None


def to_decimal_or_default(value, default: Decimal) -> Decimal:
    """取Decimal值，失败返回默认值"""
    d = to_decimal_or_none(value)
    if d is None:
        return default
    return d


def to_decimal_req(value, name: str = "") -> Decimal:
    """取Decimal值，失败抛出异常"""
    d = to_decimal_or_none(value)
    if d is None:
        raise Exception(f"取Decimal值 {name} 失败" if name else f"取Decimal值失败")
    return d
