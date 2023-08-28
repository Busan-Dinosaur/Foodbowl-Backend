package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberServiceTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @Nested
    class 프로필_조회 {

        @Test
        void 나의_프로필이라면_나의_프로필_여부는_true_팔로잉_여부는_false_이다() {
            Member loginMember = memberTestPersister.memberBuilder().save();

            MemberProfileResponse response = memberService.getProfile(loginMember.getId(), loginMember);

            assertSoftly(
                    softly -> {
                        softly.assertThat(response.id()).isEqualTo(loginMember.getId());
                        softly.assertThat(response.nickname()).isEqualTo(loginMember.getNickname());
                        softly.assertThat(response.introduction()).isEqualTo(loginMember.getIntroduction());
                        softly.assertThat(response.isMyProfile()).isTrue();
                        softly.assertThat(response.isFollowing()).isFalse();
                    }
            );
        }

        @Test
        void 나의_프로필이_아니고_팔로잉_중인_회원이라면_나의_프로필_여부는_false_팔로잉_여부는_true_이다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member profileTargetMember = memberTestPersister.memberBuilder().save();
            followTestPersister.builder().following(profileTargetMember).follower(loginMember).save();

            MemberProfileResponse response = memberService.getProfile(profileTargetMember.getId(), loginMember);

            assertSoftly(
                    softly -> {
                        softly.assertThat(response.id()).isEqualTo(profileTargetMember.getId());
                        softly.assertThat(response.nickname()).isEqualTo(profileTargetMember.getNickname());
                        softly.assertThat(response.introduction()).isEqualTo(profileTargetMember.getIntroduction());
                        softly.assertThat(response.isMyProfile()).isFalse();
                        softly.assertThat(response.isFollowing()).isTrue();
                    }
            );
        }

        @Test
        void 나의_프로필이_아니고_팔로잉_중인_회원이_아니라면_나의_프로필_여부는_false_팔로잉_여부는_false_이다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member profileTargetMember = memberTestPersister.memberBuilder().save();

            MemberProfileResponse response = memberService.getProfile(profileTargetMember.getId(), loginMember);

            assertSoftly(
                    softly -> {
                        softly.assertThat(response.id()).isEqualTo(profileTargetMember.getId());
                        softly.assertThat(response.nickname()).isEqualTo(profileTargetMember.getNickname());
                        softly.assertThat(response.introduction()).isEqualTo(profileTargetMember.getIntroduction());
                        softly.assertThat(response.isMyProfile()).isFalse();
                        softly.assertThat(response.isFollowing()).isFalse();
                    }
            );
        }

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> memberService.getProfile(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }
}
