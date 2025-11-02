import asyncio
import logging
from collections import defaultdict
from contextlib import asynccontextmanager
from typing import AsyncGenerator

from src.accessor import accessor
from src.application.application_exception import ApplicationException
from src.application.lock.lock_exception import LockException
from src.application.lock.lock_service import LockService
from src.config import settings

logger = logging.getLogger(__name__)


class AsyncioLockService(LockService):
    """
    使用 asyncio.Lock 实现的锁服务，单个 Python 进程内有效，适用于单机部署且无多进程的场景
    """

    def __init__(self):
        # 使用 defaultdict 来为每个新的 key 自动创建一个 asyncio.Lock
        self._locks = defaultdict(asyncio.Lock)
        # 这个锁用于保护对 _locks 字典的并发访问，防止竞态条件
        self._internal_lock = asyncio.Lock()

    @asynccontextmanager
    async def lock(self, key: str, timeout: float = 10.0) -> AsyncGenerator[None, None]:
        """
        获取一个进程内锁

        :param key: 锁标识
        :param timeout: 获取锁的超时时间（秒）
        """
        async with self._internal_lock:
            resource_lock = self._locks[key]

        try:
            await asyncio.wait_for(resource_lock.acquire(), timeout=timeout)
            if accessor.app_is_dev:
                logger.info(f"获取 asyncio 锁 {key} 成功")
            yield
        except asyncio.TimeoutError:
            # raise TimeoutError(f"获取 asyncio 锁 {key} 超时")
            raise LockException(f"获取 asyncio 锁 {key} 超时")
        finally:
            resource_lock.release()
            if accessor.app_is_dev:
                logger.info(f"释放 asyncio 锁 {key} 成功")
