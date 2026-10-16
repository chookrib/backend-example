from abc import ABC, abstractmethod


class SmsGateway(ABC):
    """短信Gateway接口"""

    @abstractmethod
    async def send_code(self, mobile: str, code: str) -> None:
        """发送验证码"""
        pass
