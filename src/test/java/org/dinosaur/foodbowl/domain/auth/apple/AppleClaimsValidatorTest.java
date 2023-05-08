package org.dinosaur.foodbowl.domain.auth.apple;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AppleClaimsValidatorTest {

    private static final String ISS = "iss";
    private static final String CLIENT_ID = "clientID";
    private static final String NONCE = "nonce";
    private static final String NONCE_KEY = "nonce";

    private final AppleClaimsValidator appleClaimsValidator = new AppleClaimsValidator(ISS, CLIENT_ID, NONCE);

    @Test
    @DisplayName("유효한 claims 정보를 가지고 있으면 true 반환한다.")
    void validClaims() {
        Claims claims = Jwts.claims()
                .setIssuer(ISS)
                .setAudience(CLIENT_ID);
        claims.put(NONCE_KEY, NONCE);

        boolean result = appleClaimsValidator.isValid(claims);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "invalidIss,clientID,nonce",
            "iss,invalidClientID,nonce",
            "iss,clientID,invalidNonce"
    })
    @DisplayName("유효하지 않은 claims 정보를 가지고 있으면 false 반환한다.")
    void invalidClaims(String iss, String clientId, String nonce) {
        Claims claims = Jwts.claims()
                .setIssuer(iss)
                .setAudience(clientId);
        claims.put(NONCE_KEY, nonce);

        boolean result = appleClaimsValidator.isValid(claims);

        assertThat(result).isFalse();
    }
}
