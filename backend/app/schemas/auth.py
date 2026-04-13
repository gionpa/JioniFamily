from pydantic import BaseModel, Field


class LoginRequest(BaseModel):
    avatar_key: str = Field(..., description="mom, dad, or jioni")
    pin: str = Field(..., min_length=4, max_length=4, pattern=r"^\d{4}$")


class TokenResponse(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str = "bearer"


class RefreshRequest(BaseModel):
    refresh_token: str


class ChangePinRequest(BaseModel):
    old_pin: str = Field(..., min_length=4, max_length=4, pattern=r"^\d{4}$")
    new_pin: str = Field(..., min_length=4, max_length=4, pattern=r"^\d{4}$")
