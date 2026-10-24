package com.manish.pragyan.modules.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manish.pragyan.common.response.ApiResponse;
import com.manish.pragyan.modules.auth.dtos.LoginRequestDto;
import com.manish.pragyan.modules.auth.dtos.LoginResponseDto;
import com.manish.pragyan.modules.auth.dtos.RegisterRequestDto;
import com.manish.pragyan.modules.auth.dtos.RegisterResponseDto;
import com.manish.pragyan.modules.auth.services.AuthService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public String health() {
        return "running...";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDto>>  register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        var response = authService.localRegister(registerRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "User registered successfully",
                        response
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        var response = authService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success("Login login sucessfully", response));
    }
    
}
