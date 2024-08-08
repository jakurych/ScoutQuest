from pydantic import BaseModel
from typing import Optional

class Location(BaseModel):
    latitude: float
    longitude: float

class TaskBase(BaseModel):
    title: Optional[str] = None
    description: str
    points: int
    location: Location
    markerColor: str

class TaskCreate(TaskBase):
    pass

class Task(TaskBase):
    taskId: int
    sequenceNumber: int
    gameId: int

    class Config:
        orm_mode = True
