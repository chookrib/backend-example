import asyncio
import logging
from typing import AsyncContextManager

import aioredis

from src.application.application_exception import ApplicationException
from src.application.lock.lock_service import LockService

logger = logging.getLogger(__name__)

class RedisLockService(LockService):
    """基于 aioredis 的分布式锁管理器"""

    def __init__(self, redis_client: aioredis.Redis):
        self._redis_client = redis_client
        print("Initialized RedisLockManager.")

    def lock(self, lock_key: str, timeout: int = 30) -> AsyncContextManager[None]:
        """获取一个 Redis 分布式锁（异步）"""

        class RedisLockContext:
            def __init__(self, redis_client, key, timeout):
                self.redis_client = redis_client
                self.key = key
                self.timeout = timeout
                self.locked = False

            async def __aenter__(self):
                self.locked = await self.redis_client.set(
                    self.key, "locked", expire=self.timeout, exist=aioredis.SET_IF_NOT_EXIST
                )
                current_task = asyncio.current_task()
                if not self.locked:
                    raise ApplicationException(f"协程 [{current_task.get_name() if current_task else ''}] 无法获得 redis 锁: {self.key}")

                logger.info(f"协程 [{current_task.get_name() if current_task else ''}] 获得 redis 锁: {self.key}")
                return None

            async def __aexit__(self, exc_type, exc, tb):
                if self.locked:
                    await self.redis_client.delete(self.key)
                    current_task = asyncio.current_task()
                    logger.info(f"协程 [{current_task.get_name() if current_task else ''}] 释放 redis 锁: {self.key}")

        return RedisLockContext(self._redis_client, lock_key, timeout)

