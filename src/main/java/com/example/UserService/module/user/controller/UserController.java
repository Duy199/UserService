package com.example.UserService.module.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @GetMapping("/{id}")
    public String getUserProfile(@RequestParam String id) {
        return "User profile for ID: " + id;
    }
}
