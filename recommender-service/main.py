from math import log1p

from fastapi import FastAPI
from pydantic import BaseModel, Field
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


app = FastAPI(title="Book Recommendation Service")


class CandidateBook(BaseModel):
    bookId: str
    title: str | None = None
    genres: str | None = None
    author: str | None = None
    ratingsCount: int | None = 0


class RecommendationRequest(BaseModel):
    favoriteGenres: list[str] = Field(default_factory=list)
    candidates: list[CandidateBook] = Field(default_factory=list)
    topN: int = 5


class RecommendationItem(BaseModel):
    bookId: str
    score: float


class RecommendationResponse(BaseModel):
    recommendations: list[RecommendationItem]


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/recommend", response_model=RecommendationResponse)
def recommend(payload: RecommendationRequest) -> RecommendationResponse:
    profile = " ".join(filter(None, payload.favoriteGenres)).strip()
    candidates = [book for book in payload.candidates if book.genres]

    if not profile or not candidates:
        return RecommendationResponse(recommendations=[])

    documents = [profile] + [book.genres or "" for book in candidates]
    vectorizer = TfidfVectorizer(
        lowercase=True,
        ngram_range=(1, 2),
        token_pattern=r"(?u)\b[\w\-]+\b",
    )
    matrix = vectorizer.fit_transform(documents)
    similarities = cosine_similarity(matrix[0:1], matrix[1:]).flatten()
    max_rating_count = max((book.ratingsCount or 0 for book in candidates), default=0)

    scored_books = []
    for book, similarity in zip(candidates, similarities):
        popularity = 0.0
        if max_rating_count > 0:
            popularity = log1p(book.ratingsCount or 0) / log1p(max_rating_count)

        score = (similarity * 0.9) + (popularity * 0.1)
        scored_books.append(RecommendationItem(bookId=book.bookId, score=round(float(score), 6)))

    scored_books.sort(key=lambda item: item.score, reverse=True)
    return RecommendationResponse(recommendations=scored_books[: payload.topN])
