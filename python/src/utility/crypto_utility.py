import base64
import hashlib
import re
from datetime import datetime
from typing import Any

from jose import jwt

from src.utility.utility_exception import UtilityException


def jwt_encode(payload: dict[str, Any] | None, secret: str, expires_at: datetime) -> str:
    """JWT 编码"""
    if payload is None:
        payload = {}
    payload["exp"] = int(datetime.timestamp(expires_at))
    return jwt.encode(claims=payload, key=secret, algorithm="HS256")


def jwt_decode(token: str, secret: str) -> dict[str, Any]:
    """JWT 解码"""
    try:
        payload = jwt.decode(token=token, key=secret, algorithms=["HS256"])
    except Exception as ex:
        raise UtilityException(f"JWT 解码失败") from ex
    # jwt.decode 时会判断 exp 是否过期
    # if "exp" not in payload or datetime.fromtimestamp(payload["exp"]) < datetime.now():
    #     raise UtilityException("JWT 已过期")
    return payload


def jwt_expires_minute(expires: str) -> int:
    """JWT 计算过期时长（分钟）"""
    match = re.match(r'^(\d+)([dhm])$', expires.lower())
    if not match:
        raise UtilityException("JWT EXPIRES 配置错误，值应为 时长（正整数）加时长单位（d/h/m）")

    try:
        num = int(match.group(1))
    except Exception as ex:
        raise UtilityException("JWT EXPIRES 配置错误，时长应为正整数") from ex

    unit = match.group(2)
    if unit == "d":
        return num * 24 * 60
    if unit == "h":
        return num * 60
    return num


def base64_encode(text: str) -> str:
    """BASE64 编码"""
    return base64.b64encode(text.encode('utf-8')).decode('utf-8')


def base64_decode(base64_text: str) -> str:
    """BASE64 解码"""
    padding = '=' * (-len(base64_text) % 4)
    return base64.b64decode((base64_text + padding).encode('utf-8')).decode('utf-8')


def md5_encode(text: str) -> str:
    """MD5 编码"""
    return hashlib.md5(text.encode('utf-8')).hexdigest()
