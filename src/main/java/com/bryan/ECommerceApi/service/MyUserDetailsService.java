package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.UserPrincipal;
import com.bryan.ECommerceApi.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(() ->
            new UsernameNotFoundException(email)
        );
        return new UserPrincipal(user);
    }
}
