package org.dinosaur.foodbowl.domain.auth.application;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NicknameGenerator {

    private static final int RANDOM_NICKNAME_LENGTH = 7;
    private static final String PRE_NICKNAME = "풋볼러";

    public static String generate() {
        return PRE_NICKNAME + generateRandomNickname();
    }

    private static String generateRandomNickname() {
        return RandomStringUtils.random(RANDOM_NICKNAME_LENGTH, true, false);
    }
}
