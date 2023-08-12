package org.dinosaur.foodbowl.domain.follow.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.exception.FollowExceptionType;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

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
        if (loginMember == targetMember) {
            throw new BadRequestException(FollowExceptionType.FOLLOW_ME);
        }

        followRepository.findByFollowingAndFollower(targetMember, loginMember)
                .ifPresent(ignore -> {
                    throw new BadRequestException(FollowExceptionType.DUPLICATE_FOLLOW);
                });
    }
}
