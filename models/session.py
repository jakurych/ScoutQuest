from pydantic import BaseModel
from datetime import datetime
from typing import Optional

class SessionBase(BaseModel):
    gameId: int
    userId: int

class SessionCreate(SessionBase):
    pass

class Session(SessionBase):
    sessionId: int
    startTime: datetime
    endTime: Optional[datetime] = None
    score: int

    class Config:
        orm_mode = True
