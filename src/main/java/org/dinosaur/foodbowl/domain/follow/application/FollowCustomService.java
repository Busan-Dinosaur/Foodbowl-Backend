package org.dinosaur.foodbowl.domain.follow.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowingsDto;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowCustomRepository;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.FollowAndFollowingDto;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FollowCustomService {

    private final FollowCustomRepository followCustomRepository;

    @Transactional(readOnly = true)
    public MemberToFollowerCountDto getFollowerCountByMembers(List<Member> members) {
        List<MemberFollowerCountDto> memberFollowerCounts = followCustomRepository.findFollowerCountByMembers(members);
        return MemberToFollowerCountDto.from(memberFollowerCounts);
    }

    @Transactional(readOnly = true)
    public MemberToFollowingsDto getFollowingsByMember(List<Member> members, Member loginMember) {
        List<FollowAndFollowingDto> followingsByFollowingsAndFollower = followCustomRepository.findFollowingsByFollowingsAndFollower(
                members, loginMember);
        return MemberToFollowingsDto.from(followingsByFollowingsAndFollower);
    }
}
