import logging
import subprocess
from datetime import datetime, timedelta

from fastapi import APIRouter, Response

from src.adapter.driving.result import Result
from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()


@router.get("/api/.well-known")
def well_known():
    """应用信息"""
    commit_info = subprocess.check_output(
        ["git", "log", "-1", "--pretty=format:%h%d%n%ad"],
        # ["git", "rev-parse", "--short", "HEAD"],
        encoding="utf-8"
    ).strip()
    return Response(content=commit_info, media_type="text/plain")


