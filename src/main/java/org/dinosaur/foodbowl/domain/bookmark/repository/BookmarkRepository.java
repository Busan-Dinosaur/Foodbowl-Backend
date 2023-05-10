package org.dinosaur.foodbowl.domain.bookmark.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.bookmark.entity.Bookmark;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.repository.Repository;

public interface BookmarkRepository extends Repository<Bookmark, Long> {

    List<Bookmark> findAll();

    Bookmark save(Bookmark bookmark);

    void deleteAllByMember(Member member);

    void deleteAllByPost(Post post);
}
