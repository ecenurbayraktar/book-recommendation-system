package com.ece.book_recommendation.repository;
import com.ece.book_recommendation.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface BookRepository extends JpaRepository<Book, String> {
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findByCategoriesContainingIgnoreCase(String category, Pageable pageable);

    
    
}
