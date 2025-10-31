import logging

from fastapi import APIRouter

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/.well-known/test/exception")
def test_exception():
    """测试异常"""
    try:
        1 / 0
    except Exception as ex:
        raise Exception("测试Exception") from ex
