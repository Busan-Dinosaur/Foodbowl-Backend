package org.dinosaur.foodbowl.domain.follow.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;

public interface FollowCustomRepository {

    List<MemberFollowCountDto> getFollowCountByMembers(List<Member> members);
}
