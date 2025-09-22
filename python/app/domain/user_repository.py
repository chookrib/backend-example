from abc import ABC, abstractmethod

from app.domain.user import User


class UserRepository(ABC):
    """用户Repository接口"""

    @abstractmethod
    def insert(self, entity: User) -> None:
        pass

    @abstractmethod
    def update(self, entity: User) -> None:
        pass

    @abstractmethod
    def delete_by_id(self, id: str) -> None:
        pass

    @abstractmethod
    def select_by_id(self, id: str) -> User | None:
        pass

    @abstractmethod
    def select_by_id_req(self, id: str) -> User:
        pass

    @abstractmethod
    def select_by_ids(self, id: list[str]) -> list[User]:
        pass

    @abstractmethod
    def select_by_username(self, username: str) -> User | None:
        pass


