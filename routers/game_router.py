from fastapi import APIRouter, Depends, HTTPException
from typing import List
from models.game import Game, GameCreate
from services import game_service
from dependencies import get_current_user

router = APIRouter()

@router.post("/games/", response_model=Game)
def create_game(game: GameCreate, current_user: dict = Depends(get_current_user)):
    return game_service.create_game(game, current_user)

@router.get("/games/", response_model=List[Game])
def list_games(public_only: bool = False):
    return game_service.list_games(public_only)

@router.get("/games/{game_id}", response_model=Game)
def get_game(game_id: int):
    game = game_service.get_game(game_id)
    if game is None:
        raise HTTPException(status_code=404, detail="Game not found")
    return game
