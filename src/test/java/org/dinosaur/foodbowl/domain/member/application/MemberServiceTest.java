package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberRole;
import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.dinosaur.foodbowl.domain.member.domain.Role;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileImageResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberSearchResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberSearchResponses;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRoleRepository;
import org.dinosaur.foodbowl.domain.member.persistence.MemberThumbnailRepository;
import org.dinosaur.foodbowl.domain.photo.application.PhotoService;
import org.dinosaur.foodbowl.domain.photo.application.ThumbnailService;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
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
    private MemberRoleRepository memberRoleRepository;

    @Autowired
    private MemberThumbnailRepository memberThumbnailRepository;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Nested
    class 프로필_조회_시 {

        @Test
        void 나의_프로필이라면_나의_프로필_여부는_true_팔로잉_여부는_false_이다() {
            Member loginMember = memberTestPersister.builder().save();

            MemberProfileResponse response =
                    memberService.getProfile(loginMember.getId(), new LoginMember(loginMember.getId()));

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

            MemberProfileResponse response =
                    memberService.getProfile(profileTargetMember.getId(), new LoginMember(loginMember.getId()));

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

            MemberProfileResponse response =
                    memberService.getProfile(profileTargetMember.getId(), new LoginMember(loginMember.getId()));

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

            assertThatThrownBy(() -> memberService.getProfile(-1L, new LoginMember(loginMember.getId())))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 등록되지_않은_회원의_프로필_조회라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> memberService.getProfile(member.getId(), new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Nested
    class 나의_프로필_조회_시 {

        @Test
        void 나의_프로필을_조회한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member otherMember = memberTestPersister.builder().save();
            followTestPersister.builder().following(otherMember).follower(loginMember).save();

            MemberProfileResponse response = memberService.getMyProfile(new LoginMember(loginMember.getId()));

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

        @Test
        void 등록되지_않은_회원의_프로필_조회라면_예외를_던진다() {
            assertThatThrownBy(() -> memberService.getMyProfile(new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Nested
    class 닉네임을_이용해_회원_검색_시 {

        @Test
        void 정상적으로_검색한다() {
            String name = "gray";
            Member dazzle = memberTestPersister.builder().nickname("dazzle").save();
            Member memberA = memberTestPersister.builder().nickname("gray1234").save();
            Member memberB = memberTestPersister.builder().nickname("gray").save();
            followTestPersister.builder().follower(dazzle).following(memberB).save();

            MemberSearchResponses responses = memberService.search(name, 10, new LoginMember(dazzle.getId()));
            List<MemberSearchResponse> memberSearchResponses = responses.memberSearchResponses();

            assertSoftly(softly -> {
                softly.assertThat(memberSearchResponses.get(0).memberId()).isEqualTo(memberB.getId());
                softly.assertThat(memberSearchResponses.get(0).nickname()).isEqualTo(memberB.getNickname());
                softly.assertThat(memberSearchResponses.get(0).profileImageUrl())
                        .isEqualTo(memberB.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(0).followerCount()).isOne();
                softly.assertThat(memberSearchResponses.get(0).isFollowing()).isTrue();
                softly.assertThat(memberSearchResponses.get(0).isMe()).isFalse();
                softly.assertThat(memberSearchResponses.get(1).memberId()).isEqualTo(memberA.getId());
                softly.assertThat(memberSearchResponses.get(1).nickname()).isEqualTo(memberA.getNickname());
                softly.assertThat(memberSearchResponses.get(1).profileImageUrl())
                        .isEqualTo(memberA.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(1).followerCount()).isZero();
                softly.assertThat(memberSearchResponses.get(1).isFollowing()).isFalse();
                softly.assertThat(memberSearchResponses.get(1).isMe()).isFalse();
            });
        }

        @Test
        void 등록되지_않은_회원의_회원_검색이라면_예외를_던진다() {
            String name = "gray";

            assertThatThrownBy(() -> memberService.search(name, 10, new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 결과에_검색한_사용자가_포함되면_isMe는_true_이다() {
            String name = "gray";
            Member member = memberTestPersister.builder().nickname("gray").save();

            MemberSearchResponses responses = memberService.search(name, 10, new LoginMember(member.getId()));
            List<MemberSearchResponse> memberSearchResponses = responses.memberSearchResponses();

            assertSoftly(softly -> {
                softly.assertThat(memberSearchResponses.get(0).memberId()).isEqualTo(member.getId());
                softly.assertThat(memberSearchResponses.get(0).nickname()).isEqualTo(member.getNickname());
                softly.assertThat(memberSearchResponses.get(0).profileImageUrl())
                        .isEqualTo(member.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(0).followerCount()).isZero();
                softly.assertThat(memberSearchResponses.get(0).isFollowing()).isFalse();
                softly.assertThat(memberSearchResponses.get(0).isMe()).isTrue();
            });
        }
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
    class 리뷰_많은_순으로_회원_목록_페이지_조회_시 {

        @Test
        void 조회에_성공한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member memberA = memberTestPersister.builder().save();
            Member memberB = memberTestPersister.builder().save();
            reviewTestPersister.builder().member(memberA).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberB).save();

            MemberSearchResponses responses =
                    memberService.getMembersSortByReviewCounts(0, 2, new LoginMember(loginMember.getId()));
            List<MemberSearchResponse> memberSearchResponses = responses.memberSearchResponses();

            assertSoftly(softly -> {
                softly.assertThat(memberSearchResponses.get(0).memberId()).isEqualTo(memberB.getId());
                softly.assertThat(memberSearchResponses.get(0).nickname()).isEqualTo(memberB.getNickname());
                softly.assertThat(memberSearchResponses.get(0).profileImageUrl())
                        .isEqualTo(memberB.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(0).followerCount()).isZero();
                softly.assertThat(memberSearchResponses.get(0).isFollowing()).isFalse();
                softly.assertThat(memberSearchResponses.get(0).isMe()).isFalse();
                softly.assertThat(memberSearchResponses.get(1).memberId()).isEqualTo(memberA.getId());
                softly.assertThat(memberSearchResponses.get(1).nickname()).isEqualTo(memberA.getNickname());
                softly.assertThat(memberSearchResponses.get(1).profileImageUrl())
                        .isEqualTo(memberA.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(1).followerCount()).isZero();
                softly.assertThat(memberSearchResponses.get(1).isFollowing()).isFalse();
                softly.assertThat(memberSearchResponses.get(1).isMe()).isFalse();
            });
        }

        @Test
        void 조회한_회원의_팔로워_수를_확인할_수_있다() {
            Member loginMember = memberTestPersister.builder().save();
            Member memberA = memberTestPersister.builder().save();
            Member memberB = memberTestPersister.builder().save();
            followTestPersister.builder().following(memberA).follower(memberB).save();
            reviewTestPersister.builder().member(memberA).save();

            MemberSearchResponses responses =
                    memberService.getMembersSortByReviewCounts(0, 1, new LoginMember(loginMember.getId()));
            List<MemberSearchResponse> memberSearchResponses = responses.memberSearchResponses();

            assertSoftly(softly -> {
                softly.assertThat(memberSearchResponses.get(0).memberId()).isEqualTo(memberA.getId());
                softly.assertThat(memberSearchResponses.get(0).nickname()).isEqualTo(memberA.getNickname());
                softly.assertThat(memberSearchResponses.get(0).profileImageUrl())
                        .isEqualTo(memberA.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(0).followerCount()).isOne();
                softly.assertThat(memberSearchResponses.get(0).isFollowing()).isFalse();
                softly.assertThat(memberSearchResponses.get(0).isMe()).isFalse();
            });
        }

        @Test
        void 조회한_회원이_조회를_요청한_회원이_팔로잉_중인지_확인할_수_있다() {
            Member loginMember = memberTestPersister.builder().save();
            Member member = memberTestPersister.builder().save();
            followTestPersister.builder().following(member).follower(loginMember).save();
            reviewTestPersister.builder().member(member).save();

            MemberSearchResponses responses =
                    memberService.getMembersSortByReviewCounts(0, 1, new LoginMember(loginMember.getId()));
            List<MemberSearchResponse> memberSearchResponses = responses.memberSearchResponses();

            assertSoftly(softly -> {
                softly.assertThat(memberSearchResponses.get(0).memberId()).isEqualTo(member.getId());
                softly.assertThat(memberSearchResponses.get(0).nickname()).isEqualTo(member.getNickname());
                softly.assertThat(memberSearchResponses.get(0).profileImageUrl())
                        .isEqualTo(member.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(0).followerCount()).isOne();
                softly.assertThat(memberSearchResponses.get(0).isFollowing()).isTrue();
                softly.assertThat(memberSearchResponses.get(0).isMe()).isFalse();
            });
        }

        @Test
        void 조회한_회원_목록에_조회를_요청한_회원이_포함되어_있다면_isMe는_true_이다() {
            Member loginMember = memberTestPersister.builder().save();
            reviewTestPersister.builder().member(loginMember).save();

            MemberSearchResponses responses =
                    memberService.getMembersSortByReviewCounts(0, 1, new LoginMember(loginMember.getId()));
            List<MemberSearchResponse> memberSearchResponses = responses.memberSearchResponses();

            assertSoftly(softly -> {
                softly.assertThat(memberSearchResponses.get(0).memberId()).isEqualTo(loginMember.getId());
                softly.assertThat(memberSearchResponses.get(0).nickname()).isEqualTo(loginMember.getNickname());
                softly.assertThat(memberSearchResponses.get(0).profileImageUrl())
                        .isEqualTo(loginMember.getProfileImageUrl());
                softly.assertThat(memberSearchResponses.get(0).followerCount()).isZero();
                softly.assertThat(memberSearchResponses.get(0).isFollowing()).isFalse();
                softly.assertThat(memberSearchResponses.get(0).isMe()).isTrue();
            });
        }

        @Test
        void 등록되지_않은_회원의_회원_목록_페이지_조회라면_예외를_던진다() {
            assertThatThrownBy(() -> memberService.getMembersSortByReviewCounts(0, 1, new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Nested
    class 프로필_정보_수정_시 {

        @Test
        void 정상적인_요청이라면_프로필_정보를_수정한다() {
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            memberService.updateProfile(updateProfileRequest, new LoginMember(member.getId()));

            assertSoftly(softly -> {
                softly.assertThat(member.getNickname()).isEqualTo("hello");
                softly.assertThat(member.getIntroduction()).isEqualTo("friend");
            });
        }

        @Test
        void 변경_닉네임이_현재_닉네임이라면_프로필_정보를_수정한다() {
            Member member = memberTestPersister.builder().nickname("hello").save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            memberService.updateProfile(updateProfileRequest, new LoginMember(member.getId()));

            assertSoftly(softly -> {
                softly.assertThat(member.getNickname()).isEqualTo("hello");
                softly.assertThat(member.getIntroduction()).isEqualTo("friend");
            });
        }

        @Test
        void 등록되지_않은_회원의_프로필_정보_수정이라면_예외를_던진다() {
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 정상적이지_않은_닉네임이라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("하 이", "friend");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, new LoginMember(member.getId())))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("한글, 영어, 숫자로 구성된 1글자 이상, 10글자 이하의 닉네임이 아닙니다.");
        }

        @Test
        void 정상적이지_않은_한_줄_소개라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "  ");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, new LoginMember(member.getId())))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("공백만으로 이루어지지 않은 1글자 이상, 100글자 이하의 한 줄 소개가 아닙니다.");
        }

        @Test
        void 변경_닉네임이_현재_닉네임이_아니고_존재하는_닉네임이라면_예외를_던진다() {
            memberTestPersister.builder().nickname("hello").save();
            Member member = memberTestPersister.builder().save();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest("hello", "friend");

            assertThatThrownBy(() -> memberService.updateProfile(updateProfileRequest, new LoginMember(member.getId())))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 닉네임입니다.");
        }
    }

    @Nested
    class 프로필_이미지_수정_시 {

        @Test
        void 기존_프로필_이미지는_없고_요청_프로필_이미지가_있으면_새_프로필_이미지를_등록한다() {
            Member member = memberTestPersister.builder().save();
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile("image");

            MemberProfileImageResponse response =
                    memberService.updateProfileImage(multipartFile, new LoginMember(member.getId()));

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertSoftly(softly -> {
                softly.assertThat(memberThumbnail).isPresent();
                softly.assertThat(memberThumbnail.get().getThumbnail().getPath()).isEqualTo(response.profileImageUrl());
                softly.assertThat(new File(response.profileImageUrl())).exists();
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 기존_프로필_이미지는_있고_요청_프로필_이미지도_있으면_기존_프로필_이미지를_제거_후_새_프로필_이미지를_등록한다() {
            Member member = memberTestPersister.builder().save();
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile("image");
            Thumbnail thumbnail = thumbnailService.save(multipartFile);
            memberThumbnailTestPersister.builder().member(member).thumbnail(thumbnail).save();

            MultipartFile newFile = FileTestUtils.generateMultiPartFile("image");
            MemberProfileImageResponse response =
                    memberService.updateProfileImage(newFile, new LoginMember(member.getId()));

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertSoftly(softly -> {
                softly.assertThat(memberThumbnail).isPresent();
                softly.assertThat(memberThumbnail.get().getThumbnail().getPath()).isEqualTo(response.profileImageUrl());
                softly.assertThat(new File(response.profileImageUrl())).exists();
                softly.assertThat(new File(thumbnail.getPath())).doesNotExist();
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 등록되지_않은_회원의_프로필_이미지_수정이라면_예외를_던진다() {
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile("image");

            assertThatThrownBy(() -> memberService.updateProfileImage(multipartFile, new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 요청_프로필_이미지가_없으면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> memberService.updateProfileImage(null, new LoginMember(member.getId())))
                    .isInstanceOf(FileException.class)
                    .hasMessage("파일이 존재하지 않습니다.");
        }
    }

    @Nested
    class 프로필_이미지_삭제_시 {

        @Test
        void 기존_프로필_이미지가_없으면_없는_상태를_유지한다() {
            Member member = memberTestPersister.builder().save();

            memberService.deleteProfileImage(new LoginMember(member.getId()));

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertThat(memberThumbnail).isNotPresent();
        }

        @Test
        void 기존_프로필_이미지가_있으면_프로필_이미지를_삭제한다() {
            Member member = memberTestPersister.builder().save();
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile("image");
            Thumbnail thumbnail = thumbnailService.save(multipartFile);
            memberThumbnailTestPersister.builder().member(member).thumbnail(thumbnail).save();

            memberService.deleteProfileImage(new LoginMember(member.getId()));

            Optional<MemberThumbnail> memberThumbnail = memberThumbnailRepository.findByMember(member);
            assertSoftly(softly -> {
                softly.assertThat(memberThumbnail).isNotPresent();
                softly.assertThat(new File(thumbnail.getPath())).doesNotExist();
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 등록되지_않은_회원의_프로필_이미지_삭제라면_예외를_던진다() {
            assertThatThrownBy(() -> memberService.deleteProfileImage(new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Nested
    class 회원_탈퇴_시 {

        @Test
        void 회원_탈퇴를_한다() {
            Member member = memberTestPersister.builder().save();
            followTestPersister.builder().follower(member).save();
            Role role = Role.builder()
                    .id(RoleType.ROLE_회원.getId())
                    .roleType(RoleType.ROLE_회원)
                    .build();
            MemberRole memberRole = MemberRole.builder()
                    .member(member)
                    .role(role)
                    .build();
            memberRoleRepository.save(memberRole);
            MultipartFile thumbnailFile = FileTestUtils.generateMultiPartFile("image");
            Thumbnail thumbnail = thumbnailService.save(thumbnailFile);
            memberThumbnailTestPersister.builder().member(member).thumbnail(thumbnail).save();
            bookmarkTestPersister.builder().member(member).save();
            blameTestPersister.builder().member(member).save();
            MultipartFile reviewPhotoFile = FileTestUtils.generateMultiPartFile("image");
            Photo photo = photoService.save(reviewPhotoFile, "1");
            Review review = reviewTestPersister.builder().member(member).save();
            reviewPhotoTestPersister.builder().review(review).photo(photo).save();

            assertThatNoException().isThrownBy(() -> memberService.deactivate(new LoginMember(member.getId())));
            assertSoftly(softly -> {
                softly.assertThat(new File(thumbnail.getPath())).doesNotExist();
                softly.assertThat(new File(photo.getPath())).doesNotExist();
            });
            FileTestUtils.cleanUp();
        }

        @Test
        void 등록되지_않은_회원의_회원_탈퇴라면_예외를_던진다() {
            assertThatThrownBy(() -> memberService.deactivate(new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }
}
