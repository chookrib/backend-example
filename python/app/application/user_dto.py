from datetime import datetime

from pydantic import BaseModel


class UserDto(BaseModel):
    """用户DTO"""

    id: str
    username: str
    # password: str
    nickname: str
    mobile: str
    is_admin: bool
    created_at: datetime