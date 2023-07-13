package org.dinosaur.foodbowl.domain.auth.apple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dinosaur.foodbowl.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PublicKeyGeneratorTest {

    private final PublicKeyGenerator publicKeyGenerator = new PublicKeyGenerator();

    @Test
    @DisplayName("헤더 정보를 바탕으로 Public Key를 생성한다.")
    void generatePublicKey() {
        Map<String, String> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("kid", "YuyXoY");
        ApplePublicKey applePublicKey = new ApplePublicKey(
                "rsa",
                "YuyXoY",
                "sig",
                "RS256",
                "1JiU4l3YCeT4o0gVmxGTEK1IXR-Ghdg5Bzka12tzmtdCxU00ChH66aV-4HRBjF1t95IsaeHeDFRgmF0lJbTDTqa6_VZo2hc0zTiUAsGLacN6slePvDcR1IMucQGtPP5tGhIbU-HKabsKOFdD4VQ5PCXifjpN9R-1qOR571BxCAl4u1kUUIePAAJcBcqGRFSI_I1j_jbN3gflK_8ZNmgnPrXA0kZXzj1I7ZHgekGbZoxmDrzYm2zmja1MsE5A_JX7itBYnlR41LOtvLRCNtw7K3EFlbfB6hkPL-Swk5XNGbWZdTROmaTNzJhV-lWT0gGm6V1qWAK2qOZoIDa_3Ud0Gw",
                "AQAB"
        );
        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(applePublicKey));

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");
    }

    @Test
    @DisplayName("헤더 정보가 올바르지 않으면 예외를 던진다.")
    void generatePublicKeyWithInvalidHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("alg", "alg");
        headers.put("kid", "kid");
        ApplePublicKey applePublicKey = new ApplePublicKey(
                "kty",
                "kid",
                "use",
                "alg",
                "1JiU4l3YCeT4o0gVmxGTEK1IXR-Ghdg5Bzka12tzmtdCxU00ChH66aV-4HRBjF1t95IsaeHeDFRgmF0lJbTDTqa6_VZo2hc0zTiUAsGLacN6slePvDcR1IMucQGtPP5tGhIbU-HKabsKOFdD4VQ5PCXifjpN9R-1qOR571BxCAl4u1kUUIePAAJcBcqGRFSI_I1j_jbN3gflK_8ZNmgnPrXA0kZXzj1I7ZHgekGbZoxmDrzYm2zmja1MsE5A_JX7itBYnlR41LOtvLRCNtw7K3EFlbfB6hkPL-Swk5XNGbWZdTROmaTNzJhV-lWT0gGm6V1qWAK2qOZoIDa_3Ud0Gw",
                "AQAB"
        );
        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(applePublicKey));

        assertThatThrownBy(() -> publicKeyGenerator.generatePublicKey(headers, applePublicKeys))
                .isInstanceOf(FoodbowlException.class)
                .hasMessage("애플 OAuth 퍼블릭 키 생성 중 문제가 발생하였습니다.");
    }
}
