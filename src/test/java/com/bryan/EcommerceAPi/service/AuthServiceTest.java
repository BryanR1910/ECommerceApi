package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.exception.EmailAlreadyExistsException;
import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.UserPrincipal;
import com.bryan.ECommerceApi.model.dto.AuthResponseDto;
import com.bryan.ECommerceApi.model.dto.LoginRequestDto;
import com.bryan.ECommerceApi.model.dto.RegisterRequestDto;
import com.bryan.ECommerceApi.service.AuthService;
import com.bryan.ECommerceApi.service.CartService;
import com.bryan.ECommerceApi.service.JwtService;
import com.bryan.ECommerceApi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void givenValidData_whenRegister_thenReturnsAuthResponse(){
        //given
        RegisterRequestDto request = new RegisterRequestDto(
                "Bryan","bryan@gmail.com", "1910", true
        );
        User newUser = new User(1L,"bryan","bryan@gmail.com","encoded", true);
        UserPrincipal userPrincipal = new UserPrincipal(newUser);
        Cart newCar = new Cart(newUser);

        //when
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.create(anyString(),anyString(),anyString(),anyBoolean())).thenReturn(newUser);
        when(jwtService.getToken(any(UserDetails.class))).thenReturn("token");

        AuthResponseDto response = authService.register(request);

        //then
        assertEquals(response.accessToken(), "token");

        verify(cartService).create(newUser);
        verify(jwtService).getToken(any(UserDetails.class));
    }

    @Test
    void givenExistingEmail_whenRegister_thenThrowsException(){
        //given
        RegisterRequestDto request = new RegisterRequestDto(
                "Bryan","bryan@gmail.com", "1910", true
        );

        //when-then
        when(userService.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void givenValidData_whenLogin_thenReturnsAuthResponse(){
        //given
        LoginRequestDto request = new LoginRequestDto("bryan@gmail.com","191020");
        User user = new User(1L,"bryan","bryan@gmail.com","encoded",true);

        //when
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(jwtService.getToken(any(UserDetails.class))).thenReturn("token");

        AuthResponseDto response = authService.login(request);

        //then
        assertEquals(response.accessToken(),"token");
    }

}
