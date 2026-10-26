"""Id生成器"""

import uuid_utils

# Id 分隔符，指定一个 Id 中不存在的字符作为多个 Id 拼接分隔符
SEPARATOR = ","


def generate_id() -> str:
    """生成唯一Id"""
    return str(uuid_utils.uuid7())
