package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.UserPrincipal;
import com.bryan.ECommerceApi.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService jwtService;
    private String base64Key;

    @BeforeEach
    public void init(){
        this.jwtService = new JwtService();
        this.base64Key = Base64.getEncoder()
                .encodeToString("my-super-secret-key-that-is-long-enough".getBytes());

        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", base64Key);
    }

    @Test
    void givenAdminUser_whenGetToken_thenReturnAccessToken(){
        //given
        User user = new User(1L,"bryan","bryan@gmail.com", "encoded", true);
        UserDetails userPrincipal = new UserPrincipal(user);

        //when
        String token = jwtService.getToken(userPrincipal);
        String email = jwtService.getUserNameFromToken(token);

        //then
        assertEquals(user.getEmail(),email);
    }

    @Test
    void givenNormalUser_whenGetToken_thenReturnAccessToken(){
        //given
        User user = new User(1L,"bryan","bryan@gmail.com", "encoded", false);
        UserDetails userPrincipal = new UserPrincipal(user);

        //when
        String token = jwtService.getToken(userPrincipal);
        String email = jwtService.getUserNameFromToken(token);

        //then
        assertEquals(user.getEmail(),email);
    }

    @Test
    void givenToken_whenTokenValid_thenReturnTrue(){
        //given
        User user = new User(1L,"bryan","bryan@gmail.com","encoded",true);
        UserDetails userPrincipal = new UserPrincipal(user);
        String token = jwtService.getToken(userPrincipal);

        //when
        boolean isTokenValid = jwtService.isTokenValid(token,userPrincipal);

        //then

        assertTrue(isTokenValid);
    }

    @Test
    void givenToken_whenInvalidUserToken_thenReturnFalse(){
        //given
        User user = new User(1L,"bryan","bryan@gmail.com","encoded",true);
        User other = new User(2L,"other","other@gmail.com","encoded",false);
        UserDetails userPrincipal = new UserPrincipal(user);
        UserDetails otherPrincipal = new UserPrincipal(other);
        String token = jwtService.getToken(userPrincipal);

        //when
        boolean isTokenValid = jwtService.isTokenValid(token,otherPrincipal);

        //then

        assertFalse(isTokenValid);
    }

}
