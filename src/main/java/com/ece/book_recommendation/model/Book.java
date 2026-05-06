package com.ece.book_recommendation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @Column(name = "book_id")
    private String isbn;

    @Column(length = 1000)
    private String title;

    @Column(name = "title_complete", length = 1000)
    private String titleComplete;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String genres;

    @Column(length = 1000)
    private String publisher;

    @Column(length = 1000)
    private String author;

    @Column(name = "book_characters", columnDefinition = "TEXT")
    private String characters;

    @Column(columnDefinition = "TEXT")
    private String places;

    @Column(name = "rating_histogram", length = 1000)
    private String ratingHistogram;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "ratings_count")
    private Integer ratingsCount;

    @Column(name = "reviews_count")
    private Integer reviewsCount;

    @Column(name = "num_pages")
    private Integer numPages;

    @Column(length = 100)
    private String language;

    public Book() {
    }

    public Book(String isbn, String title, String titleComplete, String description, String genres,
                String publisher, String author, String characters, String places, String ratingHistogram,
                Double averageRating, Integer ratingsCount, Integer reviewsCount, Integer numPages,
                String language) {
        this.isbn = isbn;
        this.title = title;
        this.titleComplete = titleComplete;
        this.description = description;
        this.genres = genres;
        this.publisher = publisher;
        this.author = author;
        this.characters = characters;
        this.places = places;
        this.ratingHistogram = ratingHistogram;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.reviewsCount = reviewsCount;
        this.numPages = numPages;
        this.language = language;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getBookId() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleComplete() {
        return titleComplete;
    }

    public String getDescription() {
        return description;
    }

    public String getGenres() {
        return genres;
    }

    public String getCategories() {
        return genres;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthors() {
        return author;
    }

    public String getCharacters() {
        return characters;
    }

    public String getPlaces() {
        return places;
    }

    public String getRatingHistogram() {
        return ratingHistogram;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public Integer getNumPages() {
        return numPages;
    }

    public Integer getPageCount() {
        return numPages;
    }

    public String getLanguage() {
        return language;
    }

    public String getPublishedDate() {
        return null;
    }

    public String getThumbnail() {
        return null;
    }

    public String getSearchCategory() {
        if (genres == null || genres.isBlank()) {
            return null;
        }

        return genres.split(",")[0].trim();
    }
}
