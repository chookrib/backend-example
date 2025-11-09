import logging

from fastapi import APIRouter, Request

from src.adapter.driving import request_value_helper
from src.adapter.driving.result import Result
from src.application.lock.lock_exception import LockException

logger = logging.getLogger(__name__)
router = APIRouter()

from src.ioc_container import ioc_container
from src.application.lock.test_lock_service import TestLockService

test_lock_service = ioc_container.resolve(TestLockService)  # type: ignore


@router.get("/.well-known/test/lock/set-count")
def test_lock_set_count(request: Request):
    """设置 count"""
    value = request_value_helper.get_request_param_int_or_default(request, 1, "value")
    test_lock_service.set_count(value)
    return Result.ok(data={"count": value})


@router.get("/.well-known/test/lock/sync-decrease-count-in-sync")
def test_lock_sync_decrease_count_in_sync():
    """
    同步减少 count，不加锁，在同步路由方法中调用，会超减
    并发场景下请求会同时响应
    """
    test_lock_service.sync_decrease_count()
    return Result.ok()


@router.get("/.well-known/test/lock/sync-decrease-count-in-async")
async def test_lock_sync_decrease_count_in_async():
    """
    同步减少 count，不加锁，在异步路由方法中调用，不会超减（请求会排队执行，不会并发响应）
    异步路由方法中调用同步方法时，FastAPI 会自动在线程池中运行同步方法，从而避免阻塞事件循环。如果拿到的线程如果是同一个的化，不会超减
    """
    test_lock_service.sync_decrease_count()
    return Result.ok()


@router.get("/.well-known/test/lock/async-decrease-count")
async def test_lock_async_decrease_count():
    """异步减少 count，不加锁，会超减"""
    await test_lock_service.async_decrease_count()
    return Result.ok()


@router.get("/.well-known/test/lock/sync-decrease-count-with-async-lock")
async def test_lock_sync_decrease_count_with_async_lock():
    """同步减少 count，加异步锁，不会超减（请求会排队执行，不会并发响应）"""
    await test_lock_service.sync_decrease_count_with_async_lock()
    return Result.ok()


@router.get("/.well-known/test/lock/async-decrease-count-with-async-lock")
async def test_lock_async_decrease_count_with_async_lock():
    """异步减少 count，加异步锁，不会超减"""
    await test_lock_service.async_decrease_count_with_async_lock()
    return Result.ok()


# ======================================================================================================================

@router.get("/.well-known/test/lock/asyncio-sleep")
async def test_lock_asyncio_sleep():
    """异步 asyncio.sleep"""
    await test_lock_service.asyncio_sleep()
    return Result.ok()


@router.get("/.well-known/test/lock/time-sleep")
def test_lock_time_sleep():
    """同步 time.sleep"""
    import time
    time.sleep(10)
    return Result.ok()

# ======================================================================================================================


@router.get("/.well-known/test/lock/exception")
async def test_lock_exception():
    """测试 LockException 异常"""
    raise LockException("测试 LockException 异常")
