import logging

from fastapi import APIRouter, Response

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/.well-known/test/exception")
def test_exception():
    """测试异常处理"""
    try:
        1 / 0
    except Exception as e:
        raise Exception("测试Exception") from e
