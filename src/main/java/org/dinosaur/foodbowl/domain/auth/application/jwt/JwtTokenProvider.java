package org.dinosaur.foodbowl.domain.auth.application.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.dinosaur.foodbowl.domain.auth.exception.AuthExceptionType;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.global.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessExpireTime;
    private final long refreshExpireTime;
    private final JwtParser jwtParser;

    public JwtTokenProvider(
            @Value("${jwt.secret_key}") String secretKey,
            @Value("${jwt.access_expire_time}") long accessExpireTime,
            @Value("${jwt.refresh_expire_time}") long refreshExpireTime
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpireTime = accessExpireTime;
        this.refreshExpireTime = refreshExpireTime;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String createAccessToken(Long userId, RoleType... roleTypes) {
        String[] roleNames = Arrays.stream(roleTypes)
                .map(RoleType::name)
                .toArray(String[]::new);
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put(JwtConstant.CLAMS_ROLES.getName(), String.join(JwtConstant.DELIMITER.getName(), roleNames));

        Date now = new Date();
        Date validDate = new Date(now.getTime() + accessExpireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

        Date now = new Date();
        Date validDate = new Date(now.getTime() + refreshExpireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractSubject(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (MalformedJwtException e) {
            throw new AuthenticationException(AuthExceptionType.MALFORMED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException(AuthExceptionType.UNSUPPORTED_JWT);
        } catch (SignatureException e) {
            throw new AuthenticationException(AuthExceptionType.SIGNATURE_JWT);
        } catch (Exception e) {
            throw new AuthenticationException(AuthExceptionType.UNKNOWN_JWT);
        }
    }

    public Optional<Claims> extractClaims(String token) {
        try {
            Claims claims = extractValidClaims(token);
            return Optional.of(claims);
        } catch (AuthenticationException e) {
            return Optional.empty();
        }
    }

    public Claims extractValidClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(AuthExceptionType.EXPIRED_JWT);
        } catch (MalformedJwtException e) {
            throw new AuthenticationException(AuthExceptionType.MALFORMED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException(AuthExceptionType.UNSUPPORTED_JWT);
        } catch (SignatureException e) {
            throw new AuthenticationException(AuthExceptionType.SIGNATURE_JWT);
        } catch (Exception e) {
            throw new AuthenticationException(AuthExceptionType.UNKNOWN_JWT);
        }
    }

    public long getValidRefreshMilliSecond() {
        return refreshExpireTime;
    }
}
