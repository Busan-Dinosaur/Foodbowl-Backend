package org.dinosaur.foodbowl.domain.auth.application.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.exception.AuthExceptionType;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppleTokenParser {

    private static final String TOKEN_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;

    private final ObjectMapper objectMapper;

    public Map<String, String> extractHeaders(String appleToken) {
        try {
            String encodedHeader = appleToken.split(TOKEN_DELIMITER)[HEADER_INDEX];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return objectMapper.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new BadRequestException(AuthExceptionType.INVALID_HEADER_JWT, e);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(AuthExceptionType.INVALID_BASE64_DECODE, e);
        }
    }

    public Claims extractClaims(String appleToken, PublicKey publicKey) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build();
            return jwtParser.parseClaimsJws(appleToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new BadRequestException(AuthExceptionType.EXPIRED_JWT, e);
        } catch (UnsupportedJwtException e) {
            throw new BadRequestException(AuthExceptionType.UNSUPPORTED_JWT, e);
        } catch (MalformedJwtException e) {
            throw new BadRequestException(AuthExceptionType.MALFORMED_JWT, e);
        } catch (SignatureException e) {
            throw new BadRequestException(AuthExceptionType.SIGNATURE_JWT, e);
        } catch (Exception e) {
            throw new BadRequestException(AuthExceptionType.UNKNOWN_JWT, e);
        }
    }
}
