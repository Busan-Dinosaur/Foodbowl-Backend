package org.dinosaur.foodbowl.domain.member.repository;

import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.springframework.data.repository.Repository;

import java.util.Optional;

import static org.dinosaur.foodbowl.domain.member.entity.Member.SocialType;

public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
