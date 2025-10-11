"""Id生成器"""

import uuid_utils


def generate_id() -> str:
    """生成唯一Id"""
    return str(uuid_utils.uuid7())
