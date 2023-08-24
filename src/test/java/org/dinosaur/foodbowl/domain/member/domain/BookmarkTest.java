package org.dinosaur.foodbowl.domain.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookmarkTest {

    @Test
    void 북마크를_생성한다() {
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
        Bookmark bookmark = Bookmark.builder()
                .member(member)
                .store(store)
                .build();

        assertThat(bookmark.getStore()).isEqualTo(store);
    }
}
