package com.example.xuber.controller;

import com.example.xuber.model.User;
import com.example.xuber.repository.UserRepository;
import com.example.xuber.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> userMap) {
        String username = userMap.get("username");
        String password = userMap.get("password");

        System.out.println("Trying to log in with credentials: " + username + " " + password);

        boolean loginResult = userService.login(username, password);
        System.out.println("Logged? " + loginResult);
        if (loginResult) return ResponseEntity.ok("Logged in.");
        else return ResponseEntity.badRequest().body("Bad credentials.");

    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {

        System.out.println("Trying to register user: " + user);

        boolean registerResult = userService.register(user);
        System.out.println("Registered? " + registerResult);
        if (registerResult) return ResponseEntity.ok("Registered.");
        else return ResponseEntity.badRequest().body("Bad data.");

    }


}
