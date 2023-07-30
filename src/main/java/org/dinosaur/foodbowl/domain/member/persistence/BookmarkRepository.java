package org.dinosaur.foodbowl.domain.member.persistence;

import org.dinosaur.foodbowl.domain.member.domain.Bookmark;
import org.springframework.data.repository.Repository;

public interface BookmarkRepository extends Repository<Bookmark, Long> {
}
