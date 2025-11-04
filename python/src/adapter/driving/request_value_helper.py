from datetime import datetime, date, time
from decimal import Decimal

from starlette.requests import Request

from src.adapter.driving.controller_exception import ControllerException
from src.utility import value_utility


async def get_request_json(request: Request):
    """获取请求体 json 数据"""
    try:
        return await request.json()
    except Exception as ex:
        raise ControllerException("请求体不是合法的JSON格式") from ex


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

def get_request_json_string_trim_or_default(json, default: str, *keys: str) -> str:
    """获取请求 json 数据中 string 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    if value is None:
        return default
    return str(value).strip()


def get_request_json_string_trim_or_empty(json, *keys: str) -> str:
    """获取请求 json 数据中 string 值，失败返回空字符串"""
    return get_request_json_string_trim_or_default(json, "", *keys)


def get_request_json_string_trim_req(json, *keys: str) -> str:
    """获取请求 json 数据中 string 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    s = str(value).strip()
    if value_utility.is_blank(s):
        raise ControllerException(f"请求体 {'.'.join(keys)} 值不能为空")
    return s


def get_request_json_string_trim_list(json, *keys: str) -> list[str]:
    """获取请求 json 数据中 string 数组值"""
    value = get_request_json_value(json, *keys)
    if value is not None and isinstance(value, list):
        return [str(v).strip() for v in value]
    return []


# ======================================================================================================================

def get_request_json_bool_or_none(json, *keys: str) -> bool | None:
    """获取请求 json 数据中 bool 值，失败返回 None"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_bool_or_none(value)


def get_request_json_bool_or_default(json, default: bool, *keys: str) -> bool:
    """获取请求 json 数据中 bool 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_bool_or_default(value, default)


def get_request_json_bool_req(json, *keys: str) -> bool:
    """获取请求 json 数据中 bool 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    b = value_utility.to_bool_or_none(value)
    if b is not None:
        return b
    raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 bool")


# ======================================================================================================================

def get_request_json_int_or_none(json, *keys: str) -> int | None:
    """获取请求 json 数据中 int 值，失败返回 None"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_int_or_none(value)


def get_request_json_int_or_default(json, default: int, *keys: str) -> int:
    """获取请求 json 数据中 int 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_int_or_default(value, default)


def get_request_json_int_req(json, *keys: str) -> int:
    """获取请求 json 数据中 int 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    i = value_utility.to_int_or_none(value)
    if i is not None:
        return i
    raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 int")


# ======================================================================================================================

def get_request_json_decimal_or_none(json, *keys: str) -> Decimal | None:
    """获取请求 json 数据中 decimal 值，失败返回 None"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_decimal_or_none(value)


def get_request_json_decimal_or_default(json, default: Decimal, *keys: str) -> Decimal:
    """获取请求 json 数据中 decimal 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_decimal_or_default(value, default)


def get_request_json_decimal_req(json, *keys: str) -> Decimal:
    """获取请求 json 数据中 decimal 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    d = value_utility.to_decimal_or_none(value)
    if d is not None:
        return d
    raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 decimal")


# ======================================================================================================================

def get_request_json_datetime_or_none(json, *keys: str) -> datetime | None:
    """获取请求 json 数据中 datetime 值，失败返回 None"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_datetime_or_none(value)


def get_request_json_datetime_or_default(json, default: datetime, *keys: str) -> datetime:
    """获取请求 json 数据中 datetime 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_datetime_or_default(value, default)


def get_request_json_datetime_req(json, *keys: str) -> datetime:
    """获取请求 json 数据中 datetime 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    dt = value_utility.to_datetime_or_none(value)
    if dt is not None:
        return dt
    raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 datetime")


# ======================================================================================================================

def get_request_json_date_or_none(json, *keys: str) -> date | None:
    """获取请求 json 数据中 date 值，失败返回 None"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_date_or_none(value)


def get_request_json_date_or_default(json, default: date, *keys: str) -> date:
    """获取请求 json 数据中 date 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_date_or_default(value, default)


def get_request_json_date_req(json, *keys: str) -> date:
    """获取请求 json 数据中 date 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    d = value_utility.to_date_or_none(value)
    if d is not None:
        return d
    raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 date")


# ======================================================================================================================

def get_request_json_time_or_none(json, *keys: str) -> time | None:
    """获取请求 json 数据中 time 值，失败返回 None"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_time_or_none(value)


def get_request_json_time_or_default(json, default: time, *keys: str) -> time:
    """获取请求 json 数据中 time 值，失败返回默认值"""
    value = get_request_json_value(json, *keys)
    return value_utility.to_time_or_default(value, default)


def get_request_json_time_req(json, *keys: str) -> time:
    """获取请求 json 数据中 time 值，失败抛出异常"""
    value = get_request_json_value_req(json, *keys)
    t = value_utility.to_time_or_none(value)
    if t is not None:
        return t
    raise ControllerException(f"请求体 {'.'.join(keys)} 值不是合法 time")


# ======================================================================================================================

def get_request_param_string_trim_or_default(request: Request, default: str, key: str) -> str:
    """获取请求参数中 string 值，失败返回默认值"""
    value = request.query_params.get(key)
    if value is None:
        return default
    return str(value).strip()


def get_request_param_string_trim_or_empty(request: Request, key: str) -> str:
    """获取请求参数中 string 值，失败返回空字符串"""
    return get_request_param_string_trim_or_default(request, "", key)


def get_request_param_string_trim_req(request: Request, key: str) -> str:
    """获取请求请求参数中 string 值，失败抛出异常"""
    value = request.query_params.get(key)
    if value is None:
        raise ControllerException(f"请求参数缺少 {key}")
    s = str(value).strip()
    if value_utility.is_blank(s):
        raise ControllerException(f"请求参数 {key} 值不能为空")
    return s


# ======================================================================================================================

def get_request_param_bool_or_none(request: Request, key: str) -> bool | None:
    """获取请求参数中 bool 值，失败返回 None"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_bool_or_none(value)


def get_request_param_bool_or_default(request: Request, default: bool, key: str) -> bool:
    """获取请求参数中 bool 值，失败返回默认值"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_bool_or_default(value, default)


def get_request_param_bool_req(request: Request, key: str) -> bool:
    """获取请求参数中 bool 值，失败抛出异常"""
    value = get_request_param_string_trim_req(request, key)
    b = value_utility.to_bool_or_none(value)
    if b is not None:
        return b
    raise ControllerException(f"请求参数 {key} 值不是合法 bool")


# ======================================================================================================================

def get_request_param_int_or_none(request: Request, key: str) -> int | None:
    """获取请求参数中 int 值，失败返回 None"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_int_or_none(value)


def get_request_param_int_or_default(request: Request, default: int, key: str) -> int:
    """获取请求参数中 int 值，失败返回默认值"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_int_or_default(value, default)


def get_request_param_int_req(request: Request, key: str) -> int:
    """获取请求参数中 int 值，失败抛出异常"""
    value = get_request_param_string_trim_req(request, key)
    i = value_utility.to_int_or_none(value)
    if i is not None:
        return i
    raise ControllerException(f"请求参数 {key} 值不是合法 int")


# ======================================================================================================================

def get_request_param_decimal_or_none(request: Request, key: str) -> Decimal | None:
    """获取请求参数中 decimal 值，失败返回 None"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_decimal_or_none(value)


def get_request_param_decimal_or_default(request: Request, default: Decimal, key: str) -> Decimal:
    """获取请求参数中 decimal 值，失败返回默认值"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_decimal_or_default(value, default)


def get_request_param_decimal_req(request: Request, key: str) -> Decimal:
    """获取请求参数中 decimal 值，失败抛出异常"""
    value = get_request_param_string_trim_req(request, key)
    d = value_utility.to_decimal_or_none(value)
    if d is not None:
        return d
    raise ControllerException(f"请求参数 {key} 值不是合法 decimal")


# ======================================================================================================================

def get_request_param_datetime_or_none(request: Request, key: str) -> datetime | None:
    """获取请求参数中 datetime 值，失败返回 None"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_datetime_or_none(value)


def get_request_param_datetime_or_default(request: Request, default: datetime, key: str) -> datetime:
    """获取请求参数中 datetime 值，失败返回默认值"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_datetime_or_default(value, default)


def get_request_param_datetime_req(request: Request, key: str) -> datetime:
    """获取请求参数中 datetime 值，失败抛出异常"""
    value = get_request_param_string_trim_req(request, key)
    dt = value_utility.to_datetime_or_none(value)
    if dt is not None:
        return dt
    raise ControllerException(f"请求参数 {key} 值不是合法 datetime")


# ======================================================================================================================

def get_request_param_date_or_none(request: Request, key: str) -> date | None:
    """获取请求参数中 date 值，失败返回 None"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_date_or_none(value)


def get_request_param_date_or_default(request: Request, default: date, key: str) -> date:
    """获取请求参数中 date 值，失败返回默认值"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_date_or_default(value, default)


def get_request_param_date_req(request: Request, key: str) -> date:
    """获取请求参数中 date 值，失败抛出异常"""
    value = get_request_param_string_trim_req(request, key)
    d = value_utility.to_date_or_none(value)
    if d is not None:
        return d
    raise ControllerException(f"请求参数 {key} 值不是合法 date")


# ======================================================================================================================

def get_request_param_time_or_none(request: Request, key: str) -> time | None:
    """获取请求参数中 time 值，失败返回 None"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_time_or_none(value)


def get_request_param_time_or_default(request: Request, default: time, key: str) -> time:
    """获取请求参数中 time 值，失败返回默认值"""
    value = get_request_param_string_trim_or_empty(request, key)
    return value_utility.to_time_or_default(value, default)


def get_request_param_time_req(request: Request, key: str) -> time:
    """获取请求参数中 time 值，失败抛出异常"""
    value = get_request_param_string_trim_req(request, key)
    t = value_utility.to_time_or_none(value)
    if t is not None:
        return t
    raise ControllerException(f"请求参数 {key} 值不是合法 time")


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
