from datetime import datetime

from pydantic import BaseModel

from app.domain.domain_exception import DomainException
from app.domain.user_unique_checker import UserUniqueChecker
from app.utility import crypto_utility


class User(BaseModel):
    """用户Entity"""

    id: str
    username: str
    password: str
    nickname: str
    mobile: str
    is_admin: bool
    created_at: datetime

    @staticmethod
    def restore_user(
            id: str,
            username: str,
            password: str,
            nickname: str,
            mobile: str,
            is_admin: bool,
            created_at: datetime
    ) -> "User":
        """还原用户"""

        return User(
            id=id,
            username=username,
            password=password,
            nickname=nickname,
            mobile=mobile,
            is_admin=is_admin,
            created_at=created_at
        )

    @staticmethod
    def register_user(
            id: str,
            username: str,
            password: str,
            nickname: str,
            user_unique_checker: UserUniqueChecker | None) -> "User":
        """注册用户"""

        if not username:
            raise DomainException("用户名不能为空")

        if not password:
            raise DomainException("密码不能为空")

        if not nickname:
            raise DomainException("昵称不能为空")

        if user_unique_checker is not None:
            if not user_unique_checker.is_username_unique(username):
                raise DomainException("用户名已存在")

            if not user_unique_checker.is_nickname_unique(nickname):
                raise DomainException("昵称已存在")

        return User(
            id=id,
            username=username,
            password=crypto_utility.encode_md5(password),
            nickname=nickname,
            mobile="",
            is_admin=False,
            created_at=datetime.now()
        )

    def is_password_match(self, password: str) -> bool:
        """检查密码是否匹配"""
        return crypto_utility.encode_md5(password) == self.password

    def set_admin(self, is_admin: bool) -> None:
        """设置是否为管理员"""
        self.is_admin = is_admin

    def modify_password(self, old_password: str, new_password: str) -> None:
        """修改密码"""
        if not new_password:
            raise DomainException("密码不能为空")

        if not self.is_password_match(old_password):
            raise DomainException("密码错误")

        self.password = crypto_utility.encode_md5(new_password)

    def modify_nickname(self, nickname: str, user_unique_checker: UserUniqueChecker | None) -> None:
        """修改昵称"""
        if not nickname:
            raise DomainException("昵称不能为空")

        if nickname.lower() != self.nickname.lower() and user_unique_checker is not None:
            if not user_unique_checker.is_nickname_unique(nickname):
                raise DomainException("昵称已存在")

        self.nickname = nickname

    def modify_mobile(self, mobile: str, user_unique_checker: UserUniqueChecker | None) -> None:
        """修改手机"""
        if not mobile:
            raise DomainException("昵称不能为空")

        if mobile.lower() != self.mobile.lower() and user_unique_checker is not None:
            if not user_unique_checker.is_mobile_unique(mobile):
                raise DomainException("手机已存在")

        self.mobile = mobile

    @staticmethod
    def create_user(
            id: str,
            username: str,
            password: str,
            nickname: str,
            mobile: str,
            user_unique_checker: UserUniqueChecker | None
    ) -> "User":
        """创建用户"""
        if not username:
            raise DomainException("用户名不能为空")

        if not password:
            raise DomainException("密码不能为空")

        if not nickname:
            raise DomainException("昵称不能为空")

        if user_unique_checker is not None:
            if not user_unique_checker.is_username_unique(username):
                raise DomainException("用户名已存在")

            if not user_unique_checker.is_nickname_unique(nickname):
                raise DomainException("昵称已存在")

            if mobile and not user_unique_checker.is_mobile_unique(mobile):
                raise DomainException("手机已存在")

        return User(
            id=id,
            username=username,
            password=crypto_utility.encode_md5(password),
            nickname=nickname,
            mobile=mobile,
            is_admin=False,
            created_at=datetime.now()
        )

    def modify(self, username: str,
               nickname: str,
               mobile: str,
               user_unique_checker: UserUniqueChecker | None):
        """修改用户"""
        if not username:
            raise DomainException("用户名不能为空")

        if not nickname:
            raise DomainException("昵称不能为空")

        if user_unique_checker is not None:
            if username.lower() != self.username.lower() and not user_unique_checker.is_username_unique(username):
                raise DomainException("用户名已存在")

            if nickname.lower() != self.nickname.lower() and not user_unique_checker.is_nickname_unique(nickname):
                raise DomainException("昵称已存在")

            if not mobile and mobile.lower() != self.mobile.lower() and not user_unique_checker.is_mobile_unique(mobile):
                raise DomainException("手机已存在")

        self.username = username
        self.nickname = nickname
        self.mobile = mobile
