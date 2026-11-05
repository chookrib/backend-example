from datetime import datetime

from pydantic import BaseModel

from src.domain.domain_exception import DomainException
from src.domain.user.user_uniqueness_validator import UserUniquenessValidator
from src.utility import crypto_utility, value_utility


class User(BaseModel):
    """用户 Entity"""

    id: str
    username: str
    password: str       # 加密后的密码
    nickname: str
    mobile: str
    is_admin: bool
    created_at: datetime
    updated_at: datetime | None

    @staticmethod
    def restore(
            id: str,
            username: str,
            password: str,
            nickname: str,
            mobile: str,
            is_admin: bool,
            created_at: datetime,
            updated_at: datetime | None
    ) -> "User":
        """还原用户"""
        return User(
            id=value_utility.require_not_blank(id),
            username=value_utility.require_not_blank(username),
            password=value_utility.require_not_blank(password),
            nickname=value_utility.require_not_blank(nickname),
            mobile=value_utility.require_non_none(mobile),
            is_admin=is_admin,
            created_at=value_utility.require_non_none(created_at),
            updated_at=updated_at
        )

    @staticmethod
    async def register(
            id: str,
            username: str,
            password: str,
            nickname: str,
            user_unique_specification: UserUniquenessValidator | None) -> "User":
        """注册用户"""
        username = value_utility.require_not_blank(username, "用户名不能为空")
        password = value_utility.require_not_blank(password, "密码不能为空")
        nickname = value_utility.require_not_blank(nickname, "昵称不能为空")

        if user_unique_specification is not None:
            if not await user_unique_specification.is_username_available(username):
                raise DomainException("用户名已存在")

            if not await user_unique_specification.is_nickname_available(nickname):
                raise DomainException("昵称已存在")

        return User(
            id=id,
            username=username,
            password=crypto_utility.md5_encode(password),
            nickname=nickname,
            mobile="",
            is_admin=False,
            created_at=value_utility.get_current_datetime(),
            updated_at=None
        )

    def is_password_match(self, password: str) -> bool:
        """检查密码是否匹配"""
        if value_utility.is_blank(password):
            return False    # 空密码永远返回否
        return self.password == crypto_utility.md5_encode(password)

    def set_admin(self, is_admin: bool) -> None:
        """设置是否为管理员"""
        self.is_admin = is_admin
        self.updated_at = value_utility.get_current_datetime()

    def modify_password(self, old_password: str, new_password: str) -> None:
        """修改密码"""
        old_password = value_utility.require_not_blank(old_password, "旧密码不能为空")
        new_password = value_utility.require_not_blank(new_password, "新密码不能为空")

        if not self.is_password_match(old_password):
            raise DomainException("密码错误")

        self.password = crypto_utility.md5_encode(new_password)
        self.updated_at = value_utility.get_current_datetime()

    async def modify_nickname(self, nickname: str, user_unique_specification: UserUniquenessValidator | None) -> None:
        """修改昵称"""
        nickname = value_utility.require_not_blank(nickname, "昵称不能为空");

        if nickname.lower() != self.nickname.lower() and user_unique_specification is not None:
            if not await user_unique_specification.is_nickname_available(nickname):
                raise DomainException("昵称已存在")

        self.nickname = nickname
        self.updated_at = value_utility.get_current_datetime()

    async def modify_mobile(self, mobile: str, user_unique_specification: UserUniquenessValidator | None) -> None:
        """修改手机"""
        mobile = value_utility.require_not_blank(mobile, "手机不能为空");

        if mobile.lower() != self.mobile.lower() and user_unique_specification is not None:
            if not await user_unique_specification.is_mobile_available(mobile):
                raise DomainException("手机已存在")

        self.mobile = mobile
        self.updated_at = value_utility.get_current_datetime()

    @staticmethod
    async def create(
            id: str,
            username: str,
            password: str,
            nickname: str,
            mobile: str,
            is_admin: bool,
            user_unique_specification: UserUniquenessValidator | None
    ) -> "User":
        """创建用户"""
        username = value_utility.require_not_blank(username, "用户名不能为空")
        password = value_utility.require_not_blank(password, "密码不能为空")
        nickname = value_utility.require_not_blank(nickname, "昵称不能为空")
        mobile = value_utility.empty_if_none(mobile)

        if user_unique_specification is not None:
            if not await user_unique_specification.is_username_available(username):
                raise DomainException("用户名已存在")

            if not await user_unique_specification.is_nickname_available(nickname):
                raise DomainException("昵称已存在")

            if not value_utility.is_blank(mobile) and not await user_unique_specification.is_mobile_available(mobile):
                raise DomainException("手机已存在")

        return User(
            id=id,
            username=username,
            password=crypto_utility.md5_encode(password),
            nickname=nickname,
            mobile=mobile,
            is_admin=is_admin,
            created_at=value_utility.get_current_datetime(),
            updated_at=None
        )

    async def modify(self, username: str,
                     nickname: str,
                     mobile: str,
                     is_admin: bool,
                     user_unique_specification: UserUniquenessValidator | None):
        """修改用户"""
        username = value_utility.require_not_blank(username, "用户名不能为空")
        nickname = value_utility.require_not_blank(nickname, "昵称不能为空")
        mobile = value_utility.empty_if_none(mobile)

        if user_unique_specification is not None:
            if (username.lower() != self.username.lower() and
                    not await user_unique_specification.is_username_available(username)):
                raise DomainException("用户名已存在")

            if (nickname.lower() != self.nickname.lower() and
                    not await user_unique_specification.is_nickname_available(nickname)):
                raise DomainException("昵称已存在")

            if (not value_utility.is_blank(mobile) and mobile.lower() != self.mobile.lower() and
                    not await user_unique_specification.is_mobile_available(mobile)):
                raise DomainException("手机已存在")

        self.username = username
        self.nickname = nickname
        self.mobile = mobile
        self.is_admin = is_admin
        self.updated_at = value_utility.get_current_datetime()
