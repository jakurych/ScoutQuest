from fastapi import FastAPI
from routers import game_router, user_router, session_router

app = FastAPI()

app.include_router(game_router.router)
app.include_router(user_router.router)
app.include_router(session_router.router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
