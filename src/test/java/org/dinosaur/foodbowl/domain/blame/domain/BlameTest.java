package org.dinosaur.foodbowl.domain.blame.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BlameTest {

    @Nested
    class 신고_생성_시 {

        @Test
        void 신고를_정상적으로_생성한다() {
            Member member = mockingMember();
            Blame blame = Blame.builder()
                    .member(member)
                    .targetId(1L)
                    .blameTarget(BlameTarget.MEMBER)
                    .description("invalid")
                    .build();

            assertThat(blame.getMember()).isEqualTo(member);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void 신고_내용이_공백이거나_존재하지_않으면_예외를_던진다(String description) {
            Member member = mockingMember();

            assertThatThrownBy(() -> Blame.builder()
                    .member(member)
                    .targetId(1L)
                    .blameTarget(BlameTarget.MEMBER)
                    .description(description)
                    .build()
            )
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("신고 내용이 공백이거나 존재하지 않습니다.");
        }

        @Test
        void 신고_내용이_최대_길이를_초과하면_얘외를_던진다() {
            Member member = mockingMember();
            String description = "a".repeat(256);

            assertThatThrownBy(() -> Blame.builder()
                    .member(member)
                    .targetId(1L)
                    .blameTarget(BlameTarget.MEMBER)
                    .description(description)
                    .build()
            )
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("신고 내용의 최대 길이를 초과하였습니다.");
        }
    }

    private Member mockingMember() {
        return Member.builder()
                .socialType(SocialType.APPLE)
                .socialId("1")
                .email("email@email.com")
                .nickname("hello")
                .introduction("hello world")
                .build();
    }
}
