from src.config import settings


class Accessor:
    """访问器"""

    def __init__(self):
        # 应用名称
        self.app_name = settings.APP_NAME
        # 应用是否为开发环境
        self.app_env_is_dev = settings.APP_ENV.lower() == "dev"

accessor = Accessor()
