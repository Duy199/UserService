package com.example.UserService.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.UserService.config.jwt.JwtAccessDeniedHandler;
import com.example.UserService.config.jwt.JwtAuthenticationEntryPoint;
import com.example.UserService.config.jwt.JwtAuthenticationFilter;


@Configuration
// @EnableWebSecurity
// @RequiredArgsConstructor
public class SecurityConfig {

  public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }
  private final JwtAuthenticationFilter jwtAuthFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http,
          JwtAuthenticationEntryPoint entryPoint,
          JwtAccessDeniedHandler deniedHandler,
          JwtAuthenticationFilter jwtFilter
  ) throws Exception {

      return http
          .csrf(csrf -> csrf.disable())
          .exceptionHandling(ex -> ex
              .authenticationEntryPoint(entryPoint)
              .accessDeniedHandler(deniedHandler)
          )
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/v1/auth/**").permitAll()
              .anyRequest().authenticated()
          )
          .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
          .build();
  }

}


