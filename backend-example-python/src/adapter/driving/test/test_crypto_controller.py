import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_value_resolver
from src.adapter.driving.result import Result
from src.utility import crypto_utility, json_utility
from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()

@router.post("/api/test/crypto/jwt-encode")
async def test_crypto_jwt_encode(request: Request):
    """JWT 编码"""
    json_extractor = await request_value_resolver.build_json_extractor(request)
    secret = value_utility.require_not_blank(json_extractor.get_string("secret"), "密钥不能为空")
    expires_at = value_utility.to_datetime_or_throw(json_extractor.get_string("expiresAt"), "到期时间无效")
    payload = json_extractor.get_node("payload")
    token = crypto_utility.jwt_encode(payload, secret, expires_at)
    return Result.ok(data={"token": token})

@router.post("/api/test/crypto/jwt-decode")
async def test_crypto_jwt_decode(request: Request):
    """JWT 解码"""
    json_extractor = await request_value_resolver.build_json_extractor(request)
    secret = value_utility.require_not_blank(json_extractor.get_string("secret"), "密钥不能为空")
    token = value_utility.require_not_blank(json_extractor.get_string("token"), "令牌不能为空")
    payload = crypto_utility.jwt_decode(token, secret)

    header_decoded = ""
    payload_decoded = ""
    token_parts = token.split('.')
    if len(token_parts) > 1:
        header_decoded = crypto_utility.base64_decode(token_parts[0])
    if len(token_parts) > 2:
        payload_decoded = crypto_utility.base64_decode(token_parts[1])

    return Result.ok(data={
        "payload": json_utility.serialize(payload),
        "headerDecoded": header_decoded,
        "payloadDecoded": payload_decoded,
    })

@router.get("/api/test/crypto/base64-encode")
def test_crypto_base64_encode(request: Request):
    """BASE64 编码"""
    text = value_utility.empty_if_none(request.query_params.get("text"))
    base64 = crypto_utility.base64_encode(text)
    return Result.ok(data={"base64": base64})

@router.get("/api/test/crypto/base64-decode")
def test_crypto_base64_decode(request: Request):
    """BASE64 解码"""
    base64 = value_utility.empty_if_none(request.query_params.get("base64"))
    text = crypto_utility.base64_decode(base64)
    return Result.ok(data={"text": text})

@router.get("/api/test/crypto/md5-encode")
def test_crypto_md5_encode(request: Request):
    """MD5 编码"""
    text = value_utility.empty_if_none(request.query_params.get("text"))
    md5 = crypto_utility.md5_encode(text)
    return Result.ok(data={"md5": md5})


