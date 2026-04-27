package com.ece.book_recommendation.repository;
import com.ece.book_recommendation.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookRepository extends JpaRepository<Book, String> {
    
}
