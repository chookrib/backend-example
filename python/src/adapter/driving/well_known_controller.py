import logging
import subprocess

from fastapi import APIRouter, Response

from src.adapter.driving.result import Result
from src.utility import json_utility

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/.well-known/info")
def info():
    """应用信息，显示非涉密信息"""
    commit_info = subprocess.check_output(
        ["git", "log", "-1", "--pretty=format:%h%d%n%ad"], encoding="utf-8"
    ).strip()
    return Response(content=commit_info, media_type="text/plain")


@router.get("/.well-known/test/exception")
def test_exception():
    """测试异常处理"""
    try:
        1 / 0
    except Exception as e:
        raise Exception("测试Exception") from e


@router.get("/.well-known/test/json")
def test_json():
    """测试JSON数据输出"""
    return Result.ok(data=json_utility.test_data())


@router.get("/.well-known/test/json-class")
def test_json_class():
    """测试JSON数据输出"""
    return Result.ok(data=json_utility.TestDataClass())
