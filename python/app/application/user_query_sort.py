from enum import Enum


class UserQuerySort(int, Enum):
    """用户查询Sort"""

    CreateAtAsc = 1
    CreateAtDesc = -1
    UsernameAsc = 2
    UsernameDesc = -2
