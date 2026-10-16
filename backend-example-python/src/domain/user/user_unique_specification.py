from abc import ABC, abstractmethod


class UserUniqueSpecification(ABC):
    """用户唯一性 Specification 接口"""

    @abstractmethod
    async def is_username_unique(self, username: str) -> bool:
        """用户名是否唯一"""
        pass

    @abstractmethod
    async def is_nickname_unique(self, nickname: str) -> bool:
        """昵称是否唯一"""
        pass

    @abstractmethod
    async def is_mobile_unique(self, mobile: str) -> bool:
        """手机号是否唯一"""
        pass
