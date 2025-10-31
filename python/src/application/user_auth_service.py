from datetime import datetime, timedelta

from src.application.application_exception import ApplicationException
from src.config import settings
from src.domain.user_repository import UserRepository
from src.utility import crypto_utility, value_utility


class UserAuthService:
    """用户认证Service"""

    def __init__(self, user_repository: UserRepository):
        jwt_expires_day = value_utility.to_int_or_none(settings.APP_JWT_EXPIRES_DAY)
        if jwt_expires_day is None or jwt_expires_day <= 0:
            raise ApplicationException(f"APP_JWT_EXPIRES_DAY 配置错误")
        self.jwt_expires_day = jwt_expires_day
        self.jwt_secret_key = settings.APP_JWT_SECRET_KEY
        if value_utility.is_blank(self.jwt_secret_key):
            raise ApplicationException(f"APP_JWT_SECRET_KEY 配置错误")

        self.user_repository = user_repository

    async def login(self, username, password) -> str:
        """登录，返回AccessToken"""
        user = await self.user_repository.select_by_username(username)
        if user is None or not user.is_password_match(password):
            raise ApplicationException("密码错误")

        return crypto_utility.encode_jwt(
            self.jwt_secret_key, datetime.now() + timedelta(days=self.jwt_expires_day),
            {"id": user.id})

    def get_login_user_id(self, access_token: str) -> str:
        """根据AccessToken获取登录用户Id"""
        try:
            payload = crypto_utility.decode_jwt(access_token, self.jwt_secret_key)
            return payload.get("id", "")
        except Exception:
            return ""
