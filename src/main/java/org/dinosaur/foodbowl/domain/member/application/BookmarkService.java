package org.dinosaur.foodbowl.domain.member.application;

import static org.dinosaur.foodbowl.domain.member.exception.BookmarkExceptionType.DUPLICATE_ERROR;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Bookmark;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.persistence.BookmarkRepository;
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
    public Long save(Long storeId, Member member) {
        Store store = storeService.findById(storeId);

        bookmarkRepository.findByMemberAndStore(member, store).ifPresent(
                ignore -> {
                    throw new BadRequestException(DUPLICATE_ERROR);
                }
        );

        Bookmark bookmark = Bookmark.builder()
                .store(store)
                .member(member)
                .build();
        return bookmarkRepository.save(bookmark).getId();
    }
}
