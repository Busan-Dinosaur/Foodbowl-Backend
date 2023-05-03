package org.dinosaur.foodbowl.domain.auth.apple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplePublicKeyTest {

    private static final String KTY = "kty";
    private static final String KID = "kid";
    private static final String USE = "use";
    private static final String ALG = "alg";
    private static final String N = "n";
    private static final String E = "e";

    private final ApplePublicKey applePublicKey = new ApplePublicKey(KTY, KID, USE, ALG, N, E);

    @Test
    @DisplayName("같은 알고리즘이라면 true 반환한다.")
    void isSameAlg() {
        assertThat(applePublicKey.isSameAlg("alg")).isTrue();
    }

    @Test
    @DisplayName("다른 알고리즘이라면 false 반환한다.")
    void isDifferentAlg() {
        assertThat(applePublicKey.isSameAlg("invalidAlg")).isFalse();
    }

    @Test
    @DisplayName("같은 키 ID 라면 true 반환한다.")
    void isSameKid() {
        assertThat(applePublicKey.isSameKid("kid")).isTrue();
    }

    @Test
    @DisplayName("다른 키 ID 라면 false 반환한다.")
    void isDifferentKid() {
        assertThat(applePublicKey.isSameKid("invalidKid")).isFalse();
    }
}
