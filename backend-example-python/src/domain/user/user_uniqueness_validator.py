from abc import ABC, abstractmethod


class UserUniquenessValidator(ABC):
    """用户唯一性 Validator 接口"""

    @abstractmethod
    async def is_username_available(self, username: str) -> bool:
        """用户名是否可用"""
        pass

    @abstractmethod
    async def is_nickname_available(self, nickname: str) -> bool:
        """昵称是否可用"""
        pass

    @abstractmethod
    async def is_mobile_available(self, mobile: str) -> bool:
        """手机号是否可用"""
        pass
