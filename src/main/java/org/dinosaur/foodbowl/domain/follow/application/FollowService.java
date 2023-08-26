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
    public PageResponse<FollowingResponse> getFollowings(int page, int size, Member loginMember) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<FollowingResponse> followings = followRepository.findAllByFollower(loginMember, pageable)
                .map(Follow::getFollowing)
                .map(FollowingResponse::from);
        return PageResponse.from(followings);
    }

    @Transactional(readOnly = true)
    public PageResponse<OtherUserFollowingResponse> getOtherUserFollowings(
            Long targetMemberId,
            int page,
            int size,
            Member loginMember
    ) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<OtherUserFollowingResponse> followings = followRepository.findAllByFollower(targetMember, pageable)
                .map(Follow::getFollowing)
                .map(following -> {
                    Optional<Follow> follow = followRepository.findByFollowingAndFollower(following, loginMember);
                    return OtherUserFollowingResponse.of(following, follow.isPresent());
                });
        return PageResponse.from(followings);
    }

    @Transactional(readOnly = true)
    public PageResponse<FollowerResponse> getFollowers(int page, int size, Member loginMember) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<FollowerResponse> followers = followRepository.findAllByFollowing(loginMember, pageable)
                .map(Follow::getFollower)
                .map(FollowerResponse::from);
        return PageResponse.from(followers);
    }

    @Transactional(readOnly = true)
    public PageResponse<OtherUserFollowerResponse> getOtherUserFollowers(
            Long targetMemberId,
            int page,
            int size,
            Member loginMember
    ) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<OtherUserFollowerResponse> followers = followRepository.findAllByFollowing(targetMember, pageable)
                .map(Follow::getFollower)
                .map(follower -> {
                    Optional<Follow> follow = followRepository.findByFollowingAndFollower(follower, loginMember);
                    return OtherUserFollowerResponse.of(follower, follow.isPresent());
                });
        return PageResponse.from(followers);
    }

    @Transactional
    public void follow(Long targetMemberId, Member loginMember) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        validateFollow(targetMember, loginMember);

        Follow follow = Follow.builder()
                .following(targetMember)
                .follower(loginMember)
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
    public void unfollow(Long targetMemberId, Member loginMember) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Follow follow = followRepository.findByFollowingAndFollower(targetMember, loginMember)
                .orElseThrow(() -> new BadRequestException(FollowExceptionType.UNFOLLOWED));
        followRepository.delete(follow);
    }

    @Transactional
    public void deleteFollower(Long targetMemberId, Member loginMember) {
        Member targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Follow follow = followRepository.findByFollowingAndFollower(loginMember, targetMember)
                .orElseThrow(() -> new BadRequestException(FollowExceptionType.UNFOLLOWED_ME));
        followRepository.delete(follow);
    }
}
