import logging
import subprocess

from fastapi import APIRouter, Response

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/.well-known/info")
def info():
    """应用信息，显示非涉密信息"""
    commit_info = subprocess.check_output(
        ["git", "log", "-1", "--pretty=format:%h%d%n%ad"], encoding="utf-8"
    ).strip()
    return Response(content=commit_info, media_type="text/plain")


@router.get("/.well-known/test-exception")
def test_exception():
    """测试异常处理"""
    try:
        1 / 0
    except Exception as e:
        raise Exception("测试Exception") from e
