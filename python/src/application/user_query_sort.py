from enum import Enum


class UserQuerySort(int, Enum):
    """用户查询Sort"""

    CREATED_AT_ASC = 1
    CREATED_AT_DESC = -1
    USERNAME_ASC = 2
    USERNAME_DESC = -2
