import logging
from email.quoprimime import header_decode

from fastapi import APIRouter, Request

from src.adapter.driving import request_value_helper
from src.adapter.driving.result import Result
from src.utility import crypto_utility, json_utility

logger = logging.getLogger(__name__)
router = APIRouter()

@router.post("/.well-known/test/crypto/jwt-encode")
async def test_crypto_jwt_encode(request: Request):
    """JWT 编码"""
    request_json = await request.json()
    secret = request_value_helper.get_request_json_string_trim_req(request_json, "secret")
    expires_at = request_value_helper.get_request_json_datetime_req(request_json, "expiresAt")
    payload = request_json.get("payload", {})
    token = crypto_utility.jwt_encode(payload, secret, expires_at)
    return Result.ok(data={"token": token})

@router.post("/.well-known/test/crypto/jwt-decode")
async def test_crypto_jwt_decode(request: Request):
    """JWT 解码"""
    request_json = await request.json()
    secret = request_value_helper.get_request_json_string_trim_req(request_json, "secret")
    token = request_value_helper.get_request_json_string_trim_req(request_json, "token")
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

@router.get("/.well-known/test/crypto/base64-encode")
def test_crypto_base64_encode(request: Request):
    """BASE64 编码"""
    text = request_value_helper.get_request_param_string_trim_or_empty(request, "text")
    base64 = crypto_utility.base64_encode(text)
    return Result.ok(data={"base64": base64})

@router.get("/.well-known/test/crypto/base64-decode")
def test_crypto_base64_decode(request: Request):
    """BASE64 解码"""
    base64 = request_value_helper.get_request_param_string_trim_or_empty(request, "base64")
    text = crypto_utility.base64_decode(base64)
    return Result.ok(data={"text": text})

@router.get("/.well-known/test/crypto/md5-encode")
def test_crypto_md5_encode(request: Request):
    """MD5 编码"""
    text = request_value_helper.get_request_param_string_trim_or_empty(request, "text")
    md5 = crypto_utility.md5_encode(text)
    return Result.ok(data={"md5": md5})


