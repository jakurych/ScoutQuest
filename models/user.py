from pydantic import BaseModel, EmailStr
from typing import List, Optional

class UserBase(BaseModel):
    username: str
    email: EmailStr

class UserCreate(UserBase):
    password: str

class User(UserBase):
    userId: int
    points: int
    profilePicture: Optional[str] = None

    class Config:
        orm_mode = True
