package com.ece.book_recommendation.model;
import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
@Id
    private String bookId;

    @Column(length = 1000)
    private String title;

    @Column(length = 1000)
    private String authors;

    @Column(length = 1000)
    private String publisher;

    private String publishedDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer pageCount;

    @Column(length = 1000)
    private String categories;

    private Double averageRating;

    private Integer ratingsCount;

    @Column(length = 1000)
    private String language;

    @Column(length = 1000)
    private String thumbnail;

    @Column(length = 1000)
    private String searchCategory;

    public Book() {
    }

    public Book(String bookId, String title, String authors, String publisher,
                String publishedDate, String description, Integer pageCount,
                String categories, Double averageRating, Integer ratingsCount,
                String language, String thumbnail, String searchCategory) {
        this.bookId = bookId;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
        this.categories = categories;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.language = language;
        this.thumbnail = thumbnail;
        this.searchCategory = searchCategory;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public String getCategories() {
        return categories;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public String getLanguage() {
        return language;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSearchCategory() {
        return searchCategory;
    }
}