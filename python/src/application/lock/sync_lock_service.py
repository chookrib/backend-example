from abc import ABC, abstractmethod
from typing import ContextManager


class SyncLockService(ABC):
    """锁 Service 接口"""

    @abstractmethod
    def lock(self, key: str, timeout: int = 30) -> ContextManager[None]:
        """
        根据给定的key获取一个锁。返回上下文管理器，以便使用 'with' 语句。

        :param key: 锁的唯一标识符
        :param timeout: 获取锁的超时时间（秒），主要用于分布式锁
        :return: 一个上下文管理器
        """
        pass