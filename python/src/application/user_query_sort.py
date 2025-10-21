from enum import Enum


class UserQuerySort(int, Enum):
    """用户查询Sort"""

    CREATED_AT_ASC = 1          # 创建时间升序
    CREATED_AT_DESC = -1        # 创建时间降序
    USERNAME_ASC = 2            # 用户名升序
    USERNAME_DESC = -2          # 用户名降序
