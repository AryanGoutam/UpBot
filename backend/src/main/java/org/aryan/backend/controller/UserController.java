package org.aryan.backend.controller;

import org.aryan.backend.model.User;
import org.aryan.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/users")
    private List<User> getAllUser(){
        return userService.getAll();
    }

    @PostMapping("/auth/register")
    private User addUser(@RequestBody User user){
        return userService.add(user);
//        return user;

    }

    @PostMapping("/auth/login")
    public String login(@RequestBody User user){

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        if(authentication.isAuthenticated()){
            return "Logged in !" ;
        }
        return "Failed to log in ";
    }
}
