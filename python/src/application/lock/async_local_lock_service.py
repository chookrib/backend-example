import asyncio
import logging
from collections import defaultdict
from contextlib import asynccontextmanager
from typing import AsyncIterator

from src.application.lock.async_lock_service import AsyncLockService

logger = logging.getLogger(__name__)

class AsyncLocalLockService(AsyncLockService):
    """基于 asyncio.Lock 的本地锁管理器"""

    def __init__(self):
        # 使用 defaultdict，当第一次访问一个 key 时，会自动创建一个 RLock
        self._locks = defaultdict(asyncio.Lock)

    @asynccontextmanager
    async def lock(self, key: str) -> AsyncIterator[None]:
        """
        获取一个本地异步锁。

        这个方法被 @asynccontextmanager 装饰，因此它是一个异步生成器。
        - 其返回类型被正确标注为 AsyncIterator[None]。
        - 使用 # type: ignore[override] 来告知 Mypy，我们知道签名与基类不符，
          但装饰器会使其在运行时兼容。
        """
        lock_obj = self._locks[key]

        # 在 FastAPI 应用中，为了调试，可以打印出当前任务的 ID
        current_task = asyncio.current_task()
        print(f"Task [{current_task.get_name()}] trying to acquire asyncio lock for key: {key}")

        await lock_obj.acquire()
        try:
            print(f"Task [{current_task.get_name()}] acquired asyncio lock for key: {key}")
            yield
        finally:
            lock_obj.release()
            print(f"Task [{current_task.get_name()}] released asyncio lock for key: {key}")
