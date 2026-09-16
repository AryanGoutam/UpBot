package org.aryan.backend.service;

import org.aryan.backend.model.User;
import org.aryan.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public List<User> getAll() {

        return userRepo.findAll();

    }

    public User add(User user) {
        if(userRepo.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        user.setCreatedAt(Instant.now());
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }
}
