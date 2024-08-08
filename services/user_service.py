from models.user import User, UserCreate
from typing import Optional
# from database import ... # Import your database session

def create_user(user: UserCreate) -> User:
    # new_user = User(...)
    # db.add(new_user)
    # db.commit()
    # db.refresh(new_user)
    # return new_user
    pass

def get_user(user_id: int) -> Optional[User]:
    # return db.query(User).filter(User.userId == user_id).first()
    pass
