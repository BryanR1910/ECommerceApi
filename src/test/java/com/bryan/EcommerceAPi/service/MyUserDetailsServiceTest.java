package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.UserPrincipal;
import com.bryan.ECommerceApi.repository.UserRepo;
import com.bryan.ECommerceApi.service.MyUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyUserDetailsServiceTest {
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @Test
    void givenExistingEmail_whenLoadUserByUsername_thenReturnUserDetails(){
        //given
        User user = new User(1L,"bryan","bryan@gmail.com","encoded",false);

        //when
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        UserDetails response = myUserDetailsService.loadUserByUsername("bryan@gmail.com");

        //then
        verify(userRepo).findByEmail(user.getEmail());
        assertEquals(response.getUsername(),user.getEmail());

    }

    @Test
    void givenNonExistingEmail_whenLoadUserByUsername_thenReturnUserDetails(){
        //when
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername("bryan@gmail.com"));
    }
}
