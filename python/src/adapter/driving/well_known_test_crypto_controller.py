import logging

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
    payload_string = json_utility.serialize(payload)
    return Result.ok(data={"payload": payload_string})

@router.get("/.well-known/test/crypto/md5-encode")
def test_crypto_md5_encode(request: Request):
    """MD5 编码"""
    text = request_value_helper.get_request_param_string_trim_or_empty(request, "text")
    result = crypto_utility.md5_encode(text)
    return Result.ok(data={"result": result})


