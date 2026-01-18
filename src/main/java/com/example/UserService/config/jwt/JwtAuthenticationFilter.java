package com.example.UserService.config.jwt;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.UserService.module.user.model.User;
import com.example.UserService.module.user.repository.SessionRepository;
import com.example.UserService.module.user.service.UserService;
import com.example.UserService.module.user.utils.Exceptions.BusinessException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final SessionRepository sessionRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService, SessionRepository sessionRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        
        if (token.isEmpty()
            || "null".equalsIgnoreCase(token)
            || "undefined".equalsIgnoreCase(token)
            || token.chars().filter(ch -> ch == '.').count() != 2) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean active = sessionRepository.findByAccessToken(token).orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "Session not found", HttpStatus.NOT_FOUND)).getIsActive();

        if (!active) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
            {
            "success": false,
            "code": "INACTIVE_SESSION",
            "message": "Session is inactive"
            }
            """);
            return;
        }

        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
            {
            "success": false,
            "code": "TOKEN_EXPIRED",
            "message": "Access token expired"
            }
            """);
            return;

        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            // token sai, bị sửa, không hợp lệ
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
            {
            "success": false,
            "code": "INVALID_TOKEN",
            "message": "Invalid access token"
            }
            """);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, user)) {
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        userService.getAuthorities()
                    );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
