import asyncio
import logging

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
        logger.info(f"设置 count = {value}")

    def decrease_count(self):
        """减少 count，不加锁"""
        c = self.count
        # await asyncio.sleep(0.01)   # 增加延迟，模拟并发竞争
        if c > 0:
            self.count = self.count - 1
            logger.info(f"减少 count 成功: {c} - 1 = {self.count}")
        else:
            logger.info(f"减少 count 失败: {c}")

    async def decrease_count_with_lock(self):
        """减少 count，加锁"""
        async with self.lock_service.lock(lock_keys.TEST):
            self.decrease_count()

    # ==================================================================================================================

    async def asyncio_sleep(self):
        """asyncio.sleep"""
        async with self.lock_service.lock(lock_keys.TEST):
            await asyncio.sleep(10)




