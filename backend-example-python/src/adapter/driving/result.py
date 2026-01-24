from typing import Any

from src.adapter.driving import result_codes


class Result:
    """Controller 返回结果"""

    def __init__(self, code: int, message: str = "", data: Any = None):
        self.code = code
        self.message = message
        self.data = data  # data if data is not None else {}
        self.success = code == result_codes.SUCCESS

    def to_dict(self):
        """转换为dict"""
        return self.__dict__

    @classmethod
    def ok(cls, message: str = "", data: Any = None) -> "Result":
        """成功"""
        return cls(code=result_codes.SUCCESS, message=message, data=data)

    @classmethod
    def error(cls, code: int = result_codes.ERROR_DEFAULT, message: str = "", data: Any = None) -> "Result":
        """失败"""
        if code == result_codes.SUCCESS:
            code = result_codes.ERROR_DEFAULT
        return cls(code=code, message=message, data=data)
