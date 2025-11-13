import logging
from fastapi import HTTPException

from fastapi import APIRouter

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/api/test/exception")
def test_exception():
    """测试异常"""
    try:
        1 / 0
    except Exception as ex:
        raise Exception("测试Exception") from ex

@router.get("/api/test/exception/http")
def test_exception_http():
    """测试异常"""
    raise HTTPException(status_code=500, detail="测试 HTTPException")
