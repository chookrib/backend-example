from datetime import datetime, timedelta

from src.application.application_exception import ApplicationException
from src.config import settings
from src.domain.user_repository import UserRepository
from src.utility import crypto_utility, value_utility


class UserAuthService:
    """用户认证Service"""

    def __init__(self, user_repository: UserRepository):
        if not value_utility.is_blank(settings.APP_JWT_SECRET):
            self.jwt_secret = settings.APP_JWT_SECRET
        else:
            raise ApplicationException(f"APP_JWT_SECRET 配置错误")
        try:
            self.jwt_expires_minute = crypto_utility.jwt_expires_minute(settings.APP_JWT_EXPIRES)
        except Exception as ex:
            raise ApplicationException(f"APP_JWT_EXPIRES 配置错误") from ex
        self.user_repository = user_repository

    async def login(self, username, password) -> str:
        """登录，返回 AccessToken"""
        user = await self.user_repository.select_by_username(username)
        if user is None or not user.is_password_match(password):
            raise ApplicationException("密码错误")

        return crypto_utility.jwt_encode(
            {"id": user.id},
            self.jwt_secret,
            datetime.now() + timedelta(minutes=self.jwt_expires_minute),
        )

    def get_login_user_id(self, access_token: str) -> str:
        """根据 AccessToken 获取登录用户 Id"""
        try:
            payload = crypto_utility.jwt_decode(access_token, self.jwt_secret)
            return payload.get("id", "")
        except Exception as ex:
            return ""
