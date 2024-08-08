from fastapi import APIRouter, Depends, HTTPException
from models.user import User, UserCreate
from services import user_service
from dependencies import get_current_user

router = APIRouter()

@router.post("/users/", response_model=User)
def create_user(user: UserCreate):
    return user_service.create_user(user)

@router.get("/users/{user_id}", response_model=User)
def get_user(user_id: int):
    user = user_service.get_user(user_id)
    if user is None:
        raise HTTPException(status_code=404, detail="User not found")
    return user

@router.get("/users/me/", response_model=User)
def read_users_me(current_user: User = Depends(get_current_user)):
    return current_user
