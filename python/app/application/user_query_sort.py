from enum import Enum

class UserQuerySort(int, Enum):
    CreateAtAsc = 1
    CreateAtDesc = -1
    UsernameAsc = 2
    UsernameDesc = -2
