package org.dinosaur.foodbowl.domain.blame.application;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.dto.request.BlameRequest;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class BlameServiceTest extends IntegrationTest {

    @Autowired
    private BlameService blameService;

    @Nested
    class 신고_시 {

        @Test
        void 정상적인_요청이라면_신고가_등록된다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member target = memberTestPersister.memberBuilder().save();
            BlameRequest request = new BlameRequest(target.getId(), BlameTarget.MEMBER, "부적절한 닉네임");

            assertThatNoException().isThrownBy(() -> blameService.blame(request, loginMember));
        }

        @Test
        void 회원_신고_시_존재하지_않는_회원이면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Long invalidMemberId = loginMember.getId() + 1;
            BlameRequest request = new BlameRequest(invalidMemberId, BlameTarget.MEMBER, "부적절한 닉네임");

            assertThatThrownBy(() -> blameService.blame(request, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("존재하지 않는 신고 대상입니다.");
        }

        @Test
        void 리뷰_신고_시_존재하지_않는_리뷰라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            BlameRequest request = new BlameRequest(1L, BlameTarget.REVIEW, "부적절한 리뷰 내용");

            assertThatThrownBy(() -> blameService.blame(request, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("존재하지 않는 신고 대상입니다.");
        }

        @Test
        void 회원_신고_시_나를_신고한다면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            BlameRequest request = new BlameRequest(loginMember.getId(), BlameTarget.MEMBER, "부적절한 닉네임");

            assertThatThrownBy(() -> blameService.blame(request, loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("스스로에 대해 신고할 수 없습니다.");
        }

        @Test
        void 리뷰_신고_시_나의_리뷰를_신고한다면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Review review = reviewTestPersister.builder().member(loginMember).save();
            BlameRequest request = new BlameRequest(review.getId(), BlameTarget.REVIEW, "부적절한 닉네임");

            assertThatThrownBy(() -> blameService.blame(request, loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("스스로에 대해 신고할 수 없습니다.");
        }

        @Test
        void 이미_신고한_대상이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member target = memberTestPersister.memberBuilder().save();
            BlameRequest request = new BlameRequest(target.getId(), BlameTarget.MEMBER, "부적절한 닉네임");
            blameService.blame(request, loginMember);

            assertThatThrownBy(() -> blameService.blame(request, loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("신고 대상에 대한 신고 이력이 존재합니다.");
        }
    }
}
