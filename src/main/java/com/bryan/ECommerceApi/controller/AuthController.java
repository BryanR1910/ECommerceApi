package com.bryan.ECommerceApi.controller;

import com.bryan.ECommerceApi.model.dto.AuthResponseDto;
import com.bryan.ECommerceApi.model.dto.LoginRequestDto;
import com.bryan.ECommerceApi.model.dto.RegisterRequestDto;
import com.bryan.ECommerceApi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto dto){
        AuthResponseDto response = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto){
        AuthResponseDto response = authService.login(dto);

        return ResponseEntity.ok(response);
    }
}
