package org.dinosaur.foodbowl.domain.member.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberRepositoryTest extends PersistenceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 아이디와_일치하는_회원_조회() {
        Member saveMember = memberTestPersister.memberBuilder().save();

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(saveMember.getId());
    }

    @Test
    void 썸네일과_함께_회원_조회() {
        Member member = memberTestPersister.memberBuilder().save();
        memberTestPersister.memberThumbnailBuilder().member(member).save();

        Optional<Member> findMember = memberRepository.findByIdWithThumbnail(member.getId());

        assertThat(findMember.get()).isEqualTo(member);
    }

    @Nested
    class 소셜아이디와_소셜타입으로_조회 {

        @Test
        void 일치하는_회원이_존재하면_회원을_조회한다() {
            SocialType socialType = SocialType.APPLE;
            String socialId = "abcd";
            memberTestPersister.memberBuilder()
                    .socialType(socialType)
                    .socialId(socialId)
                    .save();

            assertThat(memberRepository.findBySocialTypeAndSocialId(socialType, socialId)).isPresent();
        }

        @Test
        void 일치하는_회원이_존재하지_않으면_회원이_조회되지_않는다() {
            Optional<Member> findMember = memberRepository.findBySocialTypeAndSocialId(SocialType.APPLE, "abcd");

            assertThat(findMember).isEmpty();
        }
    }

    @Nested
    class 닉네임_존재_여부_확인 {

        @Test
        void 존재하는_닉네임이라면_true_반환한다() {
            Member saveMember = memberTestPersister.memberBuilder()
                    .nickname("hello")
                    .save();

            boolean result = memberRepository.existsByNickname(new Nickname(saveMember.getNickname()));

            assertThat(result).isTrue();
        }

        @Test
        void 존재하지_않는_닉네임이라면_false_반환한다() {
            boolean result = memberRepository.existsByNickname(new Nickname("hello"));

            assertThat(result).isFalse();
        }
    }

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
}
