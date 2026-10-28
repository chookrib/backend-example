from datetime import datetime

from pydantic import BaseModel


class UserDto(BaseModel):
    """用户 DTO"""

    id: str
    username: str
    nickname: str
    mobile: str
    is_admin: bool
    created_at: datetime
    updated_at: datetime | None

