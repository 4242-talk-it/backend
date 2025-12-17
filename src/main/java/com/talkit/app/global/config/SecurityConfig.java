package com.talkit.app.global.config;

import com.talkit.app.global.auth.UserDetailsImplService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsImplService userDetailsImplService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 비활성화 (Postman 테스트 및 API 서버용)
                .csrf(AbstractHttpConfigurer::disable)

                // HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)

                // Form Login 설정 (Postman x-www-form-urlencoded 대응)
                .formLogin(form -> form
                        .loginProcessingUrl("/api/users/login") // 로그인 처리 경로
                        .usernameParameter("email")           // 아이디 파라미터명
                        .passwordParameter("password")        // 비밀번호 파라미터명
                        .successHandler((req, res, auth) -> {
                            // 성공 시 JSON 응답 반환
                            res.setStatus(HttpServletResponse.SC_OK);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\": \"로그인 성공\", \"user\": \"" + auth.getName() + "\"}");
                        })
                        .failureHandler((req, res, ex) -> {
                            // 실패 시 401과 에러 메시지 반환
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\": \"로그인 실패: " + ex.getMessage() + "\"}");
                        })
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setStatus(HttpServletResponse.SC_OK);
                            res.getWriter().write("Logout Success");
                        })
                )

                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 화이트리스트: 인증 없이 접근 가능
                        .requestMatchers(
                                "/api/users/signup",
                                "/api/users/login",
                                "/api/community/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 특정 권한이 필요한 경로
                        .requestMatchers(
                                "/api/community/{id}/comment/**",
                                "/api/community/{id}/like/**"
                        ).hasAuthority("ROLE_USER")

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .build();
    }
}