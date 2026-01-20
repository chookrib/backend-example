from datetime import datetime

from pydantic import BaseModel


class UserDto(BaseModel):
    """用户 DTO"""

    id: str
    username: str
    # password: str
    nickname: str
    mobile: str
    is_admin: bool
    created_at: datetime
    updated_at: datetime

    def to_json(self):
        return {
            "id": self.id,
            "username": self.username,
            "nickname": self.nickname,
            "mobile": self.mobile,
            "isAdmin": self.is_admin,
            # "createdAt": self.created_at.isoformat(),
            "createdAt": self.created_at,
            "updatedAt": self.updated_at
        }
