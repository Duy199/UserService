package com.example.UserService.config.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.example.UserService.module.user.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {
    
    private final JwtProperties props;

    public JwtService(JwtProperties props) {
        this.props = props;
    }
    

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUserName())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + props.getAccessTokenExp()))
            .signWith(getKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUserName())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + props.getRefreshTokenExp()))
            .signWith(getKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public long getRefreshTokenExpiration() {
        return props.getRefreshTokenExp();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        return extractUsername(token).equals(user.getUserName())
            && !extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes());
    }
}
