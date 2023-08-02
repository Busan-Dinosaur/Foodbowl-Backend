package org.dinosaur.foodbowl.domain.auth.application.apple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Objects;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.auth.application.dto.ApplePublicKey;
import org.dinosaur.foodbowl.domain.auth.application.dto.ApplePublicKeys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class AppleClientTest extends IntegrationTest {

    @Autowired
    private AppleClient appleClient;

    @Test
    void 애플_서버와_통신하여_애플_퍼블릭키_목록을_구성한다() {
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        boolean result = applePublicKeys.keys()
                .stream()
                .allMatch(this::isValidKey);

        assertAll(
                () -> assertThat(applePublicKeys.keys().size()).isGreaterThan(0),
                () -> assertThat(result).isTrue()
        );
    }

    private boolean isValidKey(ApplePublicKey applePublicKey) {
        return Objects.nonNull(applePublicKey.kty()) && Objects.nonNull(applePublicKey.kid())
                && Objects.nonNull(applePublicKey.use()) && Objects.nonNull(applePublicKey.alg())
                && Objects.nonNull(applePublicKey.n()) && Objects.nonNull(applePublicKey.e());
    }
}
