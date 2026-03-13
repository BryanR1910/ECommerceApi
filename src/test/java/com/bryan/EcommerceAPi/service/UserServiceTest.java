package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.dto.RegisterRequestDto;
import com.bryan.ECommerceApi.repository.UserRepo;
import com.bryan.ECommerceApi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void givenNewUserData_whenCreate_thenReturnUser(){
        //given
        RegisterRequestDto request = new RegisterRequestDto("bryan", "bryan@gmail.com","191020",false);
        User user = new User(1L,"bryan","bryan@gmail.com","encoded",false);

        //when
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        User savedUser = userService.create(request.name(),request.email(),request.password(),request.isAdmin());

        //then
        assertEquals(user,savedUser);
        verify(passwordEncoder).encode(request.password());
        assertEquals("encoded",savedUser.getPassword());
    }

    @Test
    void givenExistingEmail_whenFindByEmail_thenReturnUser(){
        //given
        User user = new User(1L,"bryan","bryan@gmail.com","encoded",false);

        //when
        when(userRepo.findByEmail("bryan@gmail.com")).thenReturn(Optional.of(user));
        User foundUser = userService.findByEmail("bryan@gmail.com");

        //then
        assertEquals(user, foundUser);
    }

    @Test
    void givenNonExistingEmail_whenFindByEmail_thenThrowException(){
        //when
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(UsernameNotFoundException.class, () -> userService.findByEmail("notfound@gmail.com"));
    }

    @Test
    void givenExistingEmail_whenExistsByEmail_thenReturnTrue(){
        //when
        when(userRepo.existsByEmail("bryan@gmail.com")).thenReturn(true);
        boolean exists = userService.existsByEmail("bryan@gmail.com");

        //then
        assertTrue(exists);
    }

    @Test
    void givenNonExistingEmail_whenExistsByEmail_thenReturnFalse(){
        //when
        when(userRepo.existsByEmail("bryan@gmail.com")).thenReturn(false);
        boolean exists = userService.existsByEmail("bryan@gmail.com");

        //then
        assertFalse(exists);
    }

}
