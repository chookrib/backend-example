from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """应用配置"""

    APP_NAME: str = ""
    APP_ENV: str = ""

    APP_LOCK_SERVICE: str = "asyncio"
    APP_LOCK_REDIS_URL: str = ""

    APP_SQLITE_PATH: str = ""

    APP_JWT_EXPIRES_DAY: str = ""
    APP_JWT_SECRET_KEY: str = ""

    class Config:
        env_file = ".env"
        # env_file = os.getenv("ENV_FILE", ".env")
        # env_file_encoding = "utf-8"

settings = Settings()
