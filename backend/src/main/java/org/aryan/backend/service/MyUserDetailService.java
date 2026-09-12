package org.aryan.backend.service;

import org.aryan.backend.model.User;
import org.aryan.backend.model.UserPrincipal;
import org.aryan.backend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username);

        if(user == null){
            System.out.println("User Not found!");
            throw new UsernameNotFoundException("User 404");
        }
        return new UserPrincipal(user);
    }
}
