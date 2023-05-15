package org.dinosaur.foodbowl.domain.auth.apple;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import org.dinosaur.foodbowl.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AppleClientTest extends IntegrationTest {

    @Autowired
    private AppleClient appleClient;

    @Test
    @DisplayName("Apple 서버와 통신하여 Apple Public Keys를 구성한다.")
    void getApplePublicKeys() {
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        List<ApplePublicKey> keys = applePublicKeys.getKeys();

        boolean result = keys.stream()
                .allMatch(this::isAllNotNull);

        assertThat(result).isTrue();
    }

    private boolean isAllNotNull(ApplePublicKey applePublicKey) {
        return Objects.nonNull(applePublicKey.getKty()) && Objects.nonNull(applePublicKey.getKid())
                && Objects.nonNull(applePublicKey.getUse()) && Objects.nonNull(applePublicKey.getAlg())
                && Objects.nonNull(applePublicKey.getN()) && Objects.nonNull(applePublicKey.getE());
    }
}
