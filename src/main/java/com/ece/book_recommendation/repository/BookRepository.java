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

    Page<Book> findByCategoriesContainingIgnoreCase(String category, Pageable pageable);

    @Query("SELECT DISTINCT b.categories FROM Book b WHERE b.categories IS NOT NULL AND b.categories <> ''")
    List<String> findAllCategories();

    @Query("SELECT b FROM Book b WHERE LOWER(b.categories) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Book> findByCategoryLike(@Param("category") String category);
}