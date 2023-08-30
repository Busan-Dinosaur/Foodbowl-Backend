package org.dinosaur.foodbowl.domain.member.application;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.Introduction;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public MemberProfileResponse getProfile(Long memberId, Member loginMember) {
        Member member = memberRepository.findByIdWithThumbnail(memberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        long followingCount = followRepository.countByFollower(member);
        if (Objects.equals(member, loginMember)) {
            return MemberProfileResponse.of(member, (int) followingCount, true, false);
        }

        Optional<Follow> follow = followRepository.findByFollowingAndFollower(member, loginMember);
        return MemberProfileResponse.of(member, (int) followingCount, false, follow.isPresent());
    }

    @Transactional(readOnly = true)
    public NicknameExistResponse checkNicknameExist(String nickname) {
        boolean isExist = memberRepository.existsByNickname(new Nickname(nickname));
        return new NicknameExistResponse(isExist);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest updateProfileRequest, Member loginMember) {
        Nickname nickname = new Nickname(updateProfileRequest.nickname());
        Introduction introduction = new Introduction(updateProfileRequest.introduction());

        boolean nicknameExist = memberRepository.existsByNickname(nickname);
        if (nicknameExist) {
            throw new BadRequestException(MemberExceptionType.DUPLICATE_NICKNAME);
        }

        loginMember.updateProfile(nickname, introduction);
    }
}
