package org.dinosaur.foodbowl.domain.member.repository;

import static org.dinosaur.foodbowl.domain.member.entity.Member.SocialType;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {

    Optional<Member> findById(Long memberId);

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    List<Member> findAll();

    Member save(Member member);

    void delete(Member member);
}
