package org.dinosaur.foodbowl.domain.bookmark.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.springframework.data.repository.Repository;

public interface BookmarkRepository extends Repository<Bookmark, Long> {

    Optional<Bookmark> findByMemberAndStore(Member member, Store store);

    Bookmark save(Bookmark bookmark);
}
