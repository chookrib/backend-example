import os

import aiosqlite

from src.adapter.driven.persistence_exception import PersistenceException
from src.application.user_dto import UserDto
from src.application.user_query_criteria import UserQueryCriteria
from src.application.user_query_handler import UserQueryHandler
from src.application.user_query_sort import UserQuerySort
from src.config import settings
from src.domain.user import User
from src.domain.user_repository import UserRepository
from src.domain.user_unique_checker import UserUniqueChecker
from src.utility import value_utility, crypto_utility


class UserPersistenceAdapter(UserRepository, UserUniqueChecker, UserQueryHandler):
    """用户持久化Adapter"""

    def __init__(self):
        # self.db_path = str(Path(__file__).resolve().parents[4] / settings.SQLITE_DATABASE_FILE)
        self.db_path = os.path.join(os.getcwd(), settings.SQLITE_DATABASE_FILE)

    async def init(self) -> None:
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute(
                "create table if not exists t_user (u_id text primary key, u_username text, u_password text, " +
                "u_nickname text, u_mobile text, u_is_admin integer, u_created_at text)"
            )
            await db.execute("delete from t_user where lower(u_username) = 'admin'")
            await db.execute(
                "insert into t_user (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at) " +
                "values ('0', 'admin', '" + crypto_utility.encode_md5("password") + "', " +
                "'管理员', '', 1, datetime('now', 'localtime'))"
            )
            await db.commit()

    # ==================================================================================================================
    # UserRepository

    def to_user(self, row: aiosqlite.Row) -> User:
        """转换成 Entity"""
        row_dict = dict(row)
        return User.restore_user(
            id=row_dict.get("u_id", ""),
            username=row_dict.get("u_username", ""),
            password=row_dict.get("u_password", ""),
            nickname=row_dict.get("u_nickname", ""),
            mobile=row_dict.get("u_mobile", ""),
            is_admin=bool(row_dict.get("u_is_admin", False)),
            created_at=value_utility.to_datetime_req(row_dict.get("u_created_at", ""))
        )

    async def insert(self, entity: User) -> None:
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute("""
                             insert into t_user (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin,
                                                 u_created_at)
                             values (?, ?, ?, ?, ?, ?, ?)
                             """, (
                                 entity.id,
                                 entity.username,
                                 entity.password,
                                 entity.nickname,
                                 entity.mobile,
                                 int(entity.is_admin),
                                 value_utility.to_datetime_str(entity.created_at)
                             ))
            await db.commit()

    async def update(self, entity: User) -> None:
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute("""
                             update t_user
                             set u_username   = ?,
                                 u_password   = ?,
                                 u_nickname   = ?,
                                 u_mobile     = ?,
                                 u_is_admin   = ?,
                                 u_created_at = ?
                             where u_id = ?
                             """, (
                                 entity.username,
                                 entity.password,
                                 entity.nickname,
                                 entity.mobile,
                                 int(entity.is_admin),
                                 value_utility.to_datetime_str(entity.created_at),
                                 entity.id
                             ))
            await db.commit()

    async def delete_by_id(self, id: str) -> None:
        if value_utility.is_blank(id):
            return None
        async with aiosqlite.connect(self.db_path) as db:
            await db.execute("delete from t_user where u_id = ?", (id,))
            # await db.execute("delete from t_user where u_id = ?", [id])
            await db.commit()

    async def select_by_id(self, id: str) -> User | None:
        if value_utility.is_blank(id):
            return None
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute("select * from t_user where u_id = ?", (id,)) as cursor:
                row = await cursor.fetchone()
                if row:
                    return self.to_user(row)
                return None

    async def select_by_id_req(self, id: str) -> User:
        user = await self.select_by_id(id)
        if not user:
            raise PersistenceException(f"用户 {id} 不存在")
        return user

    async def select_by_ids(self, ids: list[str]) -> list[User]:
        if not ids or len(ids) == 0:
            return []
        placeholders = ",".join("?" for _ in ids)
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute(f"select * from t_user where u_id in ({placeholders})", ids) as cursor:
                rows = await cursor.fetchall()
                return [self.to_user(row) for row in rows]

    async def select_by_username(self, username: str) -> User | None:
        if value_utility.is_blank(username):
            return None
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute("select * from t_user where u_username = ?", (username,)) as cursor:
                row = await cursor.fetchone()
                if row:
                    return self.to_user(row)
                return None

    # ==================================================================================================================
    # UserUniqueChecker

    async def is_username_unique(self, username: str) -> bool:
        if value_utility.is_blank(username):
            raise PersistenceException("参数 username 不能为空")
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute("select 1 from t_user where u_username = ?", (username,)) as cursor:
                row = await cursor.fetchone()
                # print(row)
                return row is None

    async def is_nickname_unique(self, nickname: str) -> bool:
        if value_utility.is_blank(nickname):
            raise PersistenceException("参数 nickname 不能为空")
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute("select 1 from t_user where u_nickname = ?", (nickname,)) as cursor:
                row = await cursor.fetchone()
                # print(row)
                return row is None

    async def is_mobile_unique(self, mobile: str) -> bool:
        if value_utility.is_blank(mobile):
            raise PersistenceException("参数 mobile 不能为空")
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute("select 1 from t_user where u_mobile = ?", (mobile,)) as cursor:
                row = await cursor.fetchone()
                # print(row)
                return row is None

    # ==================================================================================================================
    # UserQueryHandler

    def to_user_dto(self, row: aiosqlite.Row) -> UserDto:
        """转换成DTO"""
        user = self.to_user(row)
        return UserDto(
            id=user.id,
            username=user.username,
            # password=user.password,
            nickname=user.nickname,
            mobile=user.mobile,
            is_admin=user.is_admin,
            created_at=user.created_at
        )

    def build_query_criteria(self, criteria: UserQueryCriteria) -> tuple[str, list]:
        """构造查询SQL"""
        sqls = []
        params = []
        if not value_utility.is_blank(criteria.keyword):
            sqls.append(" u_username like ? and u_nickname like ? ")
            params.append(f"%{criteria.keyword}%")
            params.append(f"%{criteria.keyword}%")
        if not sqls:
            return "", []
        else:
            return " where " + " and ".join(sqls), params

    def build_query_sort(self, *sorts: UserQuerySort) -> str:
        """构造排序SQL"""
        sqls = []
        for s in sorts:
            match s:
                case UserQuerySort.CREATED_AT_ASC:
                    sqls.append("u_created_at asc")
                case UserQuerySort.CREATED_AT_DESC:
                    sqls.append("u_created_at desc")
                case UserQuerySort.USERNAME_ASC:
                    sqls.append("u_username asc")
                case UserQuerySort.USERNAME_DESC:
                    sqls.append("u_username desc")

        if not sqls:
            sqls.append("u_created_at desc")

        sqls.append("u_id desc")
        return " order by " + ", ".join(sqls)

    async def query_by_id(self, id: str) -> UserDto | None:
        if value_utility.is_blank(id):
            return None
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute("select * from t_user where u_id = ?", (id,)) as cursor:
                row = await cursor.fetchone()
                if row:
                    return self.to_user_dto(row)
                return None

    async def query_by_id_req(self, id: str) -> UserDto:
        user = await self.query_by_id(id)
        if not user:
            raise PersistenceException(f"用户 {id} 不存在")
        return user

    async def query_count(self, criteria: UserQueryCriteria) -> int:
        criteria_sql, params = self.build_query_criteria(criteria)
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute(f"select count(*) from t_user {criteria_sql}", params) as cursor:
                row = await cursor.fetchone()
                return row[0] if row else 0

    async def query(self, criteria: UserQueryCriteria, *sorts: UserQuerySort) -> list[UserDto]:
        criteria_sql, params = self.build_query_criteria(criteria)
        sort_sql = self.build_query_sort(*sorts)
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute(f"select * from t_user {criteria_sql} {sort_sql}", params) as cursor:
                rows = await cursor.fetchall()
                return [self.to_user_dto(row) for row in rows]

    async def query_by_page(self, page_num: int, page_size: int, criteria: UserQueryCriteria, *sorts: UserQuerySort) \
            -> list[UserDto]:
        criteria_sql, params = self.build_query_criteria(criteria)
        sort_sql = self.build_query_sort(*sorts)
        offset = (page_num - 1) * page_size
        async with aiosqlite.connect(self.db_path) as db:
            db.row_factory = aiosqlite.Row
            async with db.execute(
                    f"select * from t_user {criteria_sql} {sort_sql} limit ? offset ?",
                    params + [page_size, offset]
            ) as cursor:
                rows = await cursor.fetchall()
                return [self.to_user_dto(row) for row in rows]
