from datetime import datetime

import aiosqlite

from src.application.query_sort_option import QuerySortOption
from src.application.user import user_query_sort_fields
from src.adapter.driven.database_adapter import DatabaseAdapter
from src.adapter.driven.persistence_exception import PersistenceException
from src.application.user.user_dto import UserDto
from src.application.user.user_query_criteria import UserQueryCriteria
from src.application.user.user_query_handler import UserQueryHandler
from src.domain.user.user import User
from src.domain.user.user_repository import UserRepository
from src.domain.user.user_unique_specification import UserUniqueSpecification
from src.utility import value_utility, crypto_utility


class UserPersistenceAdapter(UserRepository, UserUniqueSpecification, UserQueryHandler):
    """用户持久化Adapter"""

    def __init__(self, database_adapter: DatabaseAdapter):
        self.database_adapter = database_adapter
        self.table_name = "t_user" + datetime.now().strftime("%Y%m%d%H%M%S")

    async def init(self) -> None:
        async with self.database_adapter.get_connection() as conn:
            await conn.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.table_name}
                (
                    u_id TEXT PRIMARY KEY,
                    u_username TEXT,
                    u_password TEXT,
                    u_nickname TEXT,
                    u_mobile TEXT,
                    u_is_admin INTEGER,
                    u_created_at TEXT,
                    u_updated_at TEXT
                )
                """)
            await conn.execute(f"DELETE FROM {self.table_name} WHERE LOWER(u_username) = 'admin'")
            await conn.execute(f"""
                INSERT INTO {self.table_name} (
                    u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at
                ) VALUES (
                    '0', 'admin', '{crypto_utility.md5_encode("password")}', '管理员', '', 1, DATETIME('now', 'localtime'), DATETIME('now', 'localtime')
                )
                """)
            await conn.commit()

    # ==================================================================================================================
    # UserRepository

    def to_entity(self, row: aiosqlite.Row) -> User:
        """转换成 Entity"""
        row_dict = dict(row)
        return User.restore(
            id=row_dict.get("u_id", ""),
            username=row_dict.get("u_username", ""),
            password=row_dict.get("u_password", ""),
            nickname=row_dict.get("u_nickname", ""),
            mobile=row_dict.get("u_mobile", ""),
            is_admin=bool(row_dict.get("u_is_admin", False)),
            created_at=value_utility.to_datetime_or_default(row_dict.get("u_created_at"), datetime.min),
            updated_at=value_utility.to_datetime_or_default(row_dict.get("u_updated_at"), datetime.min)
        )

    async def insert(self, entity: User) -> None:
        async with self.database_adapter.get_connection() as conn:
            await conn.execute(f"""
                INSERT INTO {self.table_name} (
                    u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at
                ) VALUES (
                    ?, ?, ?, ?, ?, ?, ?, ?
                )
                """, (
                entity.id,
                entity.username,
                entity.password,
                entity.nickname,
                entity.mobile,
                int(entity.is_admin),
                value_utility.format_datetime(entity.created_at),
                value_utility.format_datetime(entity.updated_at) if entity.updated_at is not None else ""
            ))
            await conn.commit()

    async def update(self, entity: User) -> None:
        async with self.database_adapter.get_connection() as conn:
            await conn.execute(f"""
                UPDATE {self.table_name}
                    SET u_username   = ?,
                        u_password   = ?,
                        u_nickname   = ?,
                        u_mobile     = ?,
                        u_is_admin   = ?,
                        u_created_at = ?,
                        u_updated_at = ?
                    WHERE u_id = ?
                    """, (
                entity.username,
                entity.password,
                entity.nickname,
                entity.mobile,
                int(entity.is_admin),
                value_utility.format_datetime(entity.created_at),
                value_utility.format_datetime(entity.updated_at) if entity.updated_at is not None else "",
                entity.id
            ))
            await conn.commit()

    async def delete_by_id(self, id: str) -> None:
        if value_utility.is_empty_string(id):
            return None
        async with self.database_adapter.get_connection() as conn:
            await conn.execute(f"DELETE FROM {self.table_name} WHERE u_id = ?", (id,))
            # await conn.execute(f"DELETE FROM {self.table_name} WHERE u_id = ?", [id])
            await conn.commit()

    async def select_by_id(self, id: str) -> User | None:
        if value_utility.is_empty_string(id):
            return None
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} WHERE u_id = ?", (id,)) as cursor:
                row = await cursor.fetchone()
                if row:
                    return self.to_entity(row)
                return None

    async def select_by_id_req(self, id: str) -> User:
        entity = await self.select_by_id(id)
        if not entity:
            raise PersistenceException(f"用户 {id} 不存在")
        return entity

    async def select_by_ids(self, ids: list[str]) -> list[User]:
        if not ids or len(ids) == 0:
            return []
        placeholders = ",".join("?" for _ in ids)
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} WHERE u_id IN ({placeholders})", ids) as cursor:
                rows = await cursor.fetchall()
                return [self.to_entity(row) for row in rows]

    async def select_by_username(self, username: str) -> User | None:
        if value_utility.is_empty_string(username):
            return None
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} WHERE u_username = ?", (username,)) as cursor:
                row = await cursor.fetchone()
                if row:
                    return self.to_entity(row)
                return None

    # ==================================================================================================================
    # UserUniqueSpecification

    async def is_username_unique(self, username: str) -> bool:
        if value_utility.is_empty_string(username):
            raise PersistenceException("参数 username 不能为空")
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} WHERE LOWER(u_username) = LOWER(?)",
                                    (username,)) as cursor:
                row = await cursor.fetchone()
                # print(row)
                return row is None

    async def is_nickname_unique(self, nickname: str) -> bool:
        if value_utility.is_empty_string(nickname):
            raise PersistenceException("参数 nickname 不能为空")
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} WHERE LOWER(u_nickname) = LOWER(?)",
                                    (nickname,)) as cursor:
                row = await cursor.fetchone()
                # print(row)
                return row is None

    async def is_mobile_unique(self, mobile: str) -> bool:
        if value_utility.is_empty_string(mobile):
            raise PersistenceException("参数 mobile 不能为空")
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} WHERE LOWER(u_mobile) = LOWER(?)",
                                    (mobile,)) as cursor:
                row = await cursor.fetchone()
                # print(row)
                return row is None

    # ==================================================================================================================
    # UserQueryHandler

    def to_dto(self, row: aiosqlite.Row) -> UserDto:
        """转换成DTO"""
        row_dict = dict(row)
        return UserDto(
            id=row_dict.get("u_id", ""),
            username=row_dict.get("u_username", ""),
            # password=row_dict.get("u_password", ""),
            nickname=row_dict.get("u_nickname", ""),
            mobile=row_dict.get("u_mobile", ""),
            is_admin=bool(row_dict.get("u_is_admin", False)),
            created_at=value_utility.to_datetime_or_default(row_dict.get("u_created_at"), datetime.min),
            updated_at=value_utility.to_datetime_or_default(row_dict.get("u_updated_at"), datetime.min)
        )

    def build_query_criteria_sql(self, criteria: UserQueryCriteria) -> tuple[str, list]:
        """构造查询 SQL"""
        sqls = []
        params = []
        if not value_utility.is_empty_string(criteria.keyword):
            sqls.append("(u_username LIKE ? ESCAPE '\\' OR u_nickname LIKE ? ESCAPE '\\')")
            params.append(self.database_adapter.escape_like_pattern(criteria.keyword))
            params.append(self.database_adapter.escape_like_pattern(criteria.keyword))
        if not sqls:
            return "", []
        else:
            return " WHERE " + " AND ".join(sqls), params

    SORT_FIELD_MAP = {
        user_query_sort_fields.CREATED_AT: "u_created_at",
        user_query_sort_fields.USERNAME: "u_username"
    }

    def build_query_sort_sql(self, sort_options: list[QuerySortOption] | None = None) -> str:
        """构造排序 SQL"""
        sqls = []
        if sort_options is not None:
            for sort_option in sort_options:
                if sort_option.field in self.SORT_FIELD_MAP:
                    sqls.append(f"{self.SORT_FIELD_MAP[sort_option.field]} {sort_option.order}")

        if not sqls:
            sqls.append("u_created_at DESC")

        sqls.append("u_id DESC")
        return " ORDER BY " + ", ".join(sqls)

    async def query_by_id(self, id: str) -> UserDto | None:
        if value_utility.is_empty_string(id):
            return None
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} WHERE u_id = ?", (id,)) as cursor:
                row = await cursor.fetchone()
                if row:
                    return self.to_dto(row)
                return None

    async def query_by_id_req(self, id: str) -> UserDto:
        dto = await self.query_by_id(id)
        if not dto:
            raise PersistenceException(f"用户 {id} 不存在")
        return dto

    async def query_count(self, criteria: UserQueryCriteria) -> int:
        criteria_sql, params = self.build_query_criteria_sql(criteria)
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT COUNT(*) FROM {self.table_name} {criteria_sql}", params) as cursor:
                row = await cursor.fetchone()
                return row[0] if row else 0

    async def query(self, criteria: UserQueryCriteria, sort_options: list[QuerySortOption] | None = None) -> list[UserDto]:
        criteria_sql, params = self.build_query_criteria_sql(criteria)
        sort_sql = self.build_query_sort_sql(sort_options)
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(f"SELECT * FROM {self.table_name} {criteria_sql} {sort_sql}", params) as cursor:
                rows = await cursor.fetchall()
                return [self.to_dto(row) for row in rows]

    async def query_by_page(self, page_num: int, page_size: int, criteria: UserQueryCriteria,
                            sort_options: list[QuerySortOption] | None = None) \
            -> list[UserDto]:
        criteria_sql, params = self.build_query_criteria_sql(criteria)
        sort_sql = self.build_query_sort_sql(sort_options)
        offset = (page_num - 1) * page_size
        async with self.database_adapter.get_connection() as conn:
            conn.row_factory = aiosqlite.Row
            async with conn.execute(
                    f"SELECT * FROM {self.table_name} {criteria_sql} {sort_sql} LIMIT ? OFFSET ?",
                    params + [page_size, offset]
            ) as cursor:
                rows = await cursor.fetchall()
                return [self.to_dto(row) for row in rows]
