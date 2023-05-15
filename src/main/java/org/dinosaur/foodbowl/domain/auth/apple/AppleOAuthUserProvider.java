package org.dinosaur.foodbowl.domain.auth.apple;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.APPLE_INVALID_CLAIMS;

import io.jsonwebtoken.Claims;
import java.security.PublicKey;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.dto.response.ApplePlatformUserResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppleOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public ApplePlatformUserResponse extractApplePlatformUser(String appleToken) {
        Map<String, String> headers = appleJwtParser.extractHeaders(appleToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.extractClaims(appleToken, publicKey);
        if (!appleClaimsValidator.isValid(claims)) {
            throw new FoodbowlException(APPLE_INVALID_CLAIMS);
        }

        return new ApplePlatformUserResponse(claims.getSubject(), claims.get("email", String.class));
    }
}
