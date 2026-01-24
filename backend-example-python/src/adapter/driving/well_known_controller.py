import logging
import os
import subprocess
from datetime import datetime

from fastapi import APIRouter, Response

from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()

start_time: datetime = datetime.now()


@router.get("/api/.well-known")
def well_known():
    """应用信息"""
    commit_info = subprocess.check_output(
        ["git", "log", "-1", "--pretty=format:%h", "--date=iso"],
        # ["git", "log", "-1", "--pretty=format:%h%d%n%cd"],
        # ["git", "rev-parse", "--short", "HEAD"],
        encoding="utf-8"
    ).strip()
    content = ("Git-Commit-Id-Abbrev:: " + commit_info + os.linesep +
               "Start-Time: " + value_utility.format_datetime(start_time))
    return Response(content=content, media_type="text/plain")
