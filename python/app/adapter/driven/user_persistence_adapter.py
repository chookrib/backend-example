import os
import sqlite3

from app.adapter.driven.query_exception import QueryException
from app.adapter.driven.repository_exception import RepositoryException
from app.application.user_dto import UserDto
from app.application.user_query_criteria import UserQueryCriteria
from app.application.user_query_handler import UserQueryHandler
from app.application.user_query_sort import UserQuerySort
from app.domain.user import User
from app.domain.user_repository import UserRepository
from app.domain.user_unique_checker import UserUniqueChecker
from app.utility import value_utility


class UserPersistenceAdapter(UserRepository, UserUniqueChecker, UserQueryHandler):

    def __init__(self):
        self.conn = sqlite3.connect(os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'db.db'))
        self.conn.row_factory = sqlite3.Row
        self.cursor = self.conn.cursor()
        self.cursor.execute(
            """
            create table if not exists t_user (u_id text primary key, u_username text, u_password text,
            u_nickname text, u_mobile text, u_is_admin integer u_created_at text)
            """)
        self.cursor.execute("delete from t_user where lower(u_username) = 'admin'")
        self.cursor.execute(
            """
            insert into t_user (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at)
            values ('0', 'admin', '5f4dcc3b5aa765d61d8327deb882cf99', '管理员', '', 1, datetime('now', 'localtime'))
            """)
        self.conn.commit()

    def __del__(self):
        if hasattr(self, 'cursor') and self.cursor:
            self.cursor.close()
        if hasattr(self, 'conn') and self.conn:
            self.conn.close()

    def to_user(self, row: sqlite3.Row) -> User:
        row_dict = dict(row)
        return User.restore_user(
            id=row_dict.get("u_id", ""),
            username=row_dict.get("u_username", ""),
            password=row_dict.get("u_password", ""),
            nickname=row_dict.get("u_nickname", ""),
            mobile=row_dict.get("u_mobile", ""),
            is_admin=bool(row_dict.get("u_is_admin", 0)),
            created_at=value_utility.to_datetime_req(row_dict.get("u_created_at", ""))
        )

    def insert(self, entity: User) -> None:
        self.cursor.execute("""
              insert into t_user (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at)
              values (?, ?, ?, ?, ?, ?, ?)
              """, (
            entity.id,
            entity.username,
            entity.password,
            entity.nickname,
            entity.mobile,
            int(entity.is_admin),
            entity.created_at
        ))
        self.conn.commit()

    def update(self, entity: User) -> None:
        self.cursor.execute("""
              update t_user set u_username = ?, u_password = ?, u_nickname = ?, u_mobile = ?, u_is_admin  = ?,
              u_created_at = ? where u_id = ?
              """, (
            entity.username,
            entity.password,
            entity.nickname,
            entity.mobile,
            int(entity.is_admin),
            entity.created_at,
            entity.id
        ))
        self.conn.commit()

    def delete_by_id(self, id: str) -> None:
        self.cursor.execute("delete from t_user where u_id = ?", (id,))
        # self.cursor.execute("delete from t_user where u_id = ?", [id])
        self.conn.commit()

    def select_by_id(self, id: str) -> User | None:
        row = self.cursor.execute(
            "select * from t_user where u_id = ?",(id,)).fetchone()
        if row:
            return self.to_user(row)
        return None

    def select_by_id_req(self, id: str) -> User:
        user = self.select_by_id(id)
        if not user:
            raise RepositoryException(f"用户 {id} 不存在")
        return user

    def select_by_ids(self, ids: list[str]) -> list[User]:
        if not ids or len(ids) == 0:
            return []
        placeholders = ",".join("?" for _ in ids)
        rows = self.cursor.execute(
            f"select * from t_user where u_id in ({placeholders})", ids
        ).fetchall()
        return [self.to_user(row) for row in rows]

    def select_by_username(self, username: str) -> User | None:
        row = self.cursor.execute(
            "select * from t_user where u_username = ?", (username,)
        ).fetchone()
        if row:
            return self.to_user(row)
        return None

    # ==================================================================================================================

    async def is_username_unique(self, username: str) -> bool:
        row = self.cursor.execute(
            "select 1 from t_user where u_username = ?", (username,)
        ).fetchone()
        return row is None

    async def is_nickname_unique(self, nickname: str) -> bool:
        row = self.cursor.execute(
            "select 1 from t_user where u_nickname = ?", (nickname,)
        ).fetchone()
        return row is None

    async def is_mobile_unique(self, mobile: str) -> bool:
        row = self.cursor.execute(
            "select 1 from t_user where u_mobile = ?", (mobile,)
        ).fetchone()
        return row is None

    # ==================================================================================================================

    def to_user_dto(self, row: sqlite3.Row) -> UserDto:
        row_dict = dict(row)
        return UserDto(
            id=row_dict.get("u_id", ""),
            username=row_dict.get("u_username", ""),
            # password=row_dict.get("u_password", ""),
            nickname=row_dict.get("u_nickname", ""),
            mobile=row_dict.get("u_mobile", ""),
            is_admin=bool(row_dict.get("u_is_admin", 0)),
            created_at=value_utility.to_datetime_req(row_dict.get("u_created_at", ""))
        )

    def query_by_id(self, id: str) -> UserDto | None:
        row = self.cursor.execute(
            "select * from t_user where u_id = ?", (id,)).fetchone()
        if row:
            return self.to_user_dto(row)
        return None

    def query_by_id_req(self, id: str) -> UserDto:
        user = self.query_by_id(id)
        if not user:
            raise QueryException(f"用户 {id} 不存在")
        return user

    def create_query_criteria_sql(self, criteria: UserQueryCriteria) -> tuple[str, list]:
        sqls = []
        params = []
        if criteria.keyword:
            sqls.append(" u_username like ? and u_nickname like ? ")
            params.append(f"%{criteria.keyword}%")
            params.append(f"%{criteria.keyword}%")
        if not sqls:
            return "", []
        else:
            return " where " + " and ".join(sqls), params

    def create_query_sort_sql(self, *sorts: UserQuerySort) -> str:
        sqls = []
        if sorts:
            for sort in sorts:
                match sort:
                    case UserQuerySort.CreateAtAsc:
                        sqls.append("u_created_at asc")
                    case UserQuerySort.CreateAtDesc:
                        sqls.append("u_created_at desc")
                    case UserQuerySort.UsernameAsc:
                        sqls.append("u_username desc")
                    case UserQuerySort.UsernameDesc:
                        sqls.append("u_username desc")

        if not sqls:
            return " order by u_created_at desc, u_id desc"
        else:
            sqls.append("u_id desc")
            return " order by " + ", ".join(sqls)

    def query_count(self, criteria: UserQueryCriteria) -> int:
        criteria_sql, params = self.create_query_criteria_sql(criteria)
        row = self.cursor.execute(f"select count(*) from t_user {criteria_sql}", params).fetchone()
        return row[0] if row else 0

    def query(self, criteria: UserQueryCriteria, *sorts: UserQuerySort) -> list[UserDto]:
        criteria_sql, params = self.create_query_criteria_sql(criteria)
        sort_sql = self.create_query_sort_sql(*sorts)
        rows = self.cursor.execute(
            f"select * from t_user {criteria_sql} {sort_sql}", params
        ).fetchall()
        return [self.to_user_dto(row) for row in rows]

    def query_by_page(self, page_num: int, page_size: int, criteria: UserQueryCriteria, *sorts: UserQuerySort) -> list[UserDto]:
        criteria_sql, params = self.create_query_criteria_sql(criteria)
        sort_sql = self.create_query_sort_sql(*sorts)
        offset = (page_num - 1) * page_size
        rows = self.cursor.execute(
            f"select * from t_user {criteria_sql} {sort_sql} limit ? offset ?",
            params + [page_size, offset]
        ).fetchall()
        return [self.to_user_dto(row) for row in rows]
