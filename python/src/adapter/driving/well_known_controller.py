import logging
import subprocess
from datetime import datetime, timedelta

from fastapi import APIRouter, Response

from src.adapter.driving.result import Result
from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/.well-known/info")
def info():
    """应用信息"""
    commit_info = subprocess.check_output(
        ["git", "log", "-1", "--pretty=format:%h%d%n%ad"],
        # ["git", "rev-parse", "--short", "HEAD"],
        encoding="utf-8"
    ).strip()
    return Response(content=commit_info, media_type="text/plain")

@router.get("/.well-known/test")
def test():
    """测试"""
    # date = datetime.now().date() + timedelta(days=1)
    date = value_utility.to_time_or_none("12:12:12")
    return Result.ok(data=date)
