package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.dinosaur.foodbowl.domain.bookmark.exception.BookmarkExceptionType.DUPLICATE;
import static org.dinosaur.foodbowl.domain.bookmark.exception.BookmarkExceptionType.NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.bookmark.persistence.BookmarkRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StoreService storeService;

    @Transactional
    public void save(Long storeId, Member member) {
        Store store = storeService.findById(storeId);

        bookmarkRepository.findByMemberAndStore(member, store).ifPresent(
                ignore -> {
                    throw new BadRequestException(DUPLICATE);
                }
        );

        Bookmark bookmark = Bookmark.builder()
                .store(store)
                .member(member)
                .build();
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void delete(Long storeId, Member member) {
        Store store = storeService.findById(storeId);

        Bookmark bookmark = bookmarkRepository.findByMemberAndStore(member, store)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }
}
