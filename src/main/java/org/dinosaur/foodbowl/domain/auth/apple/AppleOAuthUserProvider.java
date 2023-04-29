package org.dinosaur.foodbowl.domain.auth.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.dto.response.ApplePlatformUserResponseDto;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.APPLE_INVALID_CLAIMS;

@RequiredArgsConstructor
@Component
public class AppleOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public ApplePlatformUserResponseDto extractApplePlatformUser(String appleToken) {
        Map<String, String> headers = appleJwtParser.extractHeaders(appleToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.extractClaims(appleToken, publicKey);
        if (!appleClaimsValidator.isValid(claims)) {
            throw new FoodbowlException(APPLE_INVALID_CLAIMS);
        }

        return new ApplePlatformUserResponseDto(claims.getSubject(), claims.get("email", String.class));
    }
}
