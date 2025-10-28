import logging

from fastapi import APIRouter, Response

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/.well-known/test/response/code/500")
def test_response_code_500():
    """测试响应 500"""
    return Response(status_code=500, content="测试500错误", media_type="text/plain; charset=utf-8")

