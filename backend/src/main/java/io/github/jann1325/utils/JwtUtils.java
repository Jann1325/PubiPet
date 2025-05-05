package io.github.jann1325.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {


    private static String jwtSecret;
    private static Key key;
    private static final Long EXPIRATION_TIME = 43200000L; // 12 小時

    @Value("${JWT_SECRET}")
    public void setJwtSecret(String secret) {
        jwtSecret = secret;
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // 產生 Token
    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(String.valueOf(claims.get("id"))) // 把 id 設到 subject
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 Token
    public static Claims parseJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 提取用戶 id（從 claims 裡）
    public static Integer extractUserId(String token) {
        return parseJwt(token).get("id", Integer.class);
    }

    // 檢查 Token 是否過期
    public static boolean isTokenExpired(String token) {
        return parseJwt(token).getExpiration().before(new Date());
    }

    // 檢查 Token 是否有效
    public static boolean validateToken(String token, Integer userId) {
        Integer extractedUserId = extractUserId(token);
        return (extractedUserId != null && extractedUserId.equals(userId) && !isTokenExpired(token));
    }
}
