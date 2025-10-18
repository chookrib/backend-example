from pydantic_settings import BaseSettings


class Settings(BaseSettings):

    SQLITE_DATABASE_FILE: str
    USER_JWT_EXPIRES_DAY: int = 0
    USER_JWT_SECRET: str = ""

    class Config:
        env_file = ".env"
        # env_file = os.getenv("ENV_FILE", ".env")
        # env_file_encoding = "utf-8"


settings = Settings()
