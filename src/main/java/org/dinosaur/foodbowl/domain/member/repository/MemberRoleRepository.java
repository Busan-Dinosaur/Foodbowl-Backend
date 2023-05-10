package org.dinosaur.foodbowl.domain.member.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.entity.MemberRole;
import org.springframework.data.repository.Repository;

public interface MemberRoleRepository extends Repository<MemberRole, Long> {

    List<MemberRole> findAll();

    MemberRole save(MemberRole memberRole);

    void deleteAllByMember(Member member);
}
