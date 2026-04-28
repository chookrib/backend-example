from pydantic_settings import BaseSettings


class ApplicationConfig(BaseSettings):
    """应用配置"""

    model_config = {
        "env_file": ".env",
        # "env_file_encoding": "utf-8",
    }

    APP_NAME: str = ""
    APP_ENV: str = ""

    APP_LOCK_SERVICE: str = "asyncio"
    APP_LOCK_REDIS_URL: str = ""

    APP_JWT_SECRET: str = ""
    APP_JWT_EXPIRES: str = ""

    APP_SQLITE_PATH: str = ""

    def is_app_env_dev(self):
        """应用运行环境是否为开发环境"""
        return self.APP_ENV.lower() == "dev"

application_config = ApplicationConfig()
