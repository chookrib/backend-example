import asyncio
import os
from contextlib import asynccontextmanager
from typing import AsyncIterator

import aiosqlite

from src.adapter.driven.persistence_exception import PersistenceException
from src.application.application_config import application_config
from src.utility import value_utility


class DatabaseAdapter:
    """数据库 Adapter"""

    def __init__(self) -> None:
        if value_utility.is_empty_string(application_config.APP_SQLITE_PATH):
            raise PersistenceException("APP_SQLITE_PATH 配置错误")
        # self.db_path = str(Path(__file__).resolve().parents[4] / settings.APP_SQLITE_PATH)
        self.db_path = os.path.join(os.getcwd(), application_config.APP_SQLITE_PATH)
        # aiosqlite 没有内置池，此处手工实现
        self.pool_size = 5
        self._pool: asyncio.Queue[aiosqlite.Connection] = asyncio.Queue()   #
        self._lock = asyncio.Lock()
        self._connection_count = 0

    async def _create_connection(self) -> aiosqlite.Connection:
        conn = await aiosqlite.connect(self.db_path)
        conn.row_factory = aiosqlite.Row
        # 并发写时等待而不是立即报 "database is locked"
        await conn.execute("PRAGMA busy_timeout = 5000")
        return conn

    async def _acquire(self) -> aiosqlite.Connection:
        if not self._pool.empty():
            return self._pool.get_nowait()
        async with self._lock:
            # 双重检查，防止并发下超出 pool_size
            if not self._pool.empty():
                return self._pool.get_nowait()
            if self._connection_count < self.pool_size:
                self._connection_count += 1
                return await self._create_connection()
        # 池已满，等别的请求归还
        return await self._pool.get()

    async def _release(self, conn: aiosqlite.Connection) -> None:
        # 若本次操作中途抛异常留下未提交事务，回滚掉，避免"污染"下一个借用者
        try:
            await conn.rollback()
        except aiosqlite.Error:
            pass
        await self._pool.put(conn)

    @asynccontextmanager
    async def get_connection(self) -> AsyncIterator[aiosqlite.Connection]:
        """从池中获取数据库连接"""
        conn = await self._acquire()
        try:
            yield conn
        finally:
            await self._release(conn)

    async def close(self) -> None:
        while not self._pool.empty():
            await self._pool.get_nowait().close()

    # @asynccontextmanager
    # async def get_connection(self) -> AsyncIterator[aiosqlite.Connection]:
    #     """获取数据库连接"""
    #     async with aiosqlite.connect(self.db_path) as conn:
    #         yield conn

    def escape_like_pattern(self, pattern: str) -> str:
        # "将 % 和 _ 转义为 \% 和 \_
        # LIKE 查询指定 ESCAPE '\'  \ 本身也需要转义
        pattern = pattern.replace('\\', '\\\\')
        pattern = pattern.replace('%', '\\%')
        pattern = pattern.replace('_', '\\_')
        return f"%{pattern}%"
