package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.UserPrincipal;
import com.bryan.ECommerceApi.model.dto.AuthResponseDto;
import com.bryan.ECommerceApi.model.dto.LoginRequestDto;
import com.bryan.ECommerceApi.model.dto.RegisterRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthService(JwtService jwtService, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto register(RegisterRequestDto dto){
        User user = userService.create(dto.name(),dto.email(), dto.password(), dto.isAdmin());
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String token = jwtService.getToken(userPrincipal);

        return new AuthResponseDto(token);
    }

    public AuthResponseDto login(LoginRequestDto dto){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.password()));
        User user = userService.findByEmail(dto.email());
        String token = jwtService.getToken(new UserPrincipal(user));

        return new AuthResponseDto(token);
    }

}
