package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.domain.member.persistence.MemberThumbnailRepository;
import org.dinosaur.foodbowl.domain.photo.application.ThumbnailService;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class MemberServiceTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private MemberThumbnailRepository memberThumbnailRepository;

    @Nested
    class 프로필_조회_시 {

        @Test
        void 나의_프로필이라면_나의_프로필_여부는_true_팔로잉_여부는_false_이다() {
            Member loginMember = memberTestPersister.builder().save();

            MemberProfileResponse response = memberService.getProfile(loginMember.getId(), loginMember);

            assertSoftly(softly -> {
                softly.assertThat(response.id()).isEqualTo(loginMember.getId());
                softly.assertThat(response.nickname()).isEqualTo(loginMember.getNickname());
                softly.assertThat(response.introduction()).isEqualTo(loginMember.getIntroduction());
                softly.assertThat(response.isMyProfile()).isTrue();
                softly.assertThat(response.isFollowing()).isFalse();
            });
        }

        @Test
        void 나의_프로필이_아니고_팔로잉_중인_회원이라면_나의_프로필_여부는_false_팔로잉_여부는_true_이다() {
            Member loginMember = memberTestPersister.builder().save();
            Member profileTargetMember = memberTestPersister.builder().save();
            followTestPersister.builder().following(profileTargetMember).follower(loginMember).save();

            MemberProfileResponse response = memberService.getProfile(profileTargetMember.getId(), loginMember);

            assertSoftly(softly -> {
                softly.assertThat(response.id()).isEqualTo(profileTargetMember.getId());
                softly.assertThat(response.nickname()).isEqualTo(profileTargetMember.getNickname());
                softly.assertThat(response.introduction()).isEqualTo(profileTargetMember.getIntroduction());
                softly.assertThat(response.isMyProfile()).isFalse();
                softly.assertThat(response.isFollowing()).isTrue();
            });
        }

        @Test
        void 나의_프로필이_아니고_팔로잉_중인_회원이_아니라면_나의_프로필_여부는_false_팔로잉_여부는_false_이다() {
            Member loginMember = memberTestPersister.builder().save();
            Member profileTargetMember = memberTestPersister.builder().save();

            MemberProfileResponse response = memberService.getProfile(profileTargetMember.getId(), loginMember);

            assertSoftly(softly -> {
                softly.assertThat(response.id()).isEqualTo(profileTargetMember.getId());
                softly.assertThat(response.nickname()).isEqualTo(profileTargetMember.getNickname());
                softly.assertThat(response.introduction()).isEqualTo(profileTargetMember.getIntroduction());
                softly.assertThat(response.isMyProfile()).isFalse();
                softly.assertThat(response.isFollowing()).isFalse();
            });
        }

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> memberService.getProfile(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Test
    void 나의_프로필을_조회한다() {
        Member loginMember = memberTestPersister.builder().save();
        Member otherMember = memberTestPersister.builder().save();
        followTestPersister.builder().following(otherMember).follower(loginMember).save();

        MemberProfileResponse response = memberService.getMyProfile(loginMember);

        assertSoftly(softly -> {
            softly.assertThat(response.id()).isEqualTo(loginMember.getId());
            softly.assertThat(response.nickname()).isEqualTo(loginMember.getNickname());
            softly.assertThat(response.introduction()).isEqualTo(loginMember.getIntroduction());
            softly.assertThat(response.followerCount()).isEqualTo(0);
            softly.assertThat(response.followingCount()).isEqualTo(1);
            softly.assertThat(response.isMyProfile()).isTrue();
            softly.assertThat(response.isFollowing()).isFalse();
        });
    }

    @Nested
    class 닉네임_존재_여부_확인_시 {

        @Test
        void 존재하는_닉네임이라면_true_응답한다() {
            memberTestPersister.builder().nickname("hello").save();

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
    class 프로필_정보_수정_시 {

        @Test
        void 정상적인_요청이라면_프로필_정보를_수정한다() {
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            memberService.updateProfile(updateProfileRequest, member);

            assertSoftly(softly -> {
                softly.assertThat(member.getNickname()).isEqualTo("hello");
                softly.assertThat(member.getIntroduction()).isEqualTo("friend");
            });
        }

        @Test
        void 변경_닉네임이_현재_닉네임이라면_프로필_정보를_수정한다() {
            Member member = memberTestPersister.builder().nickname("hello").save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            memberService.updateProfile(updateProfileRequest, member);

            assertSoftly(softly -> {
                softly.assertThat(member.getNickname()).isEqualTo("hello");
                softly.assertThat(member.getIntroduction()).isEqualTo("friend");
            });
        }

        @Test
        void 정상적이지_않은_닉네임이라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("하 이", "friend");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, member))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("한글, 영어, 숫자로 구성된 1글자 이상, 10글자 이하의 닉네임이 아닙니다.");
        }

        @Test
        void 정상적이지_않은_한_줄_소개라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "  ");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, member))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("공백만으로 이루어지지 않은 1글자 이상, 100글자 이하의 한 줄 소개가 아닙니다.");
        }

        @Test
        void 변경_닉네임이_현재_닉네임이_아니고_존재하는_닉네임이라면_예외를_던진다() {
            memberTestPersister.builder().nickname("hello").save();
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 닉네임입니다.");
        }
    }

    @Nested
    class 프로필_이미지_수정_시 {

        @Test
        void 기존_프로필_이미지는_없고_요청_프로필_이미지도_없으면_프로필_이미지는_존재하지_않는다() {
            Member member = memberTestPersister.builder().save();

            memberService.updateThumbnail(null, member);

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertThat(memberThumbnail).isNotPresent();
        }

        @Test
        void 기존_프로필_이미지는_없고_요청_프로필_이미지가_있으면_새_프로필_이미지를_등록한다() {
            Member member = memberTestPersister.builder().save();
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile();

            memberService.updateThumbnail(multipartFile, member);

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertSoftly(softly -> {
                softly.assertThat(memberThumbnail).isPresent();
                softly.assertThat(new File(memberThumbnail.get().getThumbnail().getPath())).exists();
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 기존_프로필_이미지는_있고_요청_프로필_이미지가_없으면_기존_프로필_이미지를_제거한다() {
            Member member = memberTestPersister.builder().save();
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile();
            Thumbnail thumbnail = thumbnailService.save(multipartFile);
            memberThumbnailTestPersister.builder().member(member).thumbnail(thumbnail).save();

            memberService.updateThumbnail(null, member);

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertSoftly(softly -> {
                softly.assertThat(memberThumbnail).isNotPresent();
                softly.assertThat(new File(thumbnail.getPath())).doesNotExist();
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 기존_프로필_이미지는_있고_요청_프로필_이미지도_있으면_기존_프로필_이미지를_제거_후_새_프로필_이미지를_등록한다() {
            Member member = memberTestPersister.builder().save();
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile();
            Thumbnail thumbnail = thumbnailService.save(multipartFile);
            memberThumbnailTestPersister.builder().member(member).thumbnail(thumbnail).save();

            MultipartFile newFile = FileTestUtils.generateMultiPartFile();
            memberService.updateThumbnail(newFile, member);

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertSoftly(softly -> {
                softly.assertThat(memberThumbnail).isPresent();
                softly.assertThat(new File(memberThumbnail.get().getThumbnail().getPath())).exists();
                softly.assertThat(new File(thumbnail.getPath())).doesNotExist();
            });
            FileTestUtils.cleanUp();
        }
    }
}
