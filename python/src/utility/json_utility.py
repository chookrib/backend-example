from datetime import datetime, date, time
from decimal import Decimal
from enum import Enum

from fastapi import encoders
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel


def to_camel_case(string: str) -> str:
    """键名转为 camel 风格"""
    # 删除前导下划线
    string = string.lstrip("_")
    if not string:
        return ""
    parts = string.split("_")
    return parts[0] + "".join(word.capitalize() for word in parts[1:])


# 可通过 /.well-known/test-json 查看输出测试结果
def custom_jsonable_encoder(obj, **kwargs):
    """自定义 JSON 序列化转换器"""
    # print(type(obj))

    if obj is None:
        return None

    if isinstance(obj, str):
        return obj

    if isinstance(obj, bool):
        return obj
    if isinstance(obj, int):
        if abs(obj) > 2 ** 53 - 1:  # long 转字符串
            return str(obj)
        else:
            return obj
    if isinstance(obj, Decimal):
        return str(obj)
    # datetime 继承于 date，故需先处理 datetime
    if isinstance(obj, datetime):
        return obj.strftime("%Y-%m-%d %H:%M:%S")  # 不处理默认为 yyyy-MM-dd hh:mm:ss.SSSSSS
    if isinstance(obj, date):
        return obj.strftime("%Y-%m-%d")  # 不处理默认为 yyyy-MM-dd
    if isinstance(obj, time):
        return obj.strftime("%H:%M:%S")  # 不处理默认为 hh:mm:ss.SSSSSS

    if isinstance(obj, Enum):  # 枚举需处理，否则未定义枚举类型的枚举进入 hasattr(obj, "__dict__") 会死循环
        return obj.value

    if isinstance(obj, dict):
        return {k: custom_jsonable_encoder(v, **kwargs) for k, v in obj.items()}
    if isinstance(obj, list):
        return [custom_jsonable_encoder(item, **kwargs) for item in obj]
    if isinstance(obj, tuple):
        return tuple(custom_jsonable_encoder(item, **kwargs) for item in obj)

    # 处理类，属性名转为 camel 风格
    if hasattr(obj, "__dict__"):
        return {to_camel_case(k): custom_jsonable_encoder(v, **kwargs) for k, v in obj.__dict__.items()}

    return str(obj)


# ======================================================================================================================
# 以下为用于JSON转换测试的数据定义

class TestDataEnum(int, Enum):
    """用于测试JSON转换的枚举"""
    ENUM_1 = 1
    ENUM_2 = 2
    ENUM_3 = 3


# class TestDataClass():
class TestDataClass(BaseModel):
    """用于测试JSON转换的数据类"""
    c_none: None = None
    c_str_empty: str = ""
    c_str_test: str = "test"
    c_true: bool = True
    c_false: bool = False
    # int 大小没有限制
    c_int_zero: int = 0
    c_int32_min: int = -2 ** 31  # -2147483648
    c_int32_max: int = 2 ** 31 - 1  # 2147483647
    c_int64_min: int = -2 ** 63  # -9223372036854775808
    c_int64_max: int = 2 ** 63 - 1  # 9223372036854775807   sys.maxsize
    # Decimal 大小没有限制
    c_decimal_zero: Decimal = Decimal('0')
    c_decimal: Decimal = Decimal("12.34")
    c_datetime_now: datetime = datetime.now()
    c_datetime_min: datetime = datetime.min  # 0001-01-01 00:00:00
    c_datetime_max: datetime = datetime.max  # 9999-12-31 23:59:59.999999
    c_date_today: date = date.today()
    c_date_min: date = date.min  # 0001-01-01
    c_date_max: date = date.max  # 9999-12-31
    c_time_now: time = datetime.now().time()
    c_time_min: time = time.min  # 00:00:00
    c_time_max: time = time.max  # 23:59:59.999999
    c_enum: TestDataEnum = TestDataEnum.ENUM_1


def test_data():
    """用于测试JSON转换的数据"""

    def _test_data():
        return {
            "none": None,
            "str_empty": "",
            "str_test": "test",
            "true": True,
            "false": False,
            "int_zero": 0,
            "int_32_min": -2 ** 31,
            "int_32_max": 2 ** 31 - 1,
            "int_64_min": -2 ** 63,
            "int_64_max": 2 ** 63 - 1,
            "decimal_zero": Decimal("0"),
            "decimal": Decimal("12.34"),
            "datetime_now": datetime.now(),
            "datetime_min": datetime.min,
            "datetime_max": datetime.max,
            "date_today": date.today(),
            "date_min": date.min,
            "date_max": date.max,
            "time_now": datetime.now().time(),
            "time_min": time.min,
            "time_max": time.max,
            "enum": TestDataEnum.ENUM_2,
        }

    return {
        "none": None,
        "str_empty": "",
        "str_test": "test",
        "true": True,
        "false": False,
        "int_zero": 0,
        "int_32_min": -2 ** 31,
        "int_32_max": 2 ** 31 - 1,
        "int_64_min": -2 ** 63,
        "int_64_max": 2 ** 63 - 1,
        "decimal_zero": Decimal("0"),
        "decimal": Decimal("12.34"),
        "datetime_now": datetime.now(),
        "datetime_min": datetime.min,
        "datetime_max": datetime.max,
        "date_today": date.today(),
        "date_min": date.min,
        "date_max": date.max,
        "time_now": datetime.now().time(),
        "time_min": time.min,
        "time_max": time.max,
        "enum": TestDataEnum.ENUM_3,

        "data": _test_data(),
        "dict": {"a": _test_data(), "b": _test_data()},
        "list": [_test_data(), _test_data()],
        "tuple": (_test_data(), _test_data()),

        "class": TestDataClass(),
        "classDict": {"a": TestDataClass(), "b": TestDataClass()},
        "classList": [TestDataClass(), TestDataClass()],
        "classTuple": (TestDataClass(), TestDataClass()),
    }

# print(
#     json.dumps(
#         test_data(),
#         default=custom_jsonable_encoder,
#         # default=str,
#         indent=4,
#         ensure_ascii=False
#     )
# )
