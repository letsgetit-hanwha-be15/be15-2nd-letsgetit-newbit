package com.newbit.newbituserservice.security;

import com.newbit.newbituserservice.common.exception.BusinessException;
import com.newbit.newbituserservice.common.exception.ErrorCode;
import com.newbit.newbituserservice.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // access token 생성 메소드
    public String createToken(User user, Long mentorId) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim("authority", user.getAuthority().name())
                .claim("nickname", user.getNickname())
                .claim("point", user.getPoint())
                .claim("diamond", user.getDiamond())
                .claim("mentorId", mentorId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey)
                .compact();
    }


    // refresh token 생성 메소드
    public String createRefreshToken(User user, Long mentorId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpiration);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim("authority", user.getAuthority().name())
                .claim("nickname", user.getNickname())
                .claim("point", user.getPoint())
                .claim("diamond", user.getDiamond())
                .claim("mentorId", mentorId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public long getRefreshExpiration() {
        return jwtRefreshExpiration;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new BusinessException(ErrorCode.JWT_INVALID);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(ErrorCode.JWT_UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.JWT_CLAIMS_EMPTY);
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
