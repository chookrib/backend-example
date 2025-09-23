from datetime import datetime, timedelta

from app.application.application_exception import ApplicationException
from app.config import settings
from app.domain.user_repository import UserRepository
from app.utility import crypto_utility


class UserAuthService:
    """用户认证Service"""

    def __init__(self, user_repository: UserRepository):
        self.user_repository = user_repository

    def login(self, username, password) -> str:
        """登录，返回AccessToken"""
        user = self.user_repository.select_by_username(username)
        if user is None or not user.is_password_match(password):
            raise ApplicationException("密码错误")

        return crypto_utility.encode_jwt(
            settings.USER_JWT_SECRET, datetime.now() + timedelta(days=settings.USER_JWT_EXPIRES_DAY),
            {"id": user.id})

    def get_login_user_id(self, access_token: str) -> str:
        """根据AccessToken获取登录用户Id"""
        try:
            payload = crypto_utility.decode_jwt(access_token, settings.USER_JWT_SECRET)
            return payload.get("id", "")
        except Exception:
            return ""
