from typing import Any


class Result:
    """通用结果类，用于API响应"""

    CODE_SUCCESS = 0
    # 默认错误
    CODE_ERROR_DEFAULT = 1
    # 未登录
    CODE_ERROR_NOT_LOGIN = -1

    def __init__(self, code: int, message: str = "", data: Any = None):
        self.code = code
        self.message = message
        self.data = data    # data if data is not None else {}
        self.success = code == Result.CODE_SUCCESS

    def to_dict(self):
        return self.__dict__

    @classmethod
    def ok(cls, message: str = "", data: Any = None) -> "Result":
        return cls(code=Result.CODE_SUCCESS, message=message, data=data)

    @classmethod
    def error(cls, code: int = CODE_ERROR_DEFAULT, message: str = "", data: Any = None) -> "Result":
        return cls(code=code, message=message, data=data)
