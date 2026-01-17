package com.example.UserService.module.user.dto;

public class RegisterResponse {
    private long userId;
    private String userName;


    public RegisterResponse(long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
