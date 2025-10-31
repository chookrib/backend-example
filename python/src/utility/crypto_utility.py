import hashlib
from datetime import datetime
from typing import Any

from jose import jwt


def encode_jwt(key: str, exp: datetime, claims: dict[str, Any] | None = None) -> str:
    """JWT编码"""
    if claims is None:
        claims = {}
    claims["exp"] = datetime.timestamp(exp)
    return jwt.encode(claims=claims, key=key)


def decode_jwt(token: str, key: str) -> dict[str, Any]:
    """JWT解码"""
    payload = jwt.decode(token, key)
    if "exp" not in payload or datetime.fromtimestamp(payload["exp"]) < datetime.now():
        raise Exception("JWT令牌已过期")
    return payload


def encode_md5(string: str) -> str:
    """MD5编码"""
    return hashlib.md5(string.encode('utf-8')).hexdigest()
