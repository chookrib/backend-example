from abc import ABC, abstractmethod

from src.application.user_dto import UserDto
from src.application.user_query_criteria import UserQueryCriteria
from src.application.user_query_sort import UserQuerySort


class UserQueryHandler(ABC):
    """用户查询Handler接口"""

    @abstractmethod
    async def query_by_id(self, id: str) -> UserDto | None:
        """根据 id 查询，找不到返回 None"""
        pass

    @abstractmethod
    async def query_by_id_req(self, id: str) -> UserDto:
        """根据 id 查询，找不到抛出异常"""
        pass

    @abstractmethod
    async def query_count(self, criteria: UserQueryCriteria) -> int:
        """查询记录数"""
        pass

    @abstractmethod
    async def query(self, criteria: UserQueryCriteria, *sorts: UserQuerySort) -> list[UserDto]:
        """查询"""
        pass

    @abstractmethod
    async def query_by_page(self, page_num: int, page_size: int, criteria: UserQueryCriteria, *sorts: UserQuerySort)\
            -> list[UserDto]:
        """分页查询"""
        pass
