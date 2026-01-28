package com.talkit.app.global.security.jwt;

import com.talkit.app.domain.user.repository.UserRepository;
import com.talkit.app.domain.user.service.UserDetailsImpl;
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

        try {
            String token = resolveToken(request);

            // 1. 토큰이 존재하고 유효한 경우에만 인증 프로세스 진행
            if (token != null && jwtTokenizer.validateAccessToken(token)) {

                // 2. 토큰에서 사용자 정보 및 권한 추출
                Long userId = jwtTokenizer.getUserIdFromAccessToken(token);
                String role = "ROLE_USER";

                // 3. DB 조회를 통해 인증 객체 생성
                userRepository.findById(userId).ifPresent(user -> {
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    UserDetailsImpl userDetails = UserDetailsImpl.from(user);

                    // 2. Principal 자리에 userDetails 객체 주입
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        } catch (Exception e) {
        }

        // 4. 다음 필터로 전달 (매우 중요)
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 추출
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 쿠키에서 추출
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
