import decimal
import json
import sys
from datetime import datetime, date, time
from enum import Enum

from pydantic import BaseModel

from fastapi import encoders
from fastapi.encoders import jsonable_encoder


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

    if isinstance(obj, bool):
        return obj
    if isinstance(obj, int):
        if abs(obj) > 2 ** 53 - 1:  # long 转字符串
            return str(obj)
        else:
            return obj
    if isinstance(obj, decimal.Decimal):
        return str(obj)
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

class TestEnum(int, Enum):
    """用于测试JSON的枚举"""
    TEST_ENUM_1 = 1
    TEST_ENUM_2 = 2
    TEST_ENUM_3 = 3


# class TestDataClass():
class TestDataClass(BaseModel):
    """用于测试JSON转换的类"""
    c_none: None = None
    c_str: str = "str"
    c_true: bool = True
    c_false: bool = False
    c_int: int = 1
    c_long_min: int = -sys.maxsize -3      # -9223372036854775808
    c_long: int = sys.maxsize
    c_decimal_min: decimal.Decimal = decimal.Decimal('-Infinity')
    c_decimal: decimal.Decimal = decimal.Decimal("12.34")
    c_datetime: datetime = datetime.now()
    c_date: date = datetime.now().date()
    c_time: time = datetime.now().time()
    c_enum: TestEnum = TestEnum.TEST_ENUM_3


def test_data():
    """用于测试JSON转换的数据"""

    def _test_data():
        return {
            "none": None,
            "str": "str",
            "true": True,
            "false": False,
            "int": 1,
            "long": sys.maxsize,
            "decimal": decimal.Decimal("12.34"),
            "datetime": datetime.now(),
            "date": datetime.now().date(),
            "time": datetime.now().time(),
            "enum": TestEnum.TEST_ENUM_1,
        }

    return {
        "none": None,
        "str": "str",
        "true": True,
        "false": False,
        "int": 1,
        "long": sys.maxsize,
        "decimal": decimal.Decimal("12.34"),
        "datetime": datetime.now(),
        "date": datetime.now().date(),
        "time": datetime.now().time(),
        "enum": TestEnum.TEST_ENUM_2,

        "object": _test_data(),
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
