package org.dinosaur.foodbowl.domain.auth.application.apple;

import io.jsonwebtoken.Claims;
import java.util.Objects;
import org.dinosaur.foodbowl.global.util.EncryptUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppleClaimsValidator {

    private static final String NONCE_KEY = "nonce";

    private final String iss;
    private final String clientId;
    private final String encryptNonce;

    public AppleClaimsValidator(
            @Value("${oauth.apple.iss}") String iss,
            @Value("${oauth.apple.client_id}") String clientId,
            @Value("${oauth.apple.nonce}") String nonce
    ) {
        this.iss = iss;
        this.clientId = clientId;
        this.encryptNonce = EncryptUtils.encrypt(nonce);
    }

    public boolean isValid(Claims claims) {
        return claims.getIssuer() != null && claims.getIssuer().contains(iss)
                && Objects.equals(claims.getAudience(), clientId)
                && Objects.equals(claims.get(NONCE_KEY, String.class).toLowerCase(), encryptNonce.toLowerCase());
    }
}
