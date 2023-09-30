package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.IntegrationTest;
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
}
