import asyncio

from src.application.lock import lock_keys
from src.application.lock.lock_service import LockService


class TestLockService:
    """锁测试 Service"""

    def __init__(self, lock_service: LockService):
        self.lock_service = lock_service
        self.count = 10

    async def reduce_unsafe(self):
        """扣减测试，不加锁"""
        current = self.count
        await asyncio.sleep(0.01)   # 增加延迟，模拟并发竞争
        if current > 0:
            self.count = current - 1
            print(f"扣减后 count = {self.count}")
        else:
            print(f"无法扣减 count = {self.count}")

    async def reduce_safe(self):
        """扣减测试，加锁"""
        async with self.lock_service.lock(lock_keys.TEST):
            await self.reduce()

    async def lock_sleep(self):
        """加锁等待测试"""
        async with self.lock_service.lock(lock_keys.TEST):
            await asyncio.sleep(10)




