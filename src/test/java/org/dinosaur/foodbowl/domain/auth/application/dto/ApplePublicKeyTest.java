package org.dinosaur.foodbowl.domain.auth.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApplePublicKeyTest {

    private static final ApplePublicKey applePublicKey = new ApplePublicKey(
            "kty",
            "kid",
            "use",
            "alg",
            "n",
            "e"
    );

    @Nested
    class 알고리즘_검증 {

        @Test
        void 알고리즘이_같다면_true_반환한다() {
            String alg = "alg";

            boolean result = applePublicKey.isSameAlg(alg);

            assertThat(result).isTrue();
        }

        @Test
        void 알고리즘이_다르다면_false_반환한다() {
            String alg = "gla";

            boolean result = applePublicKey.isSameAlg(alg);

            assertThat(result).isFalse();
        }
    }

    @Nested
    class 키아이디_검증 {

        @Test
        void 키아이디가_같다면_true_반환한다() {
            String kid = "kid";

            boolean result = applePublicKey.isSameKid(kid);

            assertThat(result).isTrue();
        }

        @Test
        void 키아이디가_다르다면_false_반환한다() {
            String kid = "dik";

            boolean result = applePublicKey.isSameKid(kid);

            assertThat(result).isFalse();
        }
    }
}
