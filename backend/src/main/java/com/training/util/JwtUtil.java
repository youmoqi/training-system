package com.training.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成包含用户信息的JWT token
     * @param username 用户名
     * @param userId 用户ID
     * @return JWT token
     */
    public String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * 生成只包含用户名的JWT token
     * @param username 用户名
     * @return JWT token
     */
    public String generateToken(String username) {
        return generateToken(username, null);
    }

    /**
     * 从token中获取用户名
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
            // 从claims中获取用户名
            String username = claims.get("username", String.class);
            if (username != null && !username.isEmpty()) {
                return username;
            }
            throw new RuntimeException("Username not found in token claims");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * 从token中获取用户ID
     * @param token JWT token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            // 尝试从claims中获取userId
            Object userIdObj = claims.get("userId");
            if (userIdObj != null) {
                if (userIdObj instanceof Integer) {
                    return ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    return (Long) userIdObj;
                } else if (userIdObj instanceof String) {
                    return Long.parseLong((String) userIdObj);
                }
            }

            // 如果claims中没有userId，尝试从subject解析
            String subject = claims.getSubject();
            if (subject != null && subject.matches("\\d+")) {
                return Long.parseLong(subject);
            }

            throw new RuntimeException("User ID not found in token");
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * 验证JWT token是否有效
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            // 无效的JWT签名
            return false;
        } catch (MalformedJwtException ex) {
            // 无效的JWT令牌
            return false;
        } catch (ExpiredJwtException ex) {
            // 过期的JWT令牌
            return false;
        } catch (UnsupportedJwtException ex) {
            // 不支持的JWT令牌
            return false;
        } catch (IllegalArgumentException ex) {
            // JWT声明字符串为空
            return false;
        }
    }

    /**
     * 从token中获取所有claims
     * @param token JWT token
     * @return Claims对象
     */
    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * 检查token是否过期
     * @param token JWT token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取token的过期时间
     * @param token JWT token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * 获取token的签发时间
     * @param token JWT token
     * @return 签发时间
     */
    public Date getIssuedAtDateFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getIssuedAt();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * 从Authorization header中提取token
     * @param authorizationHeader Authorization header字符串
     * @return 纯token字符串
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new RuntimeException("Authorization header is missing");
        }
        if (authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return token;
        }
        return authorizationHeader;
    }

    /**
     * 从Authorization header中获取用户名
     * @param authorizationHeader Authorization header字符串
     * @return 用户名
     */
    public String getUsernameFromHeader(String authorizationHeader) {
        String token = extractTokenFromHeader(authorizationHeader);
        return getUsernameFromToken(token);
    }

    /**
     * 从Authorization header中获取用户ID
     * @param authorizationHeader Authorization header字符串
     * @return 用户ID
     */
    public Long getUserIdFromHeader(String authorizationHeader) {
        String token = extractTokenFromHeader(authorizationHeader);
        return getUserIdFromToken(token);
    }

    /**
     * 验证Authorization header中的token是否有效
     * @param authorizationHeader Authorization header字符串
     * @return 是否有效
     */
    public boolean validateHeader(String authorizationHeader) {
        try {
            String token = extractTokenFromHeader(authorizationHeader);
            return validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
}
