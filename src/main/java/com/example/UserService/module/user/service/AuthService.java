package com.example.UserService.module.user.service;


import java.sql.Ref;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.UserService.config.jwt.JwtService;
import com.example.UserService.module.user.dto.LoginResponse;
import com.example.UserService.module.user.dto.RefreshTokenResponse;
import com.example.UserService.module.user.model.Session;
import com.example.UserService.module.user.model.User;
import com.example.UserService.module.user.repository.SessionRepository;
import com.example.UserService.module.user.repository.UserRepository;
import com.example.UserService.module.user.utils.Exceptions.BusinessException;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(
        UserService userService,
        JwtService jwtService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
    }


    public User registerUser(String userName, String email, String password) {
        // Registration logic here
        User user = new User();
        
        if (userRepository.existsByUserName(userName)) {
            throw new BusinessException("USER_ALREADY_EXISTS", "Username already exists", HttpStatus.CONFLICT);
        }
        
        user.setUserName(userName);
        user.setEmail(email);
        
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        
        // Save the user to the database
        userRepository.save(user);

        return user;
    }

    public LoginResponse authenticateUser(String userName, String password) {
        // Authentication logic here
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveUserSession(user, refreshToken, accessToken);

        return new LoginResponse(user.getId(), user.getUserName(), accessToken, refreshToken);
    }

    public RefreshTokenResponse getRefreshToken (String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userService.loadUserByUsername(username);

        boolean tokenActive = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Session not found", HttpStatus.NOT_FOUND))
                .getIsActive();
        
        if (!tokenActive) {
            throw new BusinessException("REFRESH_TOKEN_INACTIVE", "Refresh token is inactive", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BusinessException("REFRESH_TOKEN_INVALID", "Refresh token is invalid or expired", HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        saveUserSession(user, newRefreshToken, newAccessToken);

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    public void saveUserSession (User user, String refreshToken, String accessToken) {
        // Logic to save user session with refresh token
        Session session = new Session();
        session.setUserId(user.getId());
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(System.currentTimeMillis() + jwtService.getRefreshTokenExpiration());
        session.setIsActive(true);
        sessionRepository.save(session); 
    }

    public void revokeUserSession (String refreshToken) {
        // Logic to revoke user session
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Session not found", HttpStatus.NOT_FOUND));
        
        session.setIsActive(false);
        sessionRepository.save(session);
    }
}
    