package com.talkit.app.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenizer {

    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 3;  // 3시간
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;  // 7일

    private Key accessKey;
    private Key refreshKey;


    @Value("${JWT_SECRET_KEY}")
    private String accessSecret;

    @Value("${JWT_REFRESH_KEY}")
    private String refreshSecret;

    @PostConstruct
    public void init() {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long id, String email, String name) {
        return createToken(id, email, name, ACCESS_TOKEN_EXPIRE_TIME, accessKey);
    }

    public String createRefreshToken(Long id, String email, String name) {
        return createToken(id, email, name,REFRESH_TOKEN_EXPIRE_TIME, refreshKey);
    }

    private String createToken(Long id, String email, String name, long expireTime, Key key) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", id);
        claims.put("username", name);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireTime);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateAccessToken(String token) {
        return validate(token, accessKey);
    }

    public boolean validateRefreshToken(String token) {
        return validate(token, refreshKey);
    }

    private boolean validate(String token, Key key) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("토큰 유효성 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public Claims parseAccessToken(String token) {
        return parse(token, accessKey);
    }

    public Claims parseRefreshToken(String token) {
        return parse(token, refreshKey);
    }

    private Claims parse(String token, Key key) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public Long getUserIdFromAccessToken(String token) {
        Claims claims = parseAccessToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    public String getEmailFromAccessToken(String token) {
        Claims claims = parseAccessToken(token); // accessKey를 사용하는 기존 메서드 활용
        return claims.getSubject();
    }

    // JwtTokenizer.java에 추가
    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = parseRefreshToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    // 이메일 추출도 Refresh용으로 하나 더 (선택)
    public String getEmailFromRefreshToken(String token) {
        return parseRefreshToken(token).getSubject();
    }

}
