from fastapi import APIRouter, Depends, HTTPException
from typing import List
from models.session import Session, SessionCreate
from services import session_service
from dependencies import get_current_user

router = APIRouter()

@router.post("/sessions/", response_model=Session)
def start_session(session: SessionCreate, current_user: dict = Depends(get_current_user)):
    return session_service.start_session(session, current_user)

@router.put("/sessions/{session_id}", response_model=Session)
def end_session(session_id: int, score: int):
    session = session_service.end_session(session_id, score)
    if session is None:
        raise HTTPException(status_code=404, detail="Session not found")
    return session

@router.get("/users/{user_id}/sessions", response_model=List[Session])
def get_user_sessions(user_id: int):
    return session_service.get_user_sessions(user_id)
