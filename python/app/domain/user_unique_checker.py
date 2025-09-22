
from abc import ABC, abstractmethod

class UserUniqueChecker(ABC):
    """用户唯一性检查接口"""

    @abstractmethod
    async def is_username_unique(self, username: str) -> bool:
        pass

    @abstractmethod
    async def is_nickname_unique(self, nickname: str) -> bool:
        pass

    @abstractmethod
    async def is_mobile_unique(self, mobile: str) -> bool:
        pass

