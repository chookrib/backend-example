from datetime import datetime


def to_datetime_or_none(value) -> datetime | None:
    if value is None:
        return None
    if isinstance(value, datetime):
        return value
    try:
        return datetime.fromisoformat(value)
    except Exception:
        return None


def to_datetime_req(value) -> datetime:
    dt = to_datetime_or_none(value)
    if dt is None:
        raise Exception(f"无效的日期时间格式")
    return dt


def to_datetime_or_default(value, default: datetime) -> datetime:
    value = to_datetime_or_none(value)
    if value is None:
        return default
    return value
