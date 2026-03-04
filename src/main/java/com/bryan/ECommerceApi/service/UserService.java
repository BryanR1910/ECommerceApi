package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.repository.UserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepo userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public User create(String name, String email, String password, boolean admin) {
        User user = new User(name, email, encoder.encode(password), admin);
        return userRepo.save(user);
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    public boolean existsByEmail(String email){
        return userRepo.existsByEmail(email);
    }
}
