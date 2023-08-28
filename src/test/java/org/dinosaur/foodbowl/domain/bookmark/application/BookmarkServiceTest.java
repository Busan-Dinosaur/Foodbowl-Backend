package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
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
        void 존재하지_않는_가게인_경우_예외가_발생한다() {
            Member member = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> bookmarkService.save(-1L, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 가게를 찾을 수 없습니다.");
        }

        @Test
        void 이미_북마크에_추가된_가게인_경우_예외가_발생한다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.memberBuilder().save();
            bookmarkService.save(store.getId(), member);

            assertThatThrownBy(() -> bookmarkService.save(store.getId(), member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 북마크에 추가된 가게입니다.");
        }
    }

    @Nested
    class 북마크를_삭제할_때 {

        @Test
        void 정상적으로_삭제한다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.memberBuilder().save();
            bookmarkService.save(store.getId(), member);

            assertDoesNotThrow(() -> bookmarkService.delete(store.getId(), member));
        }

        @Test
        void 존재하지_않는_가게인_경우_예외가_발생한다() {
            Member member = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> bookmarkService.delete(-1L, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 가게를 찾을 수 없습니다.");
        }

        @Test
        void 가게_사용자_데이터로_추가된_북마크가_없으면_예외가_발생한다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> bookmarkService.delete(store.getId(), member))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("해당 가게는 사용자의 북마크에 추가되어 있지 않습니다.");
        }
    }
}
