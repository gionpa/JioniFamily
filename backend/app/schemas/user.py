from pydantic import BaseModel


class UserResponse(BaseModel):
    id: str
    name: str
    role: str
    avatar_key: str
    coin_balance: int

    model_config = {"from_attributes": True}


class FamilyMemberResponse(BaseModel):
    id: str
    name: str
    role: str
    avatar_key: str

    model_config = {"from_attributes": True}
