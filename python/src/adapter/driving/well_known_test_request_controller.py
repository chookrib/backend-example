import logging

from fastapi import APIRouter, Request

from src.adapter.driving.result import Result

logger = logging.getLogger(__name__)
router = APIRouter()


@router.post("/.well-known/test/request/string")
async def test_request_string(request: Request):
    """测试请求，获取请求体字符串"""
    request_body = await request.body()
    request_string = request_body.decode()
    return Result.ok(data=request_string)

@router.post("/.well-known/test/request/json")
async def test_request_json(request: Request):
    """测试请求，获取请求体 json"""
    request_json = await request.json()     # 无法解析JSON时报错: "Expecting value: line 1 column 1 (char 0)"
    return Result.ok(data=request_json)

@router.post("/.well-known/test/request/get")
async def test_request_get(request: Request):
    """测试请求，获取请求体 json 中的字段"""
    request_json = await request.json()
    test = str(request_json.get("test", "")).strip()
    return Result.ok(data=test)
