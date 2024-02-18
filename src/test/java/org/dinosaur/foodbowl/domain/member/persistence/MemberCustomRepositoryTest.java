package org.dinosaur.foodbowl.domain.member.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private MemberCustomRepository memberCustomRepository;

    @Test
    void 검색_키워드를_포함하거나_키워드로_시작하는_닉네임을_가진_회원을_조회한다() {
        String name = "gray";
        Member memberA = memberTestPersister.builder().nickname("gray1234").save();
        Member memberB = memberTestPersister.builder().nickname("1234gray").save();
        Member memberC = memberTestPersister.builder().nickname("gray하이").save();
        Member memberD = memberTestPersister.builder().nickname("gray").save();
        Member memberE = memberTestPersister.builder().nickname("grayabcd").save();
        Member memberF = memberTestPersister.builder().nickname("dazzle").save();

        List<Member> members = memberCustomRepository.search(name, 10);

        assertSoftly(softly -> {
            softly.assertThat(members).containsExactly(memberD, memberA, memberE, memberC);
            softly.assertThat(members).doesNotContain(memberB, memberF);
        });
    }

    @Nested
    class 리뷰_개수_많은_순_회원_목록_페이지_조회_시 {

        @Test
        void 리뷰_개수가_많은_순으로_정렬되어_조회한다() {
            Member memberA = memberTestPersister.builder().save();
            Member memberB = memberTestPersister.builder().save();
            Member memberC = memberTestPersister.builder().save();
            reviewTestPersister.builder().member(memberA).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberC).save();
            reviewTestPersister.builder().member(memberC).save();

            List<Member> members = memberCustomRepository.getMembersSortByReviewCounts(0, 3);

            assertThat(members).containsExactly(memberB, memberC, memberA);
        }

        @Test
        void 페이지_번호에_해당하는_리뷰_목록을_조회한다() {
            Member memberA = memberTestPersister.builder().save();
            Member memberB = memberTestPersister.builder().save();
            Member memberC = memberTestPersister.builder().save();
            reviewTestPersister.builder().member(memberA).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberC).save();
            reviewTestPersister.builder().member(memberC).save();

            List<Member> members = memberCustomRepository.getMembersSortByReviewCounts(1, 2);

            assertThat(members).containsExactly(memberA);
        }

        @Test
        void 페이지_개수만큼_리뷰_목록을_조회한다() {
            Member memberA = memberTestPersister.builder().save();
            Member memberB = memberTestPersister.builder().save();
            Member memberC = memberTestPersister.builder().save();
            reviewTestPersister.builder().member(memberA).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberB).save();
            reviewTestPersister.builder().member(memberC).save();
            reviewTestPersister.builder().member(memberC).save();

            List<Member> members = memberCustomRepository.getMembersSortByReviewCounts(0, 2);

            assertThat(members).containsExactly(memberB, memberC);
        }

        @Test
        void 리뷰를_작성하지_않은_회원은_조회되지_않는다() {
            Member member = memberTestPersister.builder().save();

            List<Member> members = memberCustomRepository.getMembersSortByReviewCounts(0, 2);

            assertThat(members).isEmpty();
        }
    }
}
