package org.dinosaur.foodbowl.domain.member.application;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.persistence.BlameRepository;
import org.dinosaur.foodbowl.domain.bookmark.persistence.BookmarkRepository;
import org.dinosaur.foodbowl.domain.follow.application.FollowCustomService;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowingsDto;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.dinosaur.foodbowl.domain.member.domain.vo.Introduction;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileImageResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberSearchResponses;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRoleRepository;
import org.dinosaur.foodbowl.domain.member.persistence.MemberThumbnailRepository;
import org.dinosaur.foodbowl.domain.photo.application.PhotoService;
import org.dinosaur.foodbowl.domain.photo.application.ThumbnailService;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoCustomRepository;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final ThumbnailService thumbnailService;
    private final PhotoService photoService;
    private final MemberCustomService memberCustomService;
    private final FollowCustomService followCustomService;

    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final MemberThumbnailRepository memberThumbnailRepository;
    private final FollowRepository followRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BlameRepository blameRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoCustomRepository reviewPhotoCustomRepository;

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(Long memberId, LoginMember loginMember) {
        Member member = memberRepository.findByIdWithThumbnail(memberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        long followingCount = followRepository.countByFollower(member);
        if (Objects.equals(member, viewer)) {
            return MemberProfileResponse.of(member, (int) followingCount, true, false);
        }

        Optional<Follow> follow = followRepository.findByFollowingAndFollower(member, viewer);
        return MemberProfileResponse.of(member, (int) followingCount, false, follow.isPresent());
    }

    @Transactional(readOnly = true)
    public MemberProfileResponse getMyProfile(LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        long followingCount = followRepository.countByFollower(member);
        return MemberProfileResponse.of(member, (int) followingCount, true, false);
    }

    @Transactional(readOnly = true)
    public MemberSearchResponses search(String name, int size, LoginMember loginMember) {
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        List<Member> members = memberCustomService.search(name, size);

        MemberToFollowerCountDto followerCountByMembers = followCustomService.getFollowerCountByMembers(members);
        MemberToFollowingsDto followingsByMember = followCustomService.getFollowInMembers(members, viewer);

        return MemberSearchResponses.of(
                members,
                viewer,
                followerCountByMembers,
                followingsByMember
        );
    }

    @Transactional(readOnly = true)
    public NicknameExistResponse checkNicknameExist(String nickname) {
        boolean isExist = memberRepository.existsByNickname(new Nickname(nickname));
        return new NicknameExistResponse(isExist);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest updateProfileRequest, LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Nickname nickname = new Nickname(updateProfileRequest.nickname());
        Introduction introduction = new Introduction(updateProfileRequest.introduction());

        if (!member.hasNickname(nickname)) {
            validateExistNickname(nickname);
        }
        member.updateProfile(nickname, introduction);
    }

    private void validateExistNickname(Nickname nickname) {
        boolean nicknameExist = memberRepository.existsByNickname(nickname);
        if (nicknameExist) {
            throw new BadRequestException(MemberExceptionType.DUPLICATE_NICKNAME);
        }
    }

    @Transactional
    public MemberProfileImageResponse updateProfileImage(MultipartFile image, LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        memberThumbnailRepository.findByMember(member)
                .ifPresent(this::deleteMemberThumbnail);
        Thumbnail thumbnail = thumbnailService.save(image);
        MemberThumbnail memberThumbnail = MemberThumbnail.builder()
                .member(member)
                .thumbnail(thumbnail)
                .build();
        memberThumbnailRepository.save(memberThumbnail);
        return new MemberProfileImageResponse(thumbnail.getPath());
    }

    private void deleteMemberThumbnail(MemberThumbnail memberThumbnail) {
        memberThumbnailRepository.delete(memberThumbnail);
        thumbnailService.delete(memberThumbnail.getThumbnail());
    }

    @Transactional
    public void deleteProfileImage(LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        memberThumbnailRepository.findByMember(member)
                .ifPresent(this::deleteMemberThumbnail);
    }

    @Transactional
    public void deactivate(LoginMember loginMember) {
        Member member = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        deleteMemberDetails(member);
        deleteMemberActivity(member);
        memberRepository.delete(member);
    }

    private void deleteMemberDetails(Member member) {
        followRepository.deleteByMember(member);
        memberRoleRepository.deleteByMember(member);
        memberThumbnailRepository.findByMember(member)
                .ifPresent(this::deleteMemberThumbnail);
    }

    private void deleteMemberActivity(Member member) {
        bookmarkRepository.deleteByMember(member);
        blameRepository.deleteByMember(member.getId(), BlameTarget.MEMBER);
        deleteMemberReviews(member);
    }

    private void deleteMemberReviews(Member member) {
        List<Review> reviews = reviewRepository.findAllByMember(member);
        List<ReviewPhoto> reviewPhotos = reviewPhotoCustomRepository.findAllReviewPhotosInReviews(reviews);
        reviewPhotoCustomRepository.deleteAllByReviews(reviews);
        photoService.deleteAll(getPhotos(reviewPhotos));
        reviewRepository.deleteByMember(member);
    }

    private List<Photo> getPhotos(List<ReviewPhoto> reviewPhotos) {
        return reviewPhotos.stream()
                .map(ReviewPhoto::getPhoto)
                .toList();
    }
}
