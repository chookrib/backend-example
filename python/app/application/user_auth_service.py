from datetime import datetime, timedelta

from app.application.application_exception import ApplicationException
from app.config import settings
from app.utility import jwt_utility


class UserAuthService:
    def __init__(self, user_repository):
        self.user_repository = user_repository

    def login(self, username, password) -> str:
        user = self.user_repository.get_by_username(username)
        if user is None or not user.is_password_match(password):
            raise ApplicationException("密码错误")

        return jwt_utility.encode(
            settings.USER_JWT_SECRET, datetime.now() + timedelta(days=settings.USER_JWT_EXPIRES_DAY),
            {"id": user.id})

    def get_login_user_id(self, access_token: str) -> str:
        try:
            payload = jwt_utility.decode(access_token, settings.USER_JWT_SECRET)
            return payload.get("id", "")
        except Exception:
            return ""
