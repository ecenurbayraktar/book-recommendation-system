package com.ece.book_recommendation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ece.book_recommendation.model.Book;
import com.ece.book_recommendation.model.Favorite;
import com.ece.book_recommendation.model.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    Optional<Favorite> findByUserAndBook(User user, Book book);

    void deleteByUserAndBook(User user, Book book);
}