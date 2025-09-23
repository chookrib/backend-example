from abc import ABC, abstractmethod

from app.domain.user import User


class UserRepository(ABC):
    """用户Repository接口"""

    @abstractmethod
    def insert(self, entity: User) -> None:
        """插入"""
        pass

    @abstractmethod
    def update(self, entity: User) -> None:
        """更新"""
        pass

    @abstractmethod
    def delete_by_id(self, id: str) -> None:
        """根据Id删除"""
        pass

    @abstractmethod
    def select_by_id(self, id: str) -> User | None:
        """根据Id查询，找不到返回null"""
        pass

    @abstractmethod
    def select_by_id_req(self, id: str) -> User:
        """根据Id查询，找不到抛出异常"""
        pass

    @abstractmethod
    def select_by_ids(self, id: list[str]) -> list[User]:
        """根据Id集合查询"""
        pass

    @abstractmethod
    def select_by_username(self, username: str) -> User | None:
        """根据用户名查询，找不到返回null"""
        pass


