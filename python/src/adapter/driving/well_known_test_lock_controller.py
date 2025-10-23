import logging

from fastapi import APIRouter, Response

from src.adapter.driving.result import Result

logger = logging.getLogger(__name__)
router = APIRouter()

from src.ioc_container import ioc_container
from src.application.lock.test_lock_service import TestLockService

test_lock_service = ioc_container.resolve(TestLockService)  # type: ignore


@router.get("/.well-known/test/lock/reduce-unsafe")
async def test_lock_reduce_unsafe():
    """扣减测试，不加锁"""
    await test_lock_service.reduce_unsafe()
    return Result.ok()


@router.get("/.well-known/test/lock/reduce-safe")
async def test_lock_reduce_safe():
    """扣减测试，加锁"""
    await test_lock_service.reduce_safe()
    return Result.ok()


@router.get("/.well-known/test/lock/sleep")
async def test_lock_sleep():
    """加锁等待测试"""
    await test_lock_service.lock_sleep()
    return Result.ok()
