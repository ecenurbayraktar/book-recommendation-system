package com.ece.book_recommendation.repository;

import com.ece.book_recommendation.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByGenresContainingIgnoreCase(String genre, Pageable pageable);

    boolean existsByIsbnAndTitleCompleteIsNotNull(String isbn);

    @Query("SELECT DISTINCT b.genres FROM Book b WHERE b.genres IS NOT NULL AND b.genres <> ''")
    List<String> findAllGenres();

    @Query("SELECT b FROM Book b WHERE LOWER(b.genres) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Book> findByGenreLike(@Param("genre") String genre);
}
