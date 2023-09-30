package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.bookmark.persistence.BookmarkRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;

@RequiredArgsConstructor
@Persister
public class BookmarkTestPersister {

    private final BookmarkRepository bookmarkRepository;
    private final MemberTestPersister memberTestPersister;
    private final StoreTestPersister storeTestPersister;

    public BookmarkBuilder builder() {
        return new BookmarkBuilder();
    }

    public final class BookmarkBuilder {

        private Member member;
        private Store store;

        public BookmarkBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public BookmarkBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public Bookmark save() {
            Bookmark bookmark = Bookmark.builder()
                    .member(member == null ? memberTestPersister.builder().save() : member)
                    .store(store == null ? storeTestPersister.builder().save() : store)
                    .build();
            return bookmarkRepository.save(bookmark);
        }
    }
}
