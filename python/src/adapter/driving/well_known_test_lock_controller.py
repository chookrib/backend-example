import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_value_helper
from src.adapter.driving.result import Result

logger = logging.getLogger(__name__)
router = APIRouter()

from src.ioc_container import ioc_container
from src.application.lock.test_lock_service import TestLockService

test_lock_service = ioc_container.resolve(TestLockService)  # type: ignore

@router.get("/.well-known/test/lock/set-count")
def test_lock_set_count(request: Request):
    """设置 count"""
    value = request_value_helper.get_request_param_int(request, 1, "value")
    test_lock_service.set_count(value)
    return Result.ok(data={"count": value})

@router.get("/.well-known/test/lock/decrease-count")
def test_lock_decrease_count():
    """减少 count，不加锁"""
    test_lock_service.decrease_count()
    return Result.ok()


@router.get("/.well-known/test/lock/decrease-count-with-lock")
async def test_lock_decrease_count_with_lock():
    """减少 count，加锁"""
    await test_lock_service.decrease_count_with_lock()
    return Result.ok()

# ======================================================================================================================

@router.get("/.well-known/test/lock/asyncio-sleep")
async def test_lock_asyncio_sleep():
    """asyncio.sleep"""
    await test_lock_service.asyncio_sleep()
    return Result.ok()
