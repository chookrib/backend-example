import asyncio
import logging
from collections import defaultdict
from contextlib import asynccontextmanager
from typing import AsyncIterator

from src.application.lock.lock_service import LockService

logger = logging.getLogger(__name__)

class AsyncioLockService(LockService):
    """基于 asyncio.Lock 的本地锁管理器"""

    def __init__(self):
        # 使用 defaultdict，首次访问 key 会自动创建一个 RLock
        self._locks = defaultdict(asyncio.Lock)

    @asynccontextmanager
    async def lock(self, lock_key: str) -> AsyncIterator[None]: # type: ignore[override]
        """
        获取一个本地异步锁，使用 @asynccontextmanager 装饰
        """
        lock_obj = self._locks[lock_key]
        await lock_obj.acquire()

        current_task = asyncio.current_task()
        try:
            logger.info(f"协程 [{current_task.get_name() if current_task else ''}] 获得 asyncio 锁: {lock_key}")
            yield
        finally:
            lock_obj.release()
            logger.info(f"协程 [{current_task.get_name() if current_task else ''}] 释放 asyncio 锁: {lock_key}")
