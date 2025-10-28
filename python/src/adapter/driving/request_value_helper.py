from datetime import datetime, date, time
from decimal import Decimal

from starlette.requests import Request

from src.adapter.driving.controller_exception import ControllerException
from src.utility import value_utility


async def get_request_json(request: Request):
    """获取请求体 json 数据"""
    try:
        return await request.json()
    except:
        raise ControllerException("请求体不是合法的JSON格式")


# ======================================================================================================================

def get_request_json_value(json, *keys: str):
    """获取请求 json 数据中的值，失败返回 None"""
    if not keys:
        return None
    value = json
    for key in keys:
        value = json.get(key)
        if value is None:
            return None
    return value


def get_request_json_value_req(json, *keys: str):
    """获取请求 json 数据中的值，失败抛出异常"""
    value = get_request_json_value(json, *keys)
    if value is None:
        raise ControllerException(f"请求体缺少 {'.'.join(keys)} 值")
    return value


# ======================================================================================================================

def get_request_json_string_trim(json, default: str, *keys: str) -> str:
    """获取请求 json 数据中 string 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    if value is None:
        return default
    return str(value).strip()


def get_request_json_string_trim_or_empty(json, *keys: str) -> str:
    """获取请求 json 数据中 string 值，失败返回空字符串"""
    return get_request_json_string_trim(json, "", *keys)


def get_request_json_string_trim_req(json, *keys: str) -> str:
    """获取请求 json 数据中 string 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    return str(value).strip()


# ======================================================================================================================

def get_request_json_bool(json, default: bool, *keys: str) -> bool:
    """获取请求 json 数据中 bool 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_bool_or_default(value, default)


def get_request_json_bool_req(json, *keys: str) -> bool:
    """获取请求 json 数据中 bool 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    b = value_utility.to_bool_or_none(value)
    if b is None:
        raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 bool")
    return b


# ======================================================================================================================

def get_request_json_int(json, default: int, *keys: str) -> int:
    """获取请求 json 数据中 int 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_int_or_default(value, default)


def get_request_json_int_req(json, *keys: str) -> int:
    """获取请求 json 数据中 int 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    i = value_utility.to_int_or_none(value)
    if i is None:
        raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 int")
    return i


# ======================================================================================================================

def get_request_json_decimal(json, default: Decimal, *keys: str) -> Decimal:
    """获取请求 json 数据中 decimal 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_decimal_or_default(value, default)


def get_request_json_decimal_req(json, *keys: str) -> Decimal:
    """获取请求 json 数据中 decimal 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    d = value_utility.to_decimal_or_none(value)
    if d is None:
        raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 decimal")
    return d


# ======================================================================================================================

def get_request_json_datetime(json, default: datetime, *keys: str) -> datetime:
    """获取请求 json 数据中 datetime 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_datetime_or_default(value, default)


def get_request_json_datetime_req(json, *keys: str) -> datetime:
    """获取请求 json 数据中 datetime 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    dt = value_utility.to_datetime_or_none(value)
    if dt is None:
        raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 datetime")
    return dt


# ======================================================================================================================

def get_request_json_date(json, default: date, *keys: str) -> date:
    """获取请求 json 数据中 date 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_date_or_default(value, default)


def get_request_json_date_req(json, *keys: str) -> date:
    """获取请求 json 数据中 date 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    d = value_utility.to_date_or_none(value)
    if d is None:
        raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 date")
    return d


# ======================================================================================================================

def get_request_json_time(json, default: time, *keys: str) -> time:
    """获取请求 json 数据中 time 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_time_or_default(value, default)


def get_request_json_time_req(json, *keys: str) -> time:
    """获取请求 json 数据中 time 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    t = value_utility.to_time_or_none(value)
    if t is None:
        raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 time")
    return t


# ======================================================================================================================

def fix_paging(page_num: int, page_size: int, total_count: int) -> tuple[int, int, int]:
    """验证并修正分页参数"""

    if page_size < 1:
        page_size = 1
    if total_count < 0:
        total_count = 0

    max_page_num = total_count // page_size
    if total_count % page_size > 0:
        max_page_num += 1

    if page_num > max_page_num:
        page_num = max_page_num
    if page_num < 1:
        page_num = 1

    return page_num, page_size, total_count  # 与入参顺序一致
