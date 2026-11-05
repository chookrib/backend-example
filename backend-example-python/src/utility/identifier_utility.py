"""Id Utility"""

import uuid

import uuid_utils

from src.utility import value_utility
from src.utility.utility_exception import UtilityException


def generate_uuid_v4() -> str:
    """生成 UUID v4"""
    return str(uuid.uuid4())


def generate_uuid_v7() -> str:
    """生成 UUID v7"""
    return str(uuid_utils.uuid7())


# Id 界定符，指定一个 Id 中不存在的字符作为多个 Id 拼接界定符
DELIMITER = ","


def wrap_join(ids: list[str] | None) -> str:
    """拼接 Id 字符串"""
    if ids is None or len(ids) == 0:
        return ""

    s = ""
    for id in ids:
        if not value_utility.is_blank(id):
            s += id + DELIMITER

    if not value_utility.is_blank(s):
        return DELIMITER + s
    else:
        return ""


def wrap(id: str) -> str:
    """包裹 Id 字符串"""
    if value_utility.is_blank(id):
        raise UtilityException("不能包裹一个空的 Id")

    return DELIMITER + id + DELIMITER


def split(ids: str | None) -> list[str]:
    """拆分 Id 字符串"""
    if value_utility.is_blank(ids):
        return []

    return [id for id in str(ids).split(DELIMITER) if not value_utility.is_blank(id)]
