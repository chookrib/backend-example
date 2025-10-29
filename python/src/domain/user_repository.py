from abc import ABC, abstractmethod

from src.domain.user import User


class UserRepository(ABC):
    """用户Repository接口"""

    @abstractmethod
    async def init(self) -> None:
        """初始化"""
        pass

    @abstractmethod
    async def insert(self, entity: User) -> None:
        """插入"""
        pass

    @abstractmethod
    async def update(self, entity: User) -> None:
        """更新"""
        pass

    @abstractmethod
    async def delete_by_id(self, id: str) -> None:
        """根据 id 删除"""
        pass

    @abstractmethod
    async def select_by_id(self, id: str) -> User | None:
        """根据 id 查询，找不到返回 None"""
        pass

    @abstractmethod
    async def select_by_id_req(self, id: str) -> User:
        """根据 id 查询，找不到抛出异常"""
        pass

    @abstractmethod
    async def select_by_ids(self, id: list[str]) -> list[User]:
        """根据 id 集合查询"""
        pass

    @abstractmethod
    async def select_by_username(self, username: str) -> User | None:
        """根据用户名查询，找不到返回 None"""
        pass
