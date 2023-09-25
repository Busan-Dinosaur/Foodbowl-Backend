package org.dinosaur.foodbowl.domain.follow.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;

public interface FollowCustomRepository {

    List<MemberFollowerCountDto> getFollowerCountByMembers(List<Member> members);
}
