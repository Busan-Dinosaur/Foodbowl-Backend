package org.dinosaur.foodbowl.domain.bookmark.application;

import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.bookmark.persistence.BookmarkRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkQueryService {

    private final BookmarkRepository bookmarkRepository;

    public Set<Store> getBookmarkStoresByMember(Member member) {
        List<Bookmark> bookmarks = bookmarkRepository.findByMember(member);
        return bookmarks.stream()
                .map(Bookmark::getStore)
                .collect(toSet());
    }
}
