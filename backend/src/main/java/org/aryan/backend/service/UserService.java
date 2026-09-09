package org.aryan.backend.service;

import org.aryan.backend.model.User;
import org.aryan.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public List<User> getAll() {

        return userRepo.findAll();

    }

    public User add(User user) {
        user.setCreatedAt(Instant.now());
        return userRepo.save(user);
    }
}
