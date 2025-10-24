import logging

from fastapi import APIRouter, Response, Request

from src.adapter.driving.result import Result

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/.well-known/test/request/string")
async def test_request_string(request: Request):
    """测试请求"""
    request_body = await request.body()
    request_string = request_body.decode()
    return Result.ok(data=request_string)

@router.post("/.well-known/test/request/json")
async def test_request_json(request: Request):
    """测试请求"""
    request_json = await request.json()     # 无法解析JSON时报错: "Expecting value: line 1 column 1 (char 0)"
    return Result.ok(data=request_json)
