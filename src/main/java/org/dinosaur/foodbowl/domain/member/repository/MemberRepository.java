package org.dinosaur.foodbowl.domain.member.repository;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {
}
