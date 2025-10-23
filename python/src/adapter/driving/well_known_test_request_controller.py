import logging

from fastapi import APIRouter, Response, Request

from src.adapter.driving.result import Result

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/.well-known/test/request/string")
async def test_request_string(request: Request):
    """测试请求"""
    body = await request.body()
    body_str = body.decode()
    return Result.ok(data=body_str)

@router.post("/.well-known/test/request/json")
async def test_request_json(request: Request):
    """测试请求"""
    try:
        json = await request.json()
    except:
        json = None
    return Result.ok(data=json)
