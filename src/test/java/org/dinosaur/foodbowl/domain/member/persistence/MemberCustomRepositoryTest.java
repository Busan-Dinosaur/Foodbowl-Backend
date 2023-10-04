package org.dinosaur.foodbowl.domain.member.persistence;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.PersistenceTest;
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
}
