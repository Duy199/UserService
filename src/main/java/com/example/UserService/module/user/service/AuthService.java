package com.example.UserService.module.user.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.UserService.module.user.model.User;
import com.example.UserService.module.user.repository.UserRepository;
import com.example.UserService.module.user.utils.Exceptions.BusinessException;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User registerUser(String userName, String password) {
        // Registration logic here
        User user = new User();
        
        if (userRepository.existsByUserName(userName)) {
            throw new BusinessException("USER_ALREADY_EXISTS", "Username already exists");
        }
        
        user.setUserName(userName);
        
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        
        // Save the user to the database
        userRepository.save(user);

        return user;
    }
}
    