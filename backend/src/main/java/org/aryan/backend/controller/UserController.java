package org.aryan.backend.controller;

import org.aryan.backend.model.User;
import org.aryan.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    private List<User> getAllUser(){
        return userService.getAll();
    }

    @PostMapping("/users")
    private User addUser(@RequestBody User user){
        return userService.add(user);
//        return user;

    }
}
