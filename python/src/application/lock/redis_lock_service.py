import asyncio
import logging
import time
import uuid
from contextlib import asynccontextmanager
from typing import AsyncGenerator

import redis.asyncio as redis

from src.application.application_exception import ApplicationException
from src.application.lock.lock_service import LockService

logger = logging.getLogger(__name__)

# 用于安全释放锁的 Lua 脚本
# 确保只有持有正确 value 的客户端才能删除锁，避免误删
RELEASE_LOCK_SCRIPT = """
if redis.call("get", KEYS[1]) == ARGV[1] then
    return redis.call("del", KEYS[1])
else
    return 0
end
"""


class RedisLockService(LockService):
    """使用 Redis 实现的分布式锁服务，适用于多进程或多服务器的分布式环境"""

    def __init__(self, redis_client: redis.Redis):
        self.redis = redis_client
        # 注册 Lua 脚本以提高效率
        self._release_script = self.redis.register_script(RELEASE_LOCK_SCRIPT)

    @asynccontextmanager
    async def lock(
            self,
            key: str,
            timeout: float = 30.0
    ) -> AsyncGenerator[None, None]:
        """
        获取一个分布式锁

        :param key: 锁标识
        :param timeout: 获取锁的超时时间（秒）
        """
        lease_time: int = 30    # 锁的租约时间（秒）。锁会自动在这个时间后释放，防止死锁
        poll_interval: float = 0.1  # 尝试获取锁的轮询间隔（秒）

        lock_key = f"lock:{key}"
        lock_value = str(uuid.uuid4())

        start_time = time.monotonic()
        acquired = False

        while (time.monotonic() - start_time) < timeout:
            # 尝试以原子方式设置 key (SET key value NX PX milliseconds)
            # NX: 只在 key 不存在时设置
            # PX: 设置过期时间（毫秒）
            if await self.redis.set(lock_key, lock_value, nx=True, px=lease_time * 1000):
                acquired = True
                break
            await asyncio.sleep(poll_interval)

        if not acquired:
            # raise TimeoutError(f"获取 redis 锁超时: {key}")
            raise ApplicationException(f"获取 redis 锁超时: {key}")

        try:
            logger.info(f"获取 redis 锁成功: {key}")
            yield
        finally:
            # 使用 Lua 脚本原子地检查并释放锁
            await self._release_script(keys=[lock_key], args=[lock_value])
            logger.info(f"释放 redis 锁成功: {key}")
