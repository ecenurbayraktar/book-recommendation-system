package com.ece.book_recommendation.controller;
import com.ece.book_recommendation.model.Book;
import com.ece.book_recommendation.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Page<Book> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.getBooks(page, size);
    }

    @GetMapping("/search")
    public Page<Book> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return bookService.searchBooks(query, page, size);
    }
    @GetMapping("/category")
public Page<Book> getBooksByCategory(
        @RequestParam String category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
) {
    return bookService.getBooksByCategory(category, page, size);
}
}