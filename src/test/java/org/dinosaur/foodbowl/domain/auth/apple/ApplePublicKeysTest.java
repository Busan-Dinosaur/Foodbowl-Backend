package org.dinosaur.foodbowl.domain.auth.apple;

import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplePublicKeysTest {

    @Test
    @DisplayName("알고리즘과 키 ID를 받아 알맞은 Public Key를 반환한다.")
    void getMatchingKey() {
        ApplePublicKey applePublicKey = new ApplePublicKey("kty", "kid", "use", "alg", "n", "e");

        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(applePublicKey));

        assertThat(applePublicKeys.getMatchingKey("alg", "kid")).isEqualTo(applePublicKey);
    }

    @ParameterizedTest
    @CsvSource(value = {"invalidAlg,kid", "alg,invalidKid"})
    @DisplayName("알고리즘과 키 ID 매칭되는 Public Key 존재하지 않으면 예외를 던진다.")
    void getMatchingKey(String alg, String kid) {
        ApplePublicKey applePublicKey = new ApplePublicKey("kty", "kid", "use", "alg", "n", "e");

        ApplePublicKeys applePublicKeys = new ApplePublicKeys(List.of(applePublicKey));

        assertThatThrownBy(() -> applePublicKeys.getMatchingKey(alg, kid))
                .isInstanceOf(FoodbowlException.class)
                .hasMessage("올바르지 않은 애플 OAuth 토큰 헤더 정보입니다.");
    }
}
