from abc import ABC, abstractmethod

from app.application.user_dto import UserDto
from app.application.user_query_criteria import UserQueryCriteria
from app.application.user_query_sort import UserQuerySort


class UserQueryHandler(ABC):
    """用户Repository接口"""

    @abstractmethod
    def query_by_id(self, id: str) -> UserDto | None:
        pass

    @abstractmethod
    def query_by_id_req(self, id: str) -> UserDto:
        pass

    @abstractmethod
    def query_count(self, criteria: UserQueryCriteria) -> int:
        pass

    @abstractmethod
    def query(self, criteria: UserQueryCriteria, *sorts: UserQuerySort) -> list[UserDto]:
        pass

    @abstractmethod
    def query_by_page(self, page_num: int, page_size: int, criteria: UserQueryCriteria, *sorts: UserQuerySort) -> list[UserDto]:
        pass

