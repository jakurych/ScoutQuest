from models.game import Game, GameCreate
from typing import List, Optional
# from database import ... # Import your database session

def create_game(game: GameCreate, current_user: dict) -> Game:

    # new_game = Game(...)
    # db.add(new_game)
    # db.commit()
    # db.refresh(new_game)
    # return new_game
    pass

def list_games(public_only: bool) -> List[Game]:

    # if public_only:
    #     return db.query(Game).filter(Game.isPublic == True).all()
    # return db.query(Game).all()
    pass

def get_game(game_id: int) -> Optional[Game]:
    # return db.query(Game).filter(Game.gameId == game_id).first()
    pass
