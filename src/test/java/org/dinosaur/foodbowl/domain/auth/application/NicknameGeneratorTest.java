package org.dinosaur.foodbowl.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NicknameGeneratorTest extends IntegrationTest {

    @Autowired
    private NicknameGenerator nicknameGenerator;

    @Test
    void 랜덤_10글자_닉네임을_생성한다() {
        String nickname = nicknameGenerator.generate();

        assertThat(nickname.length()).isEqualTo(10);
    }
}
