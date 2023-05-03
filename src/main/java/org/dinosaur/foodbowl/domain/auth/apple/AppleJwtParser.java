package org.dinosaur.foodbowl.domain.auth.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.*;

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
