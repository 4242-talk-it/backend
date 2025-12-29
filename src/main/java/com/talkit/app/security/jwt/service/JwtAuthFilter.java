package com.talkit.app.security.jwt.service;

import com.talkit.app.domain.user.entity.User;
import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.global.exception.BusinessLogicException;
import com.talkit.app.global.exception.ExceptionType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        String token = resolveToken(request);
        log.info("1. 요청된 URI: {}",request.getRequestURI());
        log.info("2. 추출된 토큰: {}",token);

        if (token != null){
            if (jwtTokenizer.validateAccessToken(token)) {
                try {
                    Long userId = jwtTokenizer.getUserIdFromAccessToken(token);
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessLogicException(ExceptionType.NOT_FOUND_USER));

                    List<SimpleGrantedAuthority> authorities =List.of(new SimpleGrantedAuthority("ROLE_USER"));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JWT 인증 성공 - userId={}", userId);
                }catch (Exception e) {
                    log.error("4. 인증 과정 중 에러: {}",e.getMessage());
            }
            }  else {
                log.warn("4. 토큰이 유효하지 않음");
            }
        } else {
            log.warn("5. 토큰이 헤더에 없음");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
