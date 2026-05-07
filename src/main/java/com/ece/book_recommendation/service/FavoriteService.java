package com.ece.book_recommendation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final RecommenderClient recommenderClient;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           BookRepository bookRepository,
                           RecommenderClient recommenderClient) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.recommenderClient = recommenderClient;
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

        List<String> favoriteGenres = favorites.stream()
                .map(Favorite::getBook)
                .map(Book::getGenres)
                .filter(genre -> genre != null && !genre.isBlank())
                .toList();

        Set<String> favoriteBookIds = favorites.stream()
                .map(Favorite::getBook)
                .map(Book::getBookId)
                .collect(Collectors.toSet());

        List<Book> candidates = bookRepository.findAll().stream()
                .filter(book -> !favoriteBookIds.contains(book.getBookId()))
                .filter(book -> book.getGenres() != null && !book.getGenres().isBlank())
                .toList();

        try {
            List<String> recommendedIds = recommenderClient.recommend(favoriteGenres, candidates, 5);
            Map<String, Book> booksById = bookRepository.findAllById(recommendedIds).stream()
                    .collect(Collectors.toMap(Book::getBookId, Function.identity()));

            List<Book> recommendedBooks = recommendedIds.stream()
                    .map(booksById::get)
                    .filter(book -> book != null)
                    .toList();

            if (!recommendedBooks.isEmpty()) {
                return recommendedBooks;
            }
        } catch (Exception e) {
            System.out.println("Recommendation service unavailable, using fallback: " + e.getMessage());
        }

        return fallbackRecommendations(favoriteGenres);
    }

    private List<Book> fallbackRecommendations(List<String> favoriteGenres) {
        if (favoriteGenres.isEmpty()) {
            return bookRepository.findAll(Pageable.ofSize(5)).getContent();
        }

        return bookRepository
                .findByGenresContainingIgnoreCase(firstGenre(favoriteGenres.get(0)), Pageable.ofSize(5))
                .getContent();
    }

    private String firstGenre(String genres) {
        return genres.split(",")[0].trim();
    }
}
