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
import org.dinosaur.foodbowl.global.exception.ExceptionType;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final JwtProperty jwtProperty;
    private final JwtParser jwtParser;

    public JwtTokenProvider(JwtProperty jwtProperty) {
        this.key = Keys.hmacShaKeyFor(jwtProperty.secretKey().getBytes(StandardCharsets.UTF_8));
        this.jwtProperty = jwtProperty;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String createAccessToken(Long userId, RoleType... roleTypes) {
        String[] roleNames = Arrays.stream(roleTypes)
                .map(RoleType::name)
                .toArray(String[]::new);
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put(JwtConstant.CLAMS_ROLES.getName(), String.join(JwtConstant.DELIMITER.getName(), roleNames));

        Date now = new Date();
        Date validDate = new Date(now.getTime() + jwtProperty.accessExpireTime());

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
        Date validDate = new Date(now.getTime() + jwtProperty.refreshExpireTime());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<Claims> extractClaims(String token) {
        JwtTokenValid jwtTokenValid = validateToken(token);

        if (jwtTokenValid.isValid()) {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return Optional.of(claims);
        }
        return Optional.empty();
    }

    private JwtTokenValid validateToken(String token) {
        ExceptionType exceptionType;
        try {
            jwtParser.parseClaimsJws(token).getBody();
            return new JwtTokenValid(true, "", "");
        } catch (ExpiredJwtException e) {
            exceptionType = AuthExceptionType.EXPIRED_JWT;
        } catch (MalformedJwtException e) {
            exceptionType = AuthExceptionType.MALFORMED_JWT;
        } catch (UnsupportedJwtException e) {
            exceptionType = AuthExceptionType.UNSUPPORTED_JWT;
        } catch (SignatureException e) {
            exceptionType = AuthExceptionType.SIGNATURE_JWT;
        } catch (Exception e) {
            exceptionType = AuthExceptionType.UNKNOWN_JWT;
        }
        return new JwtTokenValid(false, exceptionType.getErrorCode(), exceptionType.getMessage());
    }

    public long getValidRefreshMilliSecond() {
        return jwtProperty.refreshExpireTime();
    }
}
