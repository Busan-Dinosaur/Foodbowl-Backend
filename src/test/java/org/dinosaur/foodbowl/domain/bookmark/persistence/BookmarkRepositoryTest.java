package org.dinosaur.foodbowl.domain.bookmark.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
    void 멤버과_가게로_북마크를_조회한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Store store = storeTestPersister.builder().save();
        bookmarkTestPersister.builder().member(member).store(store).save();

        assertThat(bookmarkRepository.findByMemberAndStore(member, store)).isPresent();
    }

    @Test
    void 멤버의_북마크_목록을_조회한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Store storeA = storeTestPersister.builder().save();
        Store storeB = storeTestPersister.builder().save();
        Bookmark bookmarkA = bookmarkTestPersister.builder().member(member).store(storeA).save();
        Bookmark bookmarkB = bookmarkTestPersister.builder().member(member).store(storeB).save();

        List<Bookmark> result = bookmarkRepository.findByMember(member);

        assertThat(result).containsExactly(bookmarkA, bookmarkB);
    }

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
    void 북마크를_삭제한다() {
        Member member = memberTestPersister.memberBuilder().save();
        Store store = storeTestPersister.builder().save();
        Bookmark bookmark = bookmarkTestPersister.builder().member(member).store(store).save();

        bookmarkRepository.delete(bookmark);

        assertThat(bookmarkRepository.findByMemberAndStore(member, store)).isEmpty();
    }
}
