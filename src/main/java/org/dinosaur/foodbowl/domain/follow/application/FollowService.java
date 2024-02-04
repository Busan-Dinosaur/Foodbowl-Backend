package org.dinosaur.foodbowl.domain.follow.application;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowingResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowingResponse;
import org.dinosaur.foodbowl.domain.follow.exception.FollowExceptionType;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.common.response.PageResponse;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public PageResponse<FollowingResponse> getFollowings(int page, int size, LoginMember loginMember) {
        Member follower = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<FollowingResponse> followings = followRepository.findAllByFollower(follower, pageable)
                .map(Follow::getFollowing)
                .map(FollowingResponse::from);
        return PageResponse.from(followings);
    }

    @Transactional(readOnly = true)
    public PageResponse<OtherUserFollowingResponse> getOtherUserFollowings(
            Long targetMemberId,
            int page,
            int size,
            LoginMember loginMember
    ) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<OtherUserFollowingResponse> followings = followRepository.findAllByFollower(targetMember, pageable)
                .map(Follow::getFollowing)
                .map(following -> {
                    Optional<Follow> follow = followRepository.findByFollowingAndFollower(following, viewer);
                    return OtherUserFollowingResponse.of(
                            following,
                            follow.isPresent(),
                            Objects.equals(viewer, following)
                    );
                });
        return PageResponse.from(followings);
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowerResponse> getFollowers(int page, int size, LoginMember loginMember) {
        Member followee = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<FollowerResponse> followers = followRepository.findAllByFollowing(followee, pageable)
                .map(Follow::getFollower)
                .map(FollowerResponse::from);
        return PageResponse.from(followers);
    }

    @Transactional(readOnly = true)
    public PageResponse<OtherUserFollowerResponse> getOtherUserFollowers(
            Long targetMemberId,
            int page,
            int size,
            LoginMember loginMember
    ) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<OtherUserFollowerResponse> followers = followRepository.findAllByFollowing(targetMember, pageable)
                .map(Follow::getFollower)
                .map(follower -> {
                    Optional<Follow> follow = followRepository.findByFollowingAndFollower(follower, viewer);
                    return OtherUserFollowerResponse.of(
                            follower,
                            follow.isPresent(),
                            Objects.equals(viewer, follower)
                    );
                });
        return PageResponse.from(followers);
    }

    @Transactional
    public void follow(Long targetMemberId, LoginMember loginMember) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        Member follower = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        validateFollow(targetMember, follower);

        Follow follow = Follow.builder()
                .following(targetMember)
                .follower(follower)
                .build();
        followRepository.save(follow);
    }

    private void validateFollow(Member targetMember, Member loginMember) {
        if (Objects.equals(loginMember, targetMember)) {
            throw new BadRequestException(FollowExceptionType.FOLLOW_ME);
        }

        followRepository.findByFollowingAndFollower(targetMember, loginMember)
                .ifPresent(ignore -> {
                    throw new BadRequestException(FollowExceptionType.DUPLICATE_FOLLOW);
                });
    }

    @Transactional
    public void unfollow(Long targetMemberId, LoginMember loginMember) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        Member follower = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Follow follow = followRepository.findByFollowingAndFollower(targetMember, follower)
                .orElseThrow(() -> new BadRequestException(FollowExceptionType.UNFOLLOWED));
        followRepository.delete(follow);
    }

    @Transactional
    public void deleteFollower(Long targetMemberId, LoginMember loginMember) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        Member followee = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Follow follow = followRepository.findByFollowingAndFollower(followee, targetMember)
                .orElseThrow(() -> new BadRequestException(FollowExceptionType.UNFOLLOWED_ME));
        followRepository.delete(follow);
    }
}
