package org.dinosaur.foodbowl.global.config.security.jwt;

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
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validAccessMilliSecond;
    private final long validRefreshMilliSecond;
    private final JwtParser jwtParser;

    public JwtTokenProvider(
            @Value("${jwt.secret_key}") String secretKey,
            @Value("${jwt.access_expire_time}") long validAccessMilliSecond,
            @Value("${jwt.refresh_expire_time}") long validRefreshMilliSecond
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
        claims.put(JwtConstant.CLAMS_ROLES.getName(), String.join(JwtConstant.DELIMITER.getName(), roleNames));

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
                    claims.get(JwtConstant.CLAMS_ROLES.getName()).toString().split(JwtConstant.DELIMITER.getName())
            ).toList();
            UserDetails userDetails = new JwtUser(claims.getSubject(), roleNames);
            return Optional.of(
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                    )
            );
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

    public long getValidRefreshMilliSecond() {
        return validRefreshMilliSecond;
    }
}
