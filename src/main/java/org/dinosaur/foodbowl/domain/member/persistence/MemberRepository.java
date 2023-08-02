package org.dinosaur.foodbowl.domain.member.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    boolean existsByNickname(String nickname);

    Member save(Member member);
}
