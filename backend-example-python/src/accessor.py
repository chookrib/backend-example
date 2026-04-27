from src.config import settings


class Accessor:
    """访问器，方便在应用程序中访问配置和服务"""

    def __init__(self):
        # 应用名称
        self.app_name = settings.APP_NAME
        # 应用运行环境
        self.app_env = settings.APP_ENV

    def app_env_is_dev(self):
        """应用运行环境是否为开发环境"""
        return self.app_env.lower() == "dev"

accessor = Accessor()
