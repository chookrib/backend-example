from abc import ABC, abstractmethod
from typing import AsyncContextManager, AsyncGenerator
from contextlib import AbstractAsyncContextManager, asynccontextmanager


class LockService(ABC):
    """锁 Service 接口"""

    @abstractmethod
    @asynccontextmanager
    async def lock(self, key: str, timeout: float = 30.0) -> AsyncGenerator[None, None]:
    # def lock(self, key: str) -> AbstractAsyncContextManager:
    # def lock(self, key: str) -> AsyncContextManager[None]:
        """
        获取一个特定 key 的锁。返回一个异步上下文管理器

        使用方法: async with lock_service.lock("my-resource-key"):

        :param key: 锁标识
        :param timeout: 获取锁的超时时间（秒）
        """
        yield
