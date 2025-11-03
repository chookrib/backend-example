import asyncio
import logging
import os
import threading
import time

from src.application.lock import lock_keys
from src.application.lock.lock_service import LockService

logger = logging.getLogger(__name__)

class TestLockService:
    """锁测试 Service"""

    def __init__(self, lock_service: LockService):
        self.lock_service = lock_service
        self.count = 0

    def set_count(self, value: int):
        """设置 count"""
        self.count = value
        logger.info(f"进程 {os.getpid()} 线程 {threading.get_ident()} 设置 count = {value}")

    def sync_decrease_count(self):
        """同步减少 count，不加锁"""
        c = self.count
        time.sleep(2)  # 需要 sleep，否则看不到竞态条件冲突效果
        if c > 0:
            self.count = self.count - 1
            logger.info(f"进程 {os.getpid()} 线程 {threading.get_ident()} 减少 count 成功: {c} - 1 = {self.count}")
        else:
            logger.info(f"进程 {os.getpid()} 线程 {threading.get_ident()} 减少 count 失败: {c}")

    async def async_decrease_count(self):
        """异步减少 count，不加锁"""
        c = self.count
        await asyncio.sleep(0.1)    # 需要 sleep，否则看不到竞态条件冲突效果
        if c > 0:
            self.count = self.count - 1
            logger.info(f"进程 {os.getpid()} 线程 {threading.get_ident()} 减少 count 成功: {c} - 1 = {self.count}")
        else:
            logger.info(f"进程 {os.getpid()} 线程 {threading.get_ident()} 减少 count 失败: {c}")

    async def sync_decrease_count_with_async_lock(self):
        """同步减少 count，加异步锁"""
        async with self.lock_service.lock(lock_keys.TEST):
            self.sync_decrease_count()

    async def async_decrease_count_with_async_lock(self):
        """异步减少 count，加异步锁"""
        async with self.lock_service.lock(lock_keys.TEST):
           await  self.async_decrease_count()

    # ==================================================================================================================

    async def asyncio_sleep(self):
        """asyncio.sleep"""
        async with self.lock_service.lock(lock_keys.TEST):
            await asyncio.sleep(10)




