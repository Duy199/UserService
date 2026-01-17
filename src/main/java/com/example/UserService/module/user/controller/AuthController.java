package com.example.UserService.module.user.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.UserService.module.user.dto.RegisterRequest;
import com.example.UserService.module.user.dto.RegisterResponse;
import com.example.UserService.module.user.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api/v1/auth")
public class AuthController {


    @Autowired
    AuthService authService;


    @PostMapping("register")
    public String postMethodName(@Valid @RequestBody RegisterRequest user) {
        
        // Call the AuthService to register the user
        authService.registerUser(user.getUsername(), user.getPassword());
        
        // Return a success response
        return new RegisterResponse("User registered successfully").getMessage();
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
