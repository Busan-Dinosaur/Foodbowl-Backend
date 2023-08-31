package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
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

    @Nested
    class 닉네임_존재_여부_확인 {

        @Test
        void 존재하는_닉네임이라면_true_응답한다() {
            memberTestPersister.memberBuilder().nickname("hello").save();

            NicknameExistResponse response = memberService.checkNicknameExist("hello");

            assertThat(response.isExist()).isTrue();
        }

        @Test
        void 존재하지_않는_닉네임이라면_false_응답한다() {
            NicknameExistResponse response = memberService.checkNicknameExist("hello");

            assertThat(response.isExist()).isFalse();
        }
    }

    @Nested
    class 프로필_정보_수정 {

        @Test
        void 유효한_요청이라면_프로필_정보를_수정한다() {
            Member member = memberTestPersister.memberBuilder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            memberService.updateProfile(updateProfileRequest, member);

            assertSoftly(softly -> {
                softly.assertThat(member.getNickname()).isEqualTo("hello");
                softly.assertThat(member.getIntroduction()).isEqualTo("friend");
            });
        }

        @Test
        void 변경_닉네임이_현재_닉네임이라면_프로필_정보를_수정한다() {
            Member member = memberTestPersister.memberBuilder().nickname("hello").save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            memberService.updateProfile(updateProfileRequest, member);

            assertSoftly(softly -> {
                softly.assertThat(member.getNickname()).isEqualTo("hello");
                softly.assertThat(member.getIntroduction()).isEqualTo("friend");
            });
        }

        @Test
        void 유효하지_않은_닉네임이라면_예외를_던진다() {
            Member member = memberTestPersister.memberBuilder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("하 이", "friend");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, member))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("한글, 영어, 숫자로 구성된 1글자 이상, 10글자 이하의 닉네임이 아닙니다.");
        }

        @Test
        void 유효하지_않은_한_줄_소개라면_예외를_던진다() {
            Member member = memberTestPersister.memberBuilder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "  ");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, member))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("공백만으로 이루어지지 않은 1글자 이상, 100글자 이하의 한 줄 소개가 아닙니다.");
        }

        @Test
        void 변경_닉네임이_현재_닉네임이_아니고_존재하는_닉네임이라면_예외를_던진다() {
            memberTestPersister.memberBuilder().nickname("hello").save();
            Member member = memberTestPersister.memberBuilder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 닉네임입니다.");
        }
    }
}
