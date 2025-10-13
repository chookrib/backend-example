from abc import ABC, abstractmethod
from typing import AsyncContextManager
from contextlib import AbstractAsyncContextManager

class LockService(ABC):
    """锁 Service 接口"""

    @abstractmethod
    def lock(self, lock_key: str) -> AbstractAsyncContextManager:
    # def lock(self, lock_key: str) -> AsyncContextManager[None]:
        """
        根据给定的 lock_key 获取一个异步锁，返回异步上下文管理器，以便使用 'async with' 语句

        :param lock_key: 锁标识
        :return: 异步上下文管理器
        """
        pass
