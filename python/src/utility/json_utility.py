import json
from datetime import datetime, date, time
from decimal import Decimal
from enum import Enum


def deserialize(data):
    """反序列化 JSON 字符串"""
    return json.loads(data)

def serialize(data):
    """序列化为 JSON 字符串"""
    return json.dumps(data)

def to_camel_case(string: str) -> str:
    """键名转为 camel 风格"""
    # 删除前导下划线
    string = string.lstrip("_")
    if not string:
        return ""
    parts = string.split("_")
    return parts[0] + "".join(word.capitalize() for word in parts[1:])


# 可通过 /.well-known/test/json/dict 查看输出测试结果
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
