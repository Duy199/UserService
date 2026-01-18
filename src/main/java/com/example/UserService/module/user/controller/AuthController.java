package com.example.UserService.module.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.UserService.module.user.dto.LoginRequest;
import com.example.UserService.module.user.dto.LoginResponse;
import com.example.UserService.module.user.dto.RefreshTokenRequest;
import com.example.UserService.module.user.dto.RefreshTokenResponse;
import com.example.UserService.module.user.dto.RegisterRequest;
import com.example.UserService.module.user.model.User;
import com.example.UserService.module.user.service.AuthService;
import com.example.UserService.module.user.utils.ResponseWrapper.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Ref;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api/v1/auth")
public class AuthController {


    @Autowired
    AuthService authService;


    @PostMapping("register")
    public ResponseEntity <ApiResponse<String>> register(@Valid @RequestBody RegisterRequest user) {
        
        // Call the AuthService to register the user
        authService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        
        // Return a success response
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", "200", "User registered successfully"));
    }

    @PostMapping("login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticateUser(request.getUserName(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("Login successful", "200", response));   
    }

    @PostMapping("logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.revokeUserSession(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", "200", "Logged out token: " + request.getRefreshToken()));   
    }
    
    @PostMapping("refresh-token")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.getRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Refreshed token: " + request.getRefreshToken(), "200", response));
    }

}
