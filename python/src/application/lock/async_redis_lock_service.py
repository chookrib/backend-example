import logging
from typing import AsyncContextManager

import aioredis
import threading

from src.application.lock.async_lock_service import AsyncLockService

logger = logging.getLogger(__name__)

class AsyncRedisLockService(AsyncLockService):
    """基于 redis-py 的分布式锁管理器"""

    def __init__(self, redis_client: aioredis.Redis):
        elf._redis_client = redis_client
        print("Initialized RedisLockManager.")

    async def lock(self, key: str, timeout: int = 30) -> AsyncContextManager[None]:
        """
        获取一个 Redis 分布式锁（异步）。
        返回类型现在与基类保持一致。

        :param key: 锁的key
        :param timeout: 锁的过期时间，防止死锁
        :return: aioredis锁对象
        """
        print(f"Thread [{threading.current_thread().name}] trying to acquire redis lock for key: {key}")
        lock = await self._redis_client.lock(key, timeout=timeout)
        await lock.acquire()
        return lock