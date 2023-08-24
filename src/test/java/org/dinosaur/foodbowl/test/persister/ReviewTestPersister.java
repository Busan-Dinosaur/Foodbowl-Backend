package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.domain.store.domain.Store;

@RequiredArgsConstructor
@Persister
public class ReviewTestPersister {

    private final ReviewRepository reviewRepository;
    private final MemberTestPersister memberTestPersister;
    private final StoreTestPersister storeTestPersister;

    public ReviewBuilder builder() {
        return new ReviewBuilder();
    }

    public final class ReviewBuilder {

        private Member member;
        private Store store;
        private String content;

        public ReviewBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public ReviewBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public ReviewBuilder content(String content) {
            this.content = content;
            return this;
        }

        public Review save() {
            Review review = Review.builder()
                    .member(member == null ? memberTestPersister.memberBuilder().save() : member)
                    .store(store == null ? storeTestPersister.builder().save() : store)
                    .content(content == null ? "리뷰 내용입니다." : content)
                    .build();
            return reviewRepository.save(review);
        }
    }
}
