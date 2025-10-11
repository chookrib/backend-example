import logging

import redis
import threading
from typing import ContextManager

from src.application.lock.sync_lock_service import SyncLockService

logger = logging.getLogger(__name__)

class SyncRedisLockService(SyncLockService):
    """基于 redis-py 的分布式锁管理器"""

    def __init__(self, redis_client: redis.Redis):
        self._redis_client = redis.Redis(
            host=config.REDIS_CONFIG['host'],
            port=config.REDIS_CONFIG['port'],
            db=config.REDIS_CONFIG['db'],
            decode_responses=True # 方便调试
        )
        print("Initialized RedisLockManager.")

    def lock(self, key: str, timeout: int = 30) -> ContextManager[None]:
        """
        获取一个 Redis 分布式锁。
        返回类型现在与基类保持一致。

        :param key: 锁的key
        :param timeout: 锁的过期时间，防止死锁
        :return: redis.lock.Lock 对象
        """
        print(f"Thread [{threading.current_thread().name}] trying to acquire redis lock for key: {key}")
        return self._redis_client.lock(key, timeout=timeout, blocking_timeout=30, thread_local=False)