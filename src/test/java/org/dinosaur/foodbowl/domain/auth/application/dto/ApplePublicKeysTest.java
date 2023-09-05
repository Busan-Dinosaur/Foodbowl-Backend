package org.dinosaur.foodbowl.domain.auth.application.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApplePublicKeysTest {

    private static ApplePublicKey applePublicKeyA = new ApplePublicKey("ktyA", "kidA", "useA", "algA", "nA", "eA");
    private static ApplePublicKey applePublicKeyB = new ApplePublicKey("ktyB", "kidB", "useB", "algB", "nB", "eB");
    private static ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(applePublicKeyA, applePublicKeyB));

    @Nested
    class 애플_퍼블릭_키_매칭_시 {

        @Test
        void 알고리즘과_키아이디가_일치하는_애플_퍼블릭키가_존재하면_해당_키를_반환한다() {
            String alg = "algA";
            String kid = "kidA";

            ApplePublicKey applePublicKey = applePublicKeys.getMatchingKey(alg, kid);

            assertThat(applePublicKey).isEqualTo(applePublicKeyA);
        }

        @Test
        void 알고리즘과_키아이디가_일치하는_애플_퍼블릭키가_존재하지_않으면_예외를_던진다() {
            String alg = "algA";
            String kid = "kidB";

            assertThatThrownBy(() -> applePublicKeys.getMatchingKey(alg, kid))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("지원하지 않는 토큰입니다.");
        }
    }
}
