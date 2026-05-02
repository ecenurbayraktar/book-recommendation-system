package com.ece.book_recommendation.controller;


import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.ece.book_recommendation.model.Book;
import com.ece.book_recommendation.model.Favorite;
import com.ece.book_recommendation.service.FavoriteService;


@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // ⭐ Favoriye ekle
    @PostMapping
    public Favorite addFavorite(@RequestParam Long userId,
                                @RequestParam String bookId) {
        return favoriteService.addFavorite(userId, bookId);
    }

    // ⭐ Kullanıcının favorileri
    @GetMapping("/user/{userId}")
    public List<Favorite> getFavorites(@PathVariable Long userId) {
        return favoriteService.getFavoritesByUser(userId);
    }

    // ⭐ Favoriden çıkar
    @DeleteMapping
    public void removeFavorite(@RequestParam Long userId,
                               @RequestParam String bookId) {
        favoriteService.removeFavorite(userId, bookId);
    }
    @GetMapping("/recommend/{userId}")
public List<Book> recommend(@PathVariable Long userId) {
    return favoriteService.recommendBooks(userId);
}
}