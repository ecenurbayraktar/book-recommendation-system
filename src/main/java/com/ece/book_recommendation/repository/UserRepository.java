package com.ece.book_recommendation.repository;
import com.ece.book_recommendation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
