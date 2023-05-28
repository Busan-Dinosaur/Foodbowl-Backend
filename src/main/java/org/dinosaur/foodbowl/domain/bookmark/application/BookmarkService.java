package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.MEMBER_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkThumbnailResponse;
import org.dinosaur.foodbowl.domain.bookmark.entity.Bookmark;
import org.dinosaur.foodbowl.domain.bookmark.repository.BookmarkRepository;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;

    public PageResponse<BookmarkThumbnailResponse> findThumbnailsInProfile(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        Page<BookmarkThumbnailResponse> pageOfResponse = bookmarkRepository.findAllByMember(member, pageable)
                .map(BookmarkThumbnailResponse::from);
        return PageResponse.from(pageOfResponse);
    }

    public List<BookmarkStoreMarkerResponse> findBookmarkStoreMarkers(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        return bookmarkRepository.findWithPostAndStoreAllByMember(member)
                .stream()
                .map(Bookmark::getPost)
                .map(Post::getStore)
                .distinct()
                .map(BookmarkStoreMarkerResponse::from)
                .toList();
    }
}
