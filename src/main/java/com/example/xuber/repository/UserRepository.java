package com.example.xuber.repository;

import com.example.xuber.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByEmail(String email);
    Optional<User> findById(int id);
    public User findByUsername(String username);
    public User findByEmailAndPassword(String email, String password);
    public User findByUsernameAndPassword(String username, String password);


}
