import os
from contextlib import asynccontextmanager
from typing import AsyncIterator

import aiosqlite

from src.adapter.driven.persistence_exception import PersistenceException
from src.application.application_config import application_config
from src.utility import value_utility


class SQLiteAdapter:
    """SQLite 数据库 Adapter"""

    def __init__(self):
        if value_utility.is_empty_string(application_config.APP_SQLITE_PATH):
            raise PersistenceException("APP_SQLITE_PATH 配置错误")
        # self.db_path = str(Path(__file__).resolve().parents[4] / settings.APP_SQLITE_PATH)
        self.db_path = os.path.join(os.getcwd(), application_config.APP_SQLITE_PATH)

    @asynccontextmanager
    async def get_connection(self) -> AsyncIterator[aiosqlite.Connection]:
        """获取数据库连接"""
        async with aiosqlite.connect(self.db_path) as conn:
            yield conn

    def escape_like_pattern(self, pattern: str) -> str:
        # "将 % 和 _ 转义为 \% 和 \_
        # LIKE 查询指定 ESCAPE '\'  \ 本身也需要转义
        pattern = pattern.replace('\\', '\\\\')
        pattern = pattern.replace('%', '\\%')
        pattern = pattern.replace('_', '\\_')
        return f"%{pattern}%"
