from datetime import datetime, date, time
from decimal import Decimal, ROUND_HALF_UP

from src.utility.utility_exception import UtilityException


def is_empty_string(value) -> bool:
    """判断字符串是否为 None 或空字符串"""
    if value is None:
        return True
    s = str(value).strip()  # 去空格
    return not s


# ======================================================================================================================


def to_string_or_empty(value) -> str:
    """转 string，失败返回空字符串"""
    if value is None:
        return ""
    return str(value)  # 不去空格


# def to_string_or_default(value, default: str) -> str:
#     """转 string，失败返回默认值"""
#     if value is None:
#         return default
#     return str(value)  # 不去空格


# ======================================================================================================================

# 定义 bool 值范围
BOOL_TRUE = ('true', '1', 't', 'y', 'yes', 'on')
BOOL_FALSE = ('false', '0', 'f', 'n', 'no', 'off')

def to_bool_or_none(value) -> bool | None:
    """转 bool，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, bool):
        return value
    b = str(value).strip().lower()
    if b in BOOL_TRUE:
        return True
    elif b in BOOL_FALSE:
        return False
    return None


def to_bool_or_default(value, default: bool) -> bool:
    """转 bool，失败返回默认值"""
    b = to_bool_or_none(value)
    if b is not None:
        return b
    return default


# ======================================================================================================================

def to_int_or_none(value) -> int | None:
    """转 int，失败返回 None，注意此方法限制范围与主流语言一致，超出范围的值返回 None"""
    if value is None:
        return None
    if isinstance(value, int):
        if value >= -2147483648 or value <= 2147483647:
            return value
        else:
            return None
    try:
        value_int = int(value)
        if value_int >= -2147483648 or value_int <= 2147483647:
            return value_int
        else:
            return None
    except Exception as ex:
        return None


def to_int_or_default(value, default: int) -> int:
    """转 int，失败返回默认值"""
    i = to_int_or_none(value)
    if i is not None:
        return i
    if default >= -2147483648 or default <= 2147483647:
        return default
    else:
        raise UtilityException("默认值超出 int 范围")


# ======================================================================================================================

def to_long_or_none(value) -> int | None:
    """转 long，失败返回 None，注意此方法限制范围与主流语言一致，超出范围的值返回 None"""
    if value is None:
        return None
    if isinstance(value, int):
        if value >= -9223372036854775808 or value <= 9223372036854775807:
            return value
        else:
            return None
    try:
        value_long = int(value)
        if value_long >= -9223372036854775808 or value_long <= 9223372036854775807:
            return value_long
        else:
            return None
    except Exception as ex:
        return None


def to_long_or_default(value, default: int) -> int:
    """转 int，失败返回默认值"""
    l = to_long_or_none(value)
    if l is not None:
        return l
    if default >= -9223372036854775808 or default <= 9223372036854775807:
        return default
    else:
        raise UtilityException("默认值超出 long 范围")


# ======================================================================================================================

# 定义 decimal 边界值 DECIMAL(28, 8)，满足绝大部分场景
DECIMAL_MIN_VALUE = Decimal("-99999999999999999999.99999999")
DECIMAL_MAX_VALUE = Decimal("99999999999999999999.99999999")
# 定义 decimal 精度模版
DECIMAL_QUANTIZE_EXP = Decimal('0.00000001')


def to_decimal_or_none(value) -> Decimal | None:
    """转 decimal，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, Decimal):
        if (value < DECIMAL_MIN_VALUE or value > DECIMAL_MAX_VALUE):
            return None
        else:
            return value
    try:
        # return Decimal(str(value).strip())  # 需要 str()，否则 Decimal 解析有偏差

        d = Decimal(str(value).strip())
        # 规范化小数位（四舍五入）
        d = d.quantize(DECIMAL_QUANTIZE_EXP, rounding=ROUND_HALF_UP)
        # 检查是否超出边界
        if d < DECIMAL_MIN_VALUE or d > DECIMAL_MAX_VALUE:
            return None
        return value
    except Exception as ex:
        return None


def to_decimal_or_default(value, default: Decimal) -> Decimal:
    """转 decimal，失败返回默认值"""
    d = to_decimal_or_none(value)
    if d is not None:
        return d
    return default


# ======================================================================================================================
# datetime 精度统一为毫秒

def get_datetime_now() -> datetime:
    """获取当前时间，精度到毫秒"""
    now = datetime.now()
    return now.replace(microsecond=(now.microsecond // 1000) * 1000)


def format_datetime(value: datetime) -> str:
    """格式化 datetime，精度到秒"""
    return value.strftime("%Y-%m-%d %H:%M:%S")


def format_datetime_millisecond(value: datetime) -> str:
    """格式化 datetime，精度到毫秒"""
    return value.strftime("%Y-%m-%d %H:%M:%S.%f")[:-3]


DATETIME_FORMATS = [
    "%Y-%m-%d",
    "%Y-%m-%d %H:%M",
    "%Y-%m-%d %H:%M:%S",
    "%Y-%m-%d %H:%M:%S.%f"  # %f 自动处理 1 到 6 位小数
]


def to_datetime_or_none(value) -> datetime | None:
    """转 datetime，精度到毫秒，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, datetime):
        return value.replace(microsecond=(value.microsecond // 1000) * 1000)

    # datetime.fromisoformat(str(value).strip())
    # datetime.strptime(str(value).strip(), "%Y-%m-%d %H:%M:%S")

    dt = str(value).strip()

    # 防御性处理：Python最大支持6位，超过6位进行截断
    if '.' in dt:
        main_part, frac_part = dt.split('.', 1)
        if len(frac_part) > 6:
            dt = f"{main_part}.{frac_part[:6]}"

    for format in DATETIME_FORMATS:
        try:
            valid_datetime = datetime.strptime(dt, format)
            return valid_datetime.replace(microsecond=(valid_datetime.microsecond // 1000) * 1000)
        except ValueError:
            continue
    return None


def to_datetime_or_default(value, default: datetime) -> datetime:
    """转 datetime，精度到毫秒，失败返回默认值"""
    dt = to_datetime_or_none(value)
    if dt is not None:
        return dt
    return default.replace(microsecond=(default.microsecond // 1000) * 1000)


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
    if isinstance(value, datetime):
        return value.date()

    d = str(value).strip()
    # try:
    #     # if re.match(r"^\d{4}-\d{2}-\d{2}$", d):
    #     #     return datetime.strptime(d, "%Y-%m-%d").date()
    #     # elif re.match(r"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$", d):
    #     #     return datetime.strptime(d, "%Y-%m-%d %H:%M:%S").date()
    #     # else:
    #     #     return None
    #     return date.fromisoformat(d)
    # except Exception as ex:
    #     try:
    #         return datetime.fromisoformat(d).date()
    #     except Exception as ex:
    #         return None

    if len(d) < 10:
        return None
    try:
        return date.fromisoformat(d[0:10])
    except ValueError:
        return None


def to_date_or_default(value, default: date) -> date:
    """转 date，失败返回默认值"""
    d = to_date_or_none(value)
    if d is not None:
        return d
    return default


# ======================================================================================================================
# time 精度统一为秒

def format_time(value: time) -> str:
    """格式化 time，精度到秒"""
    return value.strftime("%H:%M:%S")


# 定义所有支持的格式
TIME_FORMATS = [
    # 带日期的格式
    "%Y-%m-%d %H:%M",
    "%Y-%m-%d %H:%M:%S",
    "%Y-%m-%d %H:%M:%S.%f",  # %f 支持1-6位小数
    # 不带日期的格式
    "%H:%M",
    "%H:%M:%S",
    "%H:%M:%S.%f"  # %f 支持1-6位小数
]


def to_time_or_none(value) -> time | None:
    """转 time，精度到秒，失败返回 None"""
    if value is None:
        return None
    if isinstance(value, time):
        return value.replace(microsecond=0)
    if isinstance(value, datetime):
        return value.time().replace(microsecond=0)

    t = str(value).strip()
    # try:
    #     # if re.match(r"^\d{2}:\d{2}:\d{2}$", value_str):
    #     #     return datetime.strptime(value_str, "%H:%M:%S").time()
    #     # elif re.match(r"^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$", value_str):
    #     #     return datetime.strptime(value_str, "%Y-%m-%d %H:%M:%S").time()
    #     # else:
    #     #         return None
    #     return time.fromisoformat(value_str)
    # except Exception as ex:
    #     try:
    #         return datetime.fromisoformat(value_str).time()
    #     except Exception as ex:
    #         return None

    # 防御性处理：Python最大支持6位，超过6位进行截断
    if '.' in t:
        main_part, frac_part = t.split('.', 1)
        if len(frac_part) > 6:
            t = f"{main_part}.{frac_part[:6]}"

    for format in TIME_FORMATS:
        try:
            # 尝试解析
            dt = datetime.strptime(t, format)
            return dt.time().replace(microsecond=0)
        except ValueError:
            continue
    return None


def to_time_or_default(value, default: time) -> time:
    """转 time，精度到秒，失败返回默认值"""
    t = to_time_or_none(value)
    if t is not None:
        return t
    return default.replace(microsecond=0)
