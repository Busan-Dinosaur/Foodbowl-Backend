package org.dinosaur.foodbowl.domain.member.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MemberRoleRepository extends Repository<MemberRole, Long> {

    List<MemberRole> findAllByMember(Member member);

    MemberRole save(MemberRole memberRole);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from MemberRole mr where mr.member = :member")
    void deleteByMember(@Param("member") Member member);
}
