package org.dinosaur.foodbowl.domain.member.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.springframework.data.repository.Repository;

public interface MemberThumbnailRepository extends Repository<MemberThumbnail, Long> {

    Optional<MemberThumbnail> findByMember(Member member);

    MemberThumbnail save(MemberThumbnail memberThumbnail);

    void delete(MemberThumbnail memberThumbnail);
}
