import logging
import threading
from collections import defaultdict
from contextlib import contextmanager
from typing import Iterator

from src.application.lock.sync_lock_service import SyncLockService

logger = logging.getLogger(__name__)

class SyncLocalLockService(SyncLockService):
    """基于 threading.RLock 的本地锁管理器"""

    def __init__(self):
        # 使用 defaultdict，当第一次访问一个 key 时，会自动创建一个 RLock
        self._locks = defaultdict(threading.RLock)

    @contextmanager
    def lock(self, key: str, timeout: int = 30) -> Iterator[None]:
        """
        获取一个本地线程锁。
        注意：本地锁的 timeout 参数通常不起作用，因为 acquire 是阻塞的。
        """
        lock_obj = self._locks[key]
        print(f"Thread [{threading.current_thread().name}] trying to acquire local lock for key: {key}")

        acquired = lock_obj.acquire()  # RLock.acquire() 总是返回 True
        if acquired:
            print(f"Thread [{threading.current_thread().name}] acquired local lock for key: {key}")
            try:
                yield  # 将控制权交给 with 语句块
            finally:
                lock_obj.release()
                print(f"Thread [{threading.current_thread().name}] released local lock for key: {key}")
