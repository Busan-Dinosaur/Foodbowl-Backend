package org.dinosaur.foodbowl.domain.blame.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.blame.domain.Blame;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface BlameRepository extends Repository<Blame, Long> {

    Optional<Blame> findByMemberAndTargetIdAndBlameTarget(Member member, Long targetId, BlameTarget blameTarget);

    Blame save(Blame blame);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Blame b where b.member.id = :memberId or (b.blameTarget = :blameTarget and b.targetId = :memberId)")
    void deleteByMember(@Param("memberId") Long memberId, @Param("blameTarget") BlameTarget blameTarget);
}
