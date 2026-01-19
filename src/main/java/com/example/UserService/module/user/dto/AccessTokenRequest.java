package com.example.UserService.module.user.dto;

public class AccessTokenRequest {
    private String accessToken;

    public AccessTokenRequest() {
    }

    public AccessTokenRequest(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
