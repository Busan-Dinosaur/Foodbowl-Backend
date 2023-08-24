package org.dinosaur.foodbowl.domain.blame.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BlameTest {

    @Test
    void 신고를_생성한다() {
        Member member = Member.builder()
                .socialType(SocialType.APPLE)
                .socialId("1")
                .email("email@email.com")
                .nickname("hello")
                .introduction("hello world")
                .build();
        Blame blame = Blame.builder()
                .member(member)
                .targetId(1L)
                .blameTarget(BlameTarget.MEMBER)
                .build();

        assertThat(blame.getMember()).isEqualTo(member);
    }
}
