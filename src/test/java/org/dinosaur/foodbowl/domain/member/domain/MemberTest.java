package org.dinosaur.foodbowl.domain.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.dinosaur.foodbowl.domain.member.domain.vo.Introduction;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "a@", "안 녕", " 안녕", "안녕 ", "가나다라마바사아자차카"})
    void 한글_영어_숫자로_구성된_1글자_이상_10글자_이하의_닉네임이_아니라면_예외를_던진다(String nickname) {
        assertThatThrownBy(
                () -> Member.builder()
                        .socialType(SocialType.APPLE)
                        .socialId("A1B2C3D4")
                        .email("email@email.com")
                        .nickname(nickname)
                        .introduction("hello")
                        .build()
        )
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("한글, 영어, 숫자로 구성된 1글자 이상, 10글자 이하의 닉네임이 아닙니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "   "})
    void 공백만으로_이루어진_한_줄_소개라면_예외를_던진다(String introduction) {
        assertThatThrownBy(
                () -> Member.builder()
                        .socialType(SocialType.APPLE)
                        .socialId("A1B2C3D4")
                        .email("email@email.com")
                        .nickname("hello")
                        .introduction(introduction)
                        .build()
        )
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("공백만으로 이루어지지 않은 1글자 이상, 100글자 이하의 한 줄 소개가 아닙니다.");
    }

    @Test
    void 총_길이가_100글자_초과의_한_줄_소개라면_예외를_던진다() {
        String introduction = "가".repeat(101);

        assertThatThrownBy(
                () -> Member.builder()
                        .socialType(SocialType.APPLE)
                        .socialId("A1B2C3D4")
                        .email("email@email.com")
                        .nickname("hello")
                        .introduction(introduction)
                        .build()
        )
                .isInstanceOf(InvalidArgumentException.class)
                .hasMessage("공백만으로 이루어지지 않은 1글자 이상, 100글자 이하의 한 줄 소개가 아닙니다.");
    }

    @Test
    void 프로필_정보를_수정한다() {
        Member member = Member.builder()
                .socialType(SocialType.APPLE)
                .socialId("A1B2C3D4")
                .email("email@email.com")
                .nickname("nickname")
                .introduction("introduction")
                .build();
        Nickname nickname = new Nickname("NewName");
        Introduction introduction = new Introduction("NewIntroduction");

        member.updateProfile(nickname, introduction);

        assertSoftly(softly -> {
            softly.assertThat(member.getNickname()).isEqualTo("NewName");
            softly.assertThat(member.getIntroduction()).isEqualTo("NewIntroduction");
        });
    }

    @Nested
    class 현재_닉네임_확인 {

        @Test
        void 닉네임이_현재_닉네임이라면_true_반환한다() {
            Member member = Member.builder()
                    .socialType(SocialType.APPLE)
                    .socialId("A1B2C3D4")
                    .email("email@email.com")
                    .nickname("nickname")
                    .introduction("introduction")
                    .build();
            Nickname nickname = new Nickname("nickname");

            boolean result = member.hasNickname(nickname);

            assertThat(result).isTrue();
        }

        @Test
        void 닉네임이_현재_닉네임이_아니라면_false_반환한다() {
            Member member = Member.builder()
                    .socialType(SocialType.APPLE)
                    .socialId("A1B2C3D4")
                    .email("email@email.com")
                    .nickname("nickname")
                    .introduction("introduction")
                    .build();
            Nickname nickname = new Nickname("hello");

            boolean result = member.hasNickname(nickname);

            assertThat(result).isFalse();
        }
    }
}
