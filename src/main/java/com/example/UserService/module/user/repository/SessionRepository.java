package com.example.UserService.module.user.repository;

import com.example.UserService.module.user.model.Session;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByRefreshToken(String refreshToken);

    Optional<Session> findByAccessToken(String accessToken);

}
