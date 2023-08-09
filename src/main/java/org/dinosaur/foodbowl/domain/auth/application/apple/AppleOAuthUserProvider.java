package org.dinosaur.foodbowl.domain.auth.application.apple;

import io.jsonwebtoken.Claims;
import java.security.PublicKey;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.auth.application.dto.ApplePublicKeys;
import org.dinosaur.foodbowl.domain.auth.application.dto.AppleUser;
import org.dinosaur.foodbowl.domain.auth.exception.AuthExceptionType;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppleOAuthUserProvider {

    private final AppleTokenParser appleTokenParser;
    private final AppleClient appleClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public AppleUser extractPlatformUser(String appleToken) {
        Map<String, String> headers = appleTokenParser.extractHeaders(appleToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = applePublicKeyGenerator.generate(headers, applePublicKeys);
        Claims claims = appleTokenParser.extractClaims(appleToken, publicKey);
        validateClaims(claims);
        return new AppleUser(SocialType.APPLE, claims.getSubject(), claims.get("email", String.class));
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new BadRequestException(AuthExceptionType.INVALID_PAYLOAD_JWT);
        }
    }
}
