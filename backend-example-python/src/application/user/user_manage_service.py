from src.application.lock import lock_key_constants
from src.application.lock.lock_service import LockService
from src.domain.user.user import User
from src.domain.user.user_repository import UserRepository
from src.domain.user.user_uniqueness_validator import UserUniquenessValidator
from src.utility import identifier_utility


class UserManageService:
    """用户管理Service"""

    def __init__(self, user_repository: UserRepository, user_unique_specification: UserUniquenessValidator,
                 lock_service: LockService) -> None:
        self.user_repository = user_repository
        self.user_unique_specification = user_unique_specification
        self.lock_service = lock_service

    async def set_admin(self, id: str, is_admin: bool) -> None:
        """设置用户管理员状态"""
        user = await self.user_repository.find_by_id_or_throw(id)
        user.set_admin(is_admin)
        await self.user_repository.update(user)

    async def create_user(self, username: str, password: str, nickname: str, mobile: str, is_admin: bool) -> str:
        """创建用户"""
        async with self.lock_service.lock_async(lock_key_constants.USER):
            user = await User.create(
                id=identifier_utility.generate_uuid_v7(),
                username=username,
                password=password,
                nickname=nickname,
                mobile=mobile,
                is_admin=is_admin,
                user_unique_specification=self.user_unique_specification)
            await self.user_repository.insert(user)
            return user.id

    async def modify_user(self, id: str, username: str, nickname: str, mobile: str, is_admin: bool) -> None:
        """修改用户"""
        async with self.lock_service.lock_async(lock_key_constants.USER):
            user = await self.user_repository.find_by_id_or_throw(id)
            await user.modify(
                username=username,
                nickname=nickname,
                mobile=mobile,
                is_admin=is_admin,
                user_unique_specification=self.user_unique_specification)
            await self.user_repository.update(user)

    async def remove_user(self, id: str) -> None:
        """删除用户"""
        return await self.user_repository.delete_by_id(id)
