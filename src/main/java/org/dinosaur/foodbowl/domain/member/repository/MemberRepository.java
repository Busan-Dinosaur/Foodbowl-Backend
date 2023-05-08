package org.dinosaur.foodbowl.domain.member.repository;

import static org.dinosaur.foodbowl.domain.member.entity.Member.SocialType;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<Member> findById(Long id);
}
