package org.dinosaur.foodbowl.domain.auth.application;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NicknameGenerator {

    private static final int RANDOM_NICKNAME_LENGTH = 7;
    private static final String PRE_NICKNAME = "풋볼러";

    private final MemberRepository memberRepository;

    public String generate() {
        String nickname;
        do {
            nickname = PRE_NICKNAME + generateRandomNickname();
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }

    private String generateRandomNickname() {
        return RandomStringUtils.random(RANDOM_NICKNAME_LENGTH, true, true);
    }
}
