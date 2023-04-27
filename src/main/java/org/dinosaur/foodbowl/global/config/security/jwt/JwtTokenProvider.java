package org.dinosaur.foodbowl.global.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import static org.dinosaur.foodbowl.global.config.security.jwt.JwtConstant.CLAMS_ROLES;
import static org.dinosaur.foodbowl.global.config.security.jwt.JwtConstant.DELIMITER;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validAccessMilliSecond;
    private final long validRefreshMilliSecond;
    private final JwtParser jwtParser;

    public JwtTokenProvider(
            @Value("${spring.jwt.token.secret-key}") String secretKey,
            @Value("${spring.jwt.token.access-expire-time}") long validAccessMilliSecond,
            @Value("${spring.jwt.token.refresh-expire-time}") long validRefreshMilliSecond
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validAccessMilliSecond = validAccessMilliSecond;
        this.validRefreshMilliSecond = validRefreshMilliSecond;
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String createAccessToken(Long userId, RoleType... roleTypes) {
        String[] roleNames = Arrays.stream(roleTypes)
                .map(RoleType::name)
                .toArray(String[]::new);
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put(CLAMS_ROLES.getName(), String.join(DELIMITER.getName(), roleNames));

        Date now = new Date();
        Date validDate = new Date(now.getTime() + validAccessMilliSecond);

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
        Date validDate = new Date(now.getTime() + validRefreshMilliSecond);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<String> extractSubject(String token) {
        JwtTokenValid jwtTokenValid = validateToken(token);

        if (jwtTokenValid.isValid()) {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return Optional.ofNullable(claims.getSubject());
        }
        return Optional.empty();
    }

    public Optional<Authentication> generateAuth(String token) {
        JwtTokenValid jwtTokenValid = validateToken(token);

        if (jwtTokenValid.isValid()) {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            List<String> roleNames = Arrays.stream(
                    claims.get(CLAMS_ROLES.getName()).toString().split(DELIMITER.getName())
            ).toList();
            UserDetails userDetails = new JwtUser(claims.getSubject(), roleNames);
            return Optional.of(new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()));
        }
        return Optional.empty();
    }

    private JwtTokenValid validateToken(String token) {
        ErrorStatus errorStatus;
        try {
            jwtParser.parseClaimsJws(token).getBody();
            return new JwtTokenValid(true, "", 0);
        } catch (ExpiredJwtException e) {
            errorStatus = ErrorStatus.JWT_EXPIRED;
        } catch (MalformedJwtException e) {
            errorStatus = ErrorStatus.JWT_MALFORMED;
        } catch (UnsupportedJwtException e) {
            errorStatus = ErrorStatus.JWT_UNSUPPORTED;
        } catch (SignatureException e) {
            errorStatus = ErrorStatus.JWT_WRONG_SIGNATURE;
        } catch (Exception e) {
            errorStatus = ErrorStatus.JWT_UNKNOWN;
        }
        return new JwtTokenValid(false, errorStatus.getMessage(), errorStatus.getCode());
    }
}
