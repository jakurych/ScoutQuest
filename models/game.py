from pydantic import BaseModel
from typing import List, Optional
from .task import Task, TaskCreate

class GameBase(BaseModel):
    name: str
    description: str
    isPublic: bool

class GameCreate(GameBase):
    tasks: List[TaskCreate]

class Game(GameBase):
    gameId: int
    creator: str
    tasks: List[Task]

    class Config:
        orm_mode = True
