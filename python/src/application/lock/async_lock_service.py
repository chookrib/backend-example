from abc import ABC, abstractmethod
from typing import AsyncContextManager


class AsyncLockService(ABC):
    """锁 Service 接口"""

    @abstractmethod
    def lock(self, key: str) -> AsyncContextManager[None]:
        """
        根据给定的 key 获取一个异步锁。
        此方法返回一个异步上下文管理器，以便使用 'async with' 语句。

        :param key: 锁的唯一标识符
        :return: 一个异步上下文管理器
        """
        pass