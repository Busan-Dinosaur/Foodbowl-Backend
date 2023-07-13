package org.dinosaur.foodbowl.domain.auth.apple;

import static org.dinosaur.foodbowl.exception.ErrorStatus.APPLE_INVALID_TOKEN;
import static org.dinosaur.foodbowl.exception.ErrorStatus.JWT_EXPIRED;
import static org.dinosaur.foodbowl.exception.ErrorStatus.JWT_MALFORMED;
import static org.dinosaur.foodbowl.exception.ErrorStatus.JWT_UNKNOWN;
import static org.dinosaur.foodbowl.exception.ErrorStatus.JWT_UNSUPPORTED;
import static org.dinosaur.foodbowl.exception.ErrorStatus.JWT_WRONG_SIGNATURE;

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
import org.dinosaur.foodbowl.exception.FoodbowlException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppleJwtParser {

    private static final String APPLE_TOKEN_VALUE_DELIMITER = "\\.";
    private static final int HEADER_INDEX = 0;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> extractHeaders(String appleToken) {
        try {
            String encodedHeader = appleToken.split(APPLE_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            return objectMapper.readValue(decodedHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new FoodbowlException(APPLE_INVALID_TOKEN);
        }
    }

    public Claims extractClaims(String appleToken, PublicKey publicKey) {
        try {
            JwtParser appleJwtParser = Jwts.parserBuilder().setSigningKey(publicKey).build();
            return appleJwtParser.parseClaimsJws(appleToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new FoodbowlException(JWT_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new FoodbowlException(JWT_MALFORMED);
        } catch (UnsupportedJwtException e) {
            throw new FoodbowlException(JWT_UNSUPPORTED);
        } catch (SignatureException e) {
            throw new FoodbowlException(JWT_WRONG_SIGNATURE);
        } catch (Exception e) {
            throw new FoodbowlException(JWT_UNKNOWN);
        }
    }
}
