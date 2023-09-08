package org.dinosaur.foodbowl.domain.review.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReviewTest {

    @Test
    void 리뷰를_생성한다() {
        Member member = Member.builder()
                .socialType(SocialType.APPLE)
                .socialId("1")
                .email("email@email.com")
                .nickname("hello")
                .introduction("hello world")
                .build();
        Store store = Store.builder()
                .storeName("농민백암순대")
                .storeUrl("http://foodbowl.com")
                .phone("02-123-4567")
                .build();
        Review review = Review.builder()
                .member(member)
                .store(store)
                .content("맛있어요")
                .build();

        assertThat(review.getContent()).isEqualTo("맛있어요");
    }

    @Nested
    class 리뷰_작성자_확인_시 {

        @Test
        void 리뷰_작성자가_아니면_true_반환한다() {
            Member member = Member.builder()
                    .socialType(SocialType.APPLE)
                    .socialId("1")
                    .email("email@email.com")
                    .nickname("hello")
                    .introduction("hello world")
                    .build();
            ReflectionTestUtils.setField(member, "id", 1L);
            Member otherMember = Member.builder()
                    .socialType(SocialType.APPLE)
                    .socialId("2")
                    .email("email2@email.com")
                    .nickname("helloAA")
                    .introduction("hello world2")
                    .build();
            ReflectionTestUtils.setField(otherMember, "id", 2L);
            Store store = Store.builder()
                    .storeName("농민백암순대")
                    .storeUrl("http://foodbowl.com")
                    .phone("02-123-4567")
                    .build();
            Review review = Review.builder()
                    .member(member)
                    .store(store)
                    .content("맛있어요")
                    .build();

            assertThat(review.isNotOwnerOf(otherMember)).isTrue();
        }

        @Test
        void 리뷰_작성자라면_false_반환한다() {
            Member member = Member.builder()
                    .socialType(SocialType.APPLE)
                    .socialId("1")
                    .email("email@email.com")
                    .nickname("hello")
                    .introduction("hello world")
                    .build();
            ReflectionTestUtils.setField(member, "id", 1L);
            Store store = Store.builder()
                    .storeName("농민백암순대")
                    .storeUrl("http://foodbowl.com")
                    .phone("02-123-4567")
                    .build();
            Review review = Review.builder()
                    .member(member)
                    .store(store)
                    .content("맛있어요")
                    .build();

            assertThat(review.isNotOwnerOf(member)).isFalse();
        }
    }
}
