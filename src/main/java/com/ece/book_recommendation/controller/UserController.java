package com.ece.book_recommendation.controller;
import org.springframework.web.bind.annotation.*;
import com.ece.book_recommendation.model.User;
import com.ece.book_recommendation.repository.UserRepository;
import java.util.List;
@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        User found = userRepository.findByEmail(user.getEmail());

        if (found != null && found.getPassword().equals(user.getPassword())) {
            return found;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}