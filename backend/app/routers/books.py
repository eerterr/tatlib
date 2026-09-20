from fastapi import APIRouter, HTTPException

from app.database import get_books, get_book


router = APIRouter(
    prefix="/books",
    tags=["Books"]
)


@router.get("")
def read_books():
    return get_books()


@router.get("/{book_id}")
def read_book(book_id: int):
    book = get_book(book_id)

    if book is None:
        raise HTTPException(
            status_code=404,
            detail="Book not found"
        )

    return book