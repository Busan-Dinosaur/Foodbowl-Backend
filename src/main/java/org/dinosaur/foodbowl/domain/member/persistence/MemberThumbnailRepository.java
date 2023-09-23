package org.dinosaur.foodbowl.domain.member.persistence;

import org.dinosaur.foodbowl.domain.member.domain.MemberThumbnail;
import org.springframework.data.repository.Repository;

public interface MemberThumbnailRepository extends Repository<MemberThumbnail, Long> {

    MemberThumbnail save(MemberThumbnail memberThumbnail);

    void delete(MemberThumbnail memberThumbnail);
}
