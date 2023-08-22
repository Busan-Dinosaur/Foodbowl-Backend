package org.dinosaur.foodbowl.domain.follow.application;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowResponse;
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
    public PageResponse<FollowResponse> getFollowers(int page, int size, Member loginMember) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Slice<FollowResponse> follows = followRepository.findAllByFollowing(loginMember, pageable)
                .map(Follow::getFollower)
                .map(FollowResponse::from);
        return PageResponse.from(follows);
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
