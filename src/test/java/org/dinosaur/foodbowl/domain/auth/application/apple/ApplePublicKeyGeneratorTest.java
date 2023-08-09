package org.dinosaur.foodbowl.domain.auth.application.apple;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dinosaur.foodbowl.domain.auth.application.dto.ApplePublicKey;
import org.dinosaur.foodbowl.domain.auth.application.dto.ApplePublicKeys;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApplePublicKeyGeneratorTest {

    private final static ApplePublicKeyGenerator applePublicKeyGenerator = new ApplePublicKeyGenerator();

    @Test
    void 헤더정보를_바탕으로_애플_퍼블릭키를_생성한다() {
        Map<String, String> headers = new HashMap<>();
        headers.put("kid", "YuyXoY");
        headers.put("alg", "RS256");
        ApplePublicKey applePublicKey = new ApplePublicKey(
                "rsa",
                "YuyXoY",
                "sig",
                "RS256",
                "1JiU4l3YCeT4o0gVmxGTEK1IXR-Ghdg5Bzka12tzmtdCxU00ChH66aV-4HRBjF1t95IsaeHeDFRgmF0lJbTDTqa6_VZo2hc0zTiUAsGLacN6slePvDcR1IMucQGtPP5tGhIbU-HKabsKOFdD4VQ5PCXifjpN9R-1qOR571BxCAl4u1kUUIePAAJcBcqGRFSI_I1j_jbN3gflK_8ZNmgnPrXA0kZXzj1I7ZHgekGbZoxmDrzYm2zmja1MsE5A_JX7itBYnlR41LOtvLRCNtw7K3EFlbfB6hkPL-Swk5XNGbWZdTROmaTNzJhV-lWT0gGm6V1qWAK2qOZoIDa_3Ud0Gw",
                "AQAB"
        );
        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(applePublicKey));

        PublicKey publicKey = applePublicKeyGenerator.generate(headers, applePublicKeys);

        assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");
    }
}
