from app.application.application_exception import ApplicationException
from app.domain import user_repository
from app.domain.user_unique_checker import UserUniqueChecker
from app.domain.user_repository import UserRepository
from app.domain.sms_gateway import SmsGateway
from app.domain.user import User
from app.application import id_generator
import random


class UserProfileService:
    """用户资料Service"""

    def __init__(self, user_repository: UserRepository, user_unique_checker: UserUniqueChecker,
                 sms_gateway: SmsGateway):
        self.user_repository = user_repository
        self.user_unique_checker = user_unique_checker
        self.sms_gateway = sms_gateway
        self.mobile_code: dict[str, str] = {}

    def register(self, username: str, password: str, nickname: str) -> str:
        """注册，仅演示使用，未防止恶意注册"""
        user = User.register_user(
            id=id_generator.generate_id(),
            username=username,
            password=password,
            nickname=nickname,
            user_unique_checker=self.user_unique_checker)
        self.user_repository.insert(user)
        return user.id

    def modify_password(self, user_id: str, old_password: str, new_password: str) -> None:
        """修改密码"""
        user = self.user_repository.select_by_id_req(user_id)
        user.modify_password(old_password, new_password)
        self.user_repository.update(user)

    def modify_nickname(self, user_id: str, nickname: str) -> None:
        """修改昵称"""
        user = self.user_repository.select_by_id_req(user_id)
        user.modify_nickname(nickname, self.user_unique_checker)
        self.user_repository.update(user)

    def send_mobile_code(self, user_id: str, mobile: str) -> None:
        """发送手机验证码"""
        if not mobile:
            raise ApplicationException("手机号不能为空")
        user = self.user_repository.select_by_id_req(user_id)
        code = str(random.randint(100000, 999999))
        self.mobile_code[user.id + "_" + mobile] = code
        self.sms_gateway.send_code(mobile, code)

        # import asyncio
        # asyncio.create_task(self.sms_gateway.send_code(mobile, code))

    def bind_mobile(self, user_id: str, mobile: str, code: str) -> None:
        """绑定手机"""
        if not code:
            raise ApplicationException("验证码不能为空")
        user = self.user_repository.select_by_id_req(user_id)
        key = user.id + "_" + mobile
        if self.mobile_code.get(key, "") != code:
            raise ApplicationException("手机验证码错误")
        user.modify_mobile(mobile, self.user_unique_checker)
        self.user_repository.update(user)
        self.mobile_code.pop(key, None)
