package org.dinosaur.foodbowl.domain.auth.application;

import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NicknameGenerator {

    private static final Random RANDOM = new Random();
    private static final int RANDOM_NICKNAME_LENGTH = 7;
    private static final String PRE_NICKNAME = "풋볼러";
    private static final String SOURCE = "AaBbCc0DdEeFf1GgHhIi2JjKkLl3MmNnOo4PpQq5RrSs6TtUu7VvWw8XxYy9Zz";

    private final MemberRepository memberRepository;

    public String generate() {
        String nickname;
        do {
            nickname = PRE_NICKNAME + generateRandomNickname();
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }

    private String generateRandomNickname() {
        return RANDOM.ints(0, SOURCE.length())
                .limit(RANDOM_NICKNAME_LENGTH)
                .mapToObj(SOURCE::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
