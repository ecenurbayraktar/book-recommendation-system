# BookBloom

BookBloom is a Spring Boot book recommendation web app with a PostgreSQL database and a Python FastAPI recommendation service.

The app lets users browse books, search and filter by category, save favorites, and receive recommendations based on the genres of their favorite books.

## Tech Stack

- Java 21, Spring Boot
- Spring Data JPA, PostgreSQL
- Python FastAPI
- scikit-learn TF-IDF and cosine similarity
- Docker Compose
- HTML, CSS, JavaScript

## Project Structure

```text
.
├── docker-compose.yml
├── recommender-service/
│   ├── Dockerfile
│   ├── main.py
│   └── requirements.txt
└── src/
    ├── main/java/com/ece/book_recommendation/
    └── main/resources/
        ├── data/book_dataset.csv
        └── static/
```

## Dataset

The application loads books from:

```text
src/main/resources/data/book_dataset.csv
```

This CSV is included in the repository so a fresh clone can run without manual dataset setup. If you replace it, keep the same CSV columns or update `DataLoader`.

## Run Locally

Start PostgreSQL and the Python recommendation service:

```powershell
docker compose up -d --build
```

Run the Spring Boot app:

```powershell
.\mvnw.cmd spring-boot:run
```

Open:

```text
http://localhost:8080/books.html
```

FastAPI health check:

```text
http://localhost:8000/health
```

## Configuration

Default local values are provided in `application.properties`.

You can override them with environment variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
RECOMMENDER_API_URL
```

## Recommendation Flow

1. User adds books to favorites.
2. Spring Boot extracts the favorite books' genres.
3. Spring sends favorite genres and candidate books to `POST /recommend` on the FastAPI service.
4. FastAPI scores candidate books with TF-IDF and cosine similarity.
5. Spring receives recommended book IDs and returns full book records from PostgreSQL.

If the Python service is unavailable, Spring falls back to a simple genre-based recommendation.

## Useful Commands

Run tests:

```powershell
.\mvnw.cmd test
```

Build without running tests:

```powershell
.\mvnw.cmd -DskipTests package
```

Stop Docker services:

```powershell
docker compose down
```

## API Highlights

- `GET /books`
- `GET /books/search?query=...`
- `GET /books/categories`
- `POST /favorites?userId=...&bookId=...`
- `GET /favorites/user/{userId}`
- `GET /favorites/recommend/{userId}`

## Notes

- PostgreSQL data is persisted in a Docker volume.
- The test profile uses an in-memory H2 database, so tests do not require PostgreSQL.
- Do not commit generated files such as `target/`, `__pycache__/`, or `*.pyc`.
