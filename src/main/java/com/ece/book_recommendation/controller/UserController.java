package com.ece.book_recommendation.controller;
import org.springframework.web.bind.annotation.*;
import com.ece.book_recommendation.model.User;
import com.ece.book_recommendation.repository.UserRepository;
import java.util.List;
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public User createUser(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password) {
        return userRepository.save(new User(username, email, password));
    }
    @GetMapping
public List<User> getAllUsers() {
    return userRepository.findAll();
}
}