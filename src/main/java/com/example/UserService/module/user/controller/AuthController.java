package com.example.UserService.module.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.UserService.module.user.dto.RegisterRequest;
import com.example.UserService.module.user.dto.RegisterResponse;
import com.example.UserService.module.user.model.User;
import com.example.UserService.module.user.service.AuthService;
import com.example.UserService.module.user.utils.ResponseWrapper.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity <ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest user) {
        
        // Call the AuthService to register the user
        User newUser = authService.registerUser(user.getUsername(), user.getPassword());

        RegisterResponse response = new RegisterResponse(newUser.getId(), newUser.getUserName());
        
        // Return a success response
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", "200", response));
    }

    @PostMapping("login")
    public String login(@RequestBody String credentials) {
        return "Logged in with: " + credentials;   
    }

    @PostMapping("logout")
    public String logout(@RequestBody String user) {
        return "Logged out token: " + user;   
    }
    
    @PostMapping("refresh-token")
    public String refreshToken(@RequestBody String token) {
        return "Refreshed token: " + token;   
    }

}
