from models.session import Session, SessionCreate
from typing import List, Optional
from datetime import datetime
# from database import ... # Import your database session

def start_session(session: SessionCreate, current_user: dict) -> Session:

    # new_session = Session(
    #     gameId=session.gameId,
    #     userId=current_user["userId"],
    #     startTime=datetime.now(),
    #     score=0
    # )
    # db.add(new_session)
    # db.commit()
    # db.refresh(new_session)
    # return new_session
    pass

def end_session(session_id: int, score: int) -> Optional[Session]:

    # session = db.query(Session).filter(Session.sessionId == session_id).first()
    # if session:
    #     session.endTime = datetime.now()
    #     session.score = score
    #     db.commit()
    #     db.refresh(session)
    # return session
    pass

def get_user_sessions(user_id: int) -> List[Session]:
    # return db.query(Session).filter(Session.userId == user_id).all()
    pass
