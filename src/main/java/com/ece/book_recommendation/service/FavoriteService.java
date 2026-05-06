package com.ece.book_recommendation.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.ece.book_recommendation.model.Book;
import com.ece.book_recommendation.model.Favorite;
import com.ece.book_recommendation.model.User;
import com.ece.book_recommendation.repository.BookRepository;
import com.ece.book_recommendation.repository.FavoriteRepository;
import com.ece.book_recommendation.repository.UserRepository;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           BookRepository bookRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Favorite addFavorite(Long userId, String bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        return favoriteRepository.findByUserAndBook(user, book)
                .orElseGet(() -> favoriteRepository.save(new Favorite(user, book)));
    }

    public List<Favorite> getFavoritesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRepository.findByUser(user);
    }

    public void removeFavorite(Long userId, String bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        favoriteRepository.deleteByUserAndBook(user, book);
    }
    public List<Book> recommendBooks(Long userId) {

    List<Favorite> favorites = getFavoritesByUser(userId);

    if (favorites.isEmpty()) {
        throw new RuntimeException("No favorites found");
    }

    // ilk favorinin kategorisini al (şimdilik basit yapıyoruz)
    String genre = favorites.get(0).getBook().getGenres();

    return bookRepository
            .findByGenresContainingIgnoreCase(genre, Pageable.ofSize(5))
            .getContent();
}
}
