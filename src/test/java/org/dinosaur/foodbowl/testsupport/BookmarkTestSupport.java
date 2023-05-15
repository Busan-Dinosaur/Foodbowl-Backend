package org.dinosaur.foodbowl.testsupport;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.entity.Bookmark;
import org.dinosaur.foodbowl.domain.bookmark.repository.BookmarkRepository;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookmarkTestSupport {

    private final BookmarkRepository bookmarkRepository;
    private final MemberTestSupport memberTestSupport;
    private final PostTestSupport postTestSupport;

    public BookmarkBuilder builder() {
        return new BookmarkBuilder();
    }

    public final class BookmarkBuilder {

        private Member member;
        private Post post;

        public BookmarkBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public BookmarkBuilder post(Post post) {
            this.post = post;
            return this;
        }

        public Bookmark build() {
            return bookmarkRepository.save(
                    Bookmark.builder()
                            .member(member == null ? memberTestSupport.memberBuilder().build() : member)
                            .post(post == null ? postTestSupport.postBuilder().build() : post)
                            .build()
            );
        }
    }
}
