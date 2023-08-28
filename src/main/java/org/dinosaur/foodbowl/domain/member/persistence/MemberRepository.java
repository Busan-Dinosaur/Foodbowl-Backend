package org.dinosaur.foodbowl.domain.member.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends Repository<Member, Long> {

    Optional<Member> findById(Long id);

    @Query("select m from Member m"
            + " left join fetch m.memberThumbnail t"
            + " left join fetch t.thumbnail"
            + " where m.id = :id")
    Optional<Member> findByIdWithThumbnail(@Param("id") Long id);

    Optional<Member> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    boolean existsByNickname(Nickname nickname);

    Member save(Member member);
}
