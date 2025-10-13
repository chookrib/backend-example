import logging

from src.domain.sms_gateway import SmsGateway

logger = logging.getLogger(__name__)


class SmsGatewayAdapter(SmsGateway):
    """短信接口Adapter"""

    async def send_code(self, mobile: str, code: str) -> None:
        logger.info(f"发送手机验证码到 {mobile}, code:  {code}")
