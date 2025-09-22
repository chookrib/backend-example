from datetime import datetime, timedelta
from typing import Any

from jose import jwt, JWTError


def encode(key: str, exp: datetime, claims: dict[str, Any] | None = None) -> str:
    """编码JWT令牌"""

    if claims is None:
        claims = {}
    claims["exp"] = datetime.timestamp(exp)
    return jwt.encode(claims=claims, key=key)


def decode(token: str, key: str) -> dict[str, Any]:
    """解码JWT令牌"""
    payload = jwt.decode(token, key)
    if "exp" not in payload or datetime.fromtimestamp(payload["exp"]) < datetime.now():
        raise JWTError("JWT令牌已过期")
    return payload

