import logging
import os
import subprocess
from datetime import datetime

from fastapi import APIRouter, Response

from src.utility import value_utility

logger = logging.getLogger(__name__)
router = APIRouter()

start_time: datetime = datetime.now()


@router.get("/.well-known/metadata")
def metadata():
    """应用信息，显示非涉密信息"""
    commit_info = subprocess.check_output(
        ["git", "log", "-1", "--pretty=format:%h", "--date=iso"],
        # ["git", "log", "-1", "--pretty=format:%h%d%n%cd"],
        # ["git", "rev-parse", "--short", "HEAD"],
        encoding="utf-8"
    ).strip()
    content = ("Git-Commit-Id-Abbrev:: " + commit_info + os.linesep +
               "Start-Time: " + value_utility.format_datetime(start_time))
    return Response(content=content, media_type="text/plain")


@router.get("/.well-known/metadata.json")
def metadata_json():
    """应用信息，显示非涉密信息，JSON格式"""
    commit_info = subprocess.check_output(
        ["git", "log", "-1", "--pretty=format:%h", "--date=iso"],
        encoding="utf-8"
    ).strip()

    return {
        "gitCommitIdAbbrev": commit_info,
        "startTime": value_utility.format_datetime(start_time)
    }
