package org.dinosaur.foodbowl.domain.bookmark.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.bookmark.exception.BookmarkExceptionType;
import org.dinosaur.foodbowl.domain.bookmark.persistence.BookmarkRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final MemberRepository memberRepository;
    private final BookmarkRepository bookmarkRepository;
    private final StoreService storeService;

    @Transactional
    public void save(Long storeId, LoginMember loginMember) {
        Store store = storeService.findById(storeId);
        Member bookmarkOwner = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        bookmarkRepository.findByMemberAndStore(bookmarkOwner, store).ifPresent(
                ignore -> {
                    throw new BadRequestException(BookmarkExceptionType.DUPLICATE);
                }
        );

        Bookmark bookmark = Bookmark.builder()
                .store(store)
                .member(bookmarkOwner)
                .build();
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void delete(Long storeId, LoginMember loginMember) {
        Store store = storeService.findById(storeId);
        Member bookmarkOwner = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        Bookmark bookmark = bookmarkRepository.findByMemberAndStore(bookmarkOwner, store)
                .orElseThrow(() -> new BadRequestException(BookmarkExceptionType.NOT_FOUND));

        bookmarkRepository.delete(bookmark);
    }
}
