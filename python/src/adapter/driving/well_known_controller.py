import logging
import subprocess

from fastapi import APIRouter, Response

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
