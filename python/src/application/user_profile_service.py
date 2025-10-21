import random

from src.application import id_generator
from src.application.application_exception import ApplicationException
from src.application.lock import lock_keys
from src.application.lock.lock_service import LockService
from src.domain.sms_gateway import SmsGateway
from src.domain.user import User
from src.domain.user_repository import UserRepository
from src.domain.user_unique_checker import UserUniqueChecker
from src.utility import value_utility


class UserProfileService:
    """用户资料Service"""

    def __init__(self, user_repository: UserRepository, user_unique_checker: UserUniqueChecker,
                 sms_gateway: SmsGateway, lock_service: LockService) -> None:
        self.user_repository = user_repository
        self.user_unique_checker = user_unique_checker
        self.sms_gateway = sms_gateway
        self.lock_service = lock_service
        self.mobile_code: dict[str, str] = {}

    async def register(self, username: str, password: str, nickname: str) -> str:
        """注册，仅演示使用，未防止恶意注册"""
        async with self.lock_service.lock(lock_keys.USER):
            user = await User.register_user(
                id=id_generator.generate_id(),
                username=username,
                password=password,
                nickname=nickname,
                user_unique_checker=self.user_unique_checker)
            await self.user_repository.insert(user)
            return user.id

    async def modify_password(self, user_id: str, old_password: str, new_password: str) -> None:
        """修改密码"""
        user = await self.user_repository.select_by_id_req(user_id)
        user.modify_password(old_password, new_password)
        await self.user_repository.update(user)

    async def modify_nickname(self, user_id: str, nickname: str) -> None:
        """修改昵称"""
        async with self.lock_service.lock(lock_keys.USER):
            user = await self.user_repository.select_by_id_req(user_id)
            await user.modify_nickname(nickname, self.user_unique_checker)
            await self.user_repository.update(user)

    async def send_mobile_code(self, user_id: str, mobile: str) -> None:
        """发送手机验证码"""
        if value_utility.is_blank(mobile):
            raise ApplicationException("手机号不能为空")
        user = await self.user_repository.select_by_id_req(user_id)
        code = str(random.randint(100000, 999999))
        self.mobile_code[user.id + "_" + mobile] = code
        await self.sms_gateway.send_code(mobile, code)

        # import asyncio
        # asyncio.create_task(self.sms_gateway.send_code(mobile, code))

    async def bind_mobile(self, user_id: str, mobile: str, code: str) -> None:
        """绑定手机"""
        if value_utility.is_blank(code):
            raise ApplicationException("验证码不能为空")
        user = await self.user_repository.select_by_id_req(user_id)
        key = user.id + "_" + mobile
        if self.mobile_code.get(key, "") != code:
            raise ApplicationException("手机验证码错误")
        await user.modify_mobile(mobile, self.user_unique_checker)
        await self.user_repository.update(user)
        self.mobile_code.pop(key, None)
