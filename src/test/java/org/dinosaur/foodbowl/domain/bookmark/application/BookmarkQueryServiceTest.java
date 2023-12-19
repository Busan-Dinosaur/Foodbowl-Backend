package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class BookmarkQueryServiceTest extends IntegrationTest {

    @Autowired
    private BookmarkQueryService bookmarkQueryService;

    @Test
    void 멤버의_북마크_가게_목록을_조회한다() {
        Member member = memberTestPersister.builder().save();
        Bookmark bookmarkA = bookmarkTestPersister.builder().member(member).save();
        Bookmark bookmarkB = bookmarkTestPersister.builder().member(member).save();

        Set<Store> result = bookmarkQueryService.getBookmarkStoresByMember(member);

        assertThat(result).contains(bookmarkA.getStore(), bookmarkB.getStore());
    }

    @Nested
    class 멤버의_가게_북마크_여부_조회_시 {

        @Test
        void 북마크_했다면_TRUE를_반환한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().member(member).store(store).save();

            assertThat(bookmarkQueryService.isBookmarkStoreByMember(member, store)).isTrue();
        }

        @Test
        void 북마크_하지_않았다면_FALSE를_반환한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();

            assertThat(bookmarkQueryService.isBookmarkStoreByMember(member, store)).isFalse();
        }
    }
}
