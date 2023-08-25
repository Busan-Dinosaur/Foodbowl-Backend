package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class BookmarkServiceTest extends IntegrationTest {

    @Autowired
    private BookmarkService bookmarkService;

    @Nested
    class 북마크를_추가할_때 {

        @Test
        void 정상적으로_추가한다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.memberBuilder().save();

            Long saveId = bookmarkService.save(store.getId(), member);

            assertThat(saveId).isPositive();
        }

        @Test
        void 이미_북마크에_추가된_가게인_경우_예외가_발생한다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.memberBuilder().save();
            bookmarkService.save(store.getId(), member);

            assertThatThrownBy(() -> bookmarkService.save(store.getId(), member))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("이미 북마크에 추가된 가게입니다.");
        }
    }
}
