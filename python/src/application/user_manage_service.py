from src.application import id_generator
from src.application.lock import lock_keys
from src.application.lock.lock_service import LockService
from src.domain.user import User
from src.domain.user_repository import UserRepository
from src.domain.user_unique_specification import UserUniqueSpecification


class UserManageService:
    """用户管理Service"""

    def __init__(self, user_repository: UserRepository, user_unique_specification: UserUniqueSpecification,
                 lock_service: LockService) -> None:
        self.user_repository = user_repository
        self.user_unique_specification = user_unique_specification
        self.lock_service = lock_service

    async def set_admin(self, id: str, is_admin: bool) -> None:
        """设置用户管理员状态"""
        user = await self.user_repository.select_by_id_req(id)
        user.set_admin(is_admin)
        await self.user_repository.update(user)

    async def create_user(self, username: str, password: str, nickname: str, mobile: str = "") -> str:
        """创建用户"""
        async with self.lock_service.lock_async(lock_keys.USER):
            user = await User.create(
                id=id_generator.generate_id(),
                username=username,
                password=password,
                nickname=nickname,
                mobile=mobile,
                user_unique_specification=self.user_unique_specification)
            await self.user_repository.insert(user)
            return user.id

    async def modify_user(self, id: str, username: str, nickname: str, mobile: str) -> None:
        """修改用户"""
        async with self.lock_service.lock_async(lock_keys.USER):
            user = await self.user_repository.select_by_id_req(id)
            await user.modify(
                username=username,
                nickname=nickname,
                mobile=mobile,
                user_unique_specification=self.user_unique_specification)
            await self.user_repository.update(user)

    async def remove_user(self, id: str) -> None:
        """删除用户"""
        return await self.user_repository.delete_by_id(id)
