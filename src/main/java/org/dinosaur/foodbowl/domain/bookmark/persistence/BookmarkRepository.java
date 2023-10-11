package org.dinosaur.foodbowl.domain.bookmark.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

public interface BookmarkRepository extends Repository<Bookmark, Long> {

    @EntityGraph(attributePaths = {"store"})
    List<Bookmark> findByMember(Member member);

    Bookmark save(Bookmark bookmark);

    void delete(Bookmark bookmark);
}
