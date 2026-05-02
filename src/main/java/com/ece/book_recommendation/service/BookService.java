package com.ece.book_recommendation.service;

import com.ece.book_recommendation.model.Book;
import com.ece.book_recommendation.repository.BookRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> getBooks(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Book> searchBooks(String query, int page, int size) {
        return bookRepository.findByTitleContainingIgnoreCase(query, PageRequest.of(page, size));
    }

    public Page<Book> getBooksByCategory(String category, int page, int size) {
        return bookRepository.findByCategoriesContainingIgnoreCase(category, PageRequest.of(page, size));
    }

    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }

    public List<Book> filterByCategory(String category) {
        return bookRepository.findByCategoryLike(category);
    }
}