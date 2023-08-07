package org.dinosaur.foodbowl.domain.member.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberRepositoryTest extends PersistenceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_저장() {
        Member member = Member.builder()
                .email("foodBowl@gmail.com")
                .socialId("foodBowlId")
                .socialType(SocialType.APPLE)
                .nickname("foodbowl")
                .introduction("안녕하세요")
                .build();

        Member saveMember = memberRepository.save(member);

        assertThat(saveMember.getId()).isNotNull();
    }

    @Test
    void 아이디와_일치하는_회원_조회() {
        Member member = Member.builder()
                .email("foodBowl@gmail.com")
                .socialId("foodBowlId")
                .socialType(SocialType.APPLE)
                .nickname("foodbowl")
                .introduction("안녕하세요")
                .build();
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(saveMember.getId());
    }
}
