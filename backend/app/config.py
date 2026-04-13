from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    APP_NAME: str = "JioniFamily API"
    DATABASE_URL: str = "sqlite+aiosqlite:///./jionifamily.db"
    JWT_SECRET: str = "jioni-family-secret-change-in-production"
    JWT_ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_DAYS: int = 7
    REFRESH_TOKEN_EXPIRE_DAYS: int = 30
    TIMEZONE: str = "Asia/Seoul"

    model_config = {"env_file": ".env", "extra": "ignore"}

    @property
    def async_database_url(self) -> str:
        """Convert DATABASE_URL to async-compatible format.
        Railway provides postgresql:// but asyncpg needs postgresql+asyncpg://
        """
        url = self.DATABASE_URL
        if url.startswith("postgresql://"):
            return url.replace("postgresql://", "postgresql+asyncpg://", 1)
        if url.startswith("postgres://"):
            return url.replace("postgres://", "postgresql+asyncpg://", 1)
        return url


settings = Settings()
