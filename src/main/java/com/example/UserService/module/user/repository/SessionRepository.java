package com.example.UserService.module.user.repository;

import com.example.UserService.module.user.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findByRefreshToken(String refreshToken);
}
