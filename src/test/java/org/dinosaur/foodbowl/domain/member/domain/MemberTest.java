package org.dinosaur.foodbowl.domain.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.dinosaur.foodbowl.domain.member.domain.vo.Introduction;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "a@", "안 녕", "hi2", "가나다라마바사아자차카"})
    void 한글_영어로_구성된_1글자_이상_10글자_이하의_닉네임이_아니라면_예외를_던진다(String nickname) {
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
                .hasMessage("한글, 영어로 구성된 1글자 이상, 10글자 이하의 닉네임이 아닙니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "a@", "안 녕", "hi2"})
    void 한글_영어로_구성된_한_줄_소개가_아니라면_예외를_던진다(String introduction) {
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
                .hasMessage("한글, 영어로 구성된 1글자 이상, 100글자 이하의 한 줄 소개가 아닙니다.");
    }

    @Test
    void 한글_영어로_구성된_100글자_이하의_한_줄_소개가_아니라면_예외를_던진다() {
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
                .hasMessage("한글, 영어로 구성된 1글자 이상, 100글자 이하의 한 줄 소개가 아닙니다.");
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
}
