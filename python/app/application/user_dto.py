from datetime import datetime

from pydantic import BaseModel, computed_field

from app.utility import json_utility


class UserDto(BaseModel):
    """用户DTO"""

    id: str
    username: str
    # password: str
    nickname: str
    mobile: str
    is_admin: bool
    created_at: datetime

    # pydantic自动序列化
    @computed_field
    def is_not_admin(self) -> bool:
        return not self.is_admin

    model_config = {
        "alias_generator": json_utility.to_camel,
        "populate_by_name": True
    }

    # 手工序列化
    # def to_json(self):
    #     return {
    #         "id": self.id,
    #         "username": self.username,
    #         "nickname": self.nickname,
    #         "mobile": self.mobile,
    #         "isAdmin": self.is_admin,
    #         # "createdAt": self.created_at.isoformat(),
    #         "createdAt": self.created_at,
    #     }
