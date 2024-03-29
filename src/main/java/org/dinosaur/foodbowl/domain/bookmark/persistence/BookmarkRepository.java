package org.dinosaur.foodbowl.domain.bookmark.persistence;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepository extends Repository<Bookmark, Long> {

    Optional<Bookmark> findByMemberAndStore(Member member, Store store);

    @EntityGraph(attributePaths = {"store"})
    List<Bookmark> findByMember(Member member);

    Bookmark save(Bookmark bookmark);

    void delete(Bookmark bookmark);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Bookmark b where b.member = :member")
    void deleteByMember(@Param("member") Member member);
}
