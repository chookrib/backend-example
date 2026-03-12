import logging

from fastapi import APIRouter

from src.adapter.driving.result import Result

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/api/test/response/json/dict")
def test_response_json_dict():
    """测试响应，dict 响应为 json"""
    return Result.ok(data=test_data())


@router.get("/api/test/response/json/class")
def test_response_json_class():
    """测试响应，class 响应为 json"""
    return Result.ok(data=TestDataClass())


# ======================================================================================================================
# 以下为用于JSON转换测试的数据定义
from datetime import datetime, date, time
from decimal import Decimal
from enum import Enum
from pydantic import BaseModel


class TestDataEnum(int, Enum):
    """测试枚举"""
    ENUM_1 = 1
    ENUM_2 = 2
    ENUM_3 = 3


# class TestDataClass():
class TestDataClass(BaseModel):
    """测试数据 class"""
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
    """生成测试数据 dict"""

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
#     json_utility.deserialize(
#         test_data(),
#         default=custom_jsonable_encoder,
#         # default=str,
#         indent=4,
#         ensure_ascii=False
#     )
# )
