package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberCustomServiceTest extends IntegrationTest {

    @Autowired
    private MemberCustomService memberCustomService;

    @Test
    void 검색어를_이용한_회원_검색_결과를_가져온다() {
        String name = "dazzle";
        Member memberA = memberTestPersister.builder().nickname("dazzle").save();
        Member memberB = memberTestPersister.builder().nickname("dazzleAbc").save();
        Member memberC = memberTestPersister.builder().nickname("dazzle1234").save();
        Member memberD = memberTestPersister.builder().nickname("1234dazzle").save();

        List<Member> members = memberCustomService.search(name, 20);

        assertSoftly(softly -> {
            softly.assertThat(members).containsExactly(memberA, memberC, memberB);
            softly.assertThat(members).doesNotContain(memberD);
        });
    }
}
