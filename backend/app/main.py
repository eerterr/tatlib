from fastapi import FastAPI
from app.routers import books, events, ocr

app = FastAPI(
    title="TatLib API",
    description="Backend for the adaptive Tatar reading application",
    version="1.0.0"
)

app.include_router(
    books.router,
    prefix="/api"
)

app.include_router(
    events.router,
    prefix="/api"
)

app.include_router(
    ocr.router,
    prefix="/api"
)


@app.get("/")
def root():
    return {
        "message": "TatLib API is running"
    }


@app.get("/health")
def health():
    return {
        "status": "ok"
    }