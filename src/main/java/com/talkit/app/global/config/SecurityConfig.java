package com.talkit.app.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] WHITE_LIST_SWAGGER = {
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api-docs/**",
        "/v3/api-docs/**"
    };

    private static final String[] WHITE_LIST_AUTH = {
        "/api/users/signup",
        "/api/users/login"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Swagger 허용
                .requestMatchers(WHITE_LIST_SWAGGER).permitAll()
                // 회원가입, 로그인 허용
                .requestMatchers(WHITE_LIST_AUTH).permitAll()
                .requestMatchers("/api/community/**").permitAll()
                // 특정 경로는 USER 권한 필요
                .requestMatchers(
                    "/api/community/{id}/comment/**",
                    "/api/community/{id}/like/**"
                ).hasAuthority("ROLE_USER")
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .build();
    }
}
