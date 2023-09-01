package org.dinosaur.foodbowl.domain.bookmark.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class BookmarkRepositoryTest extends PersistenceTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Test
    void 북마크를_저장한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Store store = storeTestPersister.builder().save();
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .store(store)
                .build();

        Bookmark saveBookmark = bookmarkRepository.save(bookmark);

        assertThat(saveBookmark).isEqualTo(bookmark);
    }

    @Test
    void 회원과_가게로_북마크를_조회한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Store store = storeTestPersister.builder().save();
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .store(store)
                .build();
        bookmarkRepository.save(bookmark);

        assertThat(bookmarkRepository.findByMemberAndStore(member, store)).isPresent();
    }

    @Test
    void 북마크를_삭제한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Store store = storeTestPersister.builder().save();
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .store(store)
                .build();
        bookmarkRepository.save(bookmark);

        bookmarkRepository.delete(bookmark);

        assertThat(bookmarkRepository.findByMemberAndStore(member, store)).isEmpty();
    }
}
