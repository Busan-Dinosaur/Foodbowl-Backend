package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.domain.bookmark.persistence.BookmarkRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class BookmarkServiceTest extends IntegrationTest {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Nested
    class 북마크_추가_시 {

        @Test
        void 북마크_추가에_성공하면_북마크가_등록된다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.builder().save();

            bookmarkService.save(store.getId(), new LoginMember(member.getId()));

            assertThat(bookmarkRepository.findByMemberAndStore(member, store)).isPresent();
        }

        @Test
        void 가게가_존재하지_않으면_예외가_발생한다() {
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> bookmarkService.save(-1L, new LoginMember(member.getId())))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 가게를 찾을 수 없습니다.");
        }

        @Test
        void 등록되지_회원의_북마크_추가라면_예외를_던진다() {
            Store store = storeTestPersister.builder().save();

            assertThatThrownBy(() -> bookmarkService.save(store.getId(), new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 이미_북마크에_추가된_가게라면_예외가_발생한다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.builder().save();
            bookmarkService.save(store.getId(), new LoginMember(member.getId()));

            assertThatThrownBy(() -> bookmarkService.save(store.getId(), new LoginMember(member.getId())))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 북마크에 추가된 가게입니다.");
        }
    }

    @Nested
    class 북마크_삭제_시 {

        @Test
        void 북마크_삭제에_성공하면_북마크가_삭제된다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.builder().save();
            bookmarkService.save(store.getId(), new LoginMember(member.getId()));

            bookmarkService.delete(store.getId(), new LoginMember(member.getId()));

            assertThat(bookmarkRepository.findByMemberAndStore(member, store)).isEmpty();
        }

        @Test
        void 가게가_존재하지_않으면_예외가_발생한다() {
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> bookmarkService.delete(-1L, new LoginMember(member.getId())))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 가게를 찾을 수 없습니다.");
        }

        @Test
        void 등록되지_않은_회원의_북마크_삭제라면_예외를_던진다() {
            Store store = storeTestPersister.builder().save();

            assertThatThrownBy(() -> bookmarkService.delete(store.getId(), new LoginMember(-1L)))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 북마크에_등록하지_않은_가게라면_예외가_발생한다() {
            Store store = storeTestPersister.builder().save();
            Member member = memberTestPersister.builder().save();

            assertThatThrownBy(() -> bookmarkService.delete(store.getId(), new LoginMember(member.getId())))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("해당 가게는 사용자의 북마크에 추가되어 있지 않습니다.");
        }
    }
}
