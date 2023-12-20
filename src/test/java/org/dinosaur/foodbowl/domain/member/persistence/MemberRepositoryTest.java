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
    void ID에_일치하는_회원을_조회한다() {
        Member saveMember = memberTestPersister.builder().save();

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember).isEqualTo(saveMember);
    }

    @Test
    void ID에_일치하는_회원을_썸네일과_함께_조회한다() {
        Member member = memberTestPersister.builder().save();
        memberThumbnailTestPersister.builder().member(member).save();

        Member findMember = memberRepository.findByIdWithThumbnail(member.getId()).get();

        assertThat(findMember).isEqualTo(member);
    }

    @Nested
    class 소셜_아이디와_소셜_타입으로_조회_시 {

        @Test
        void 일치하는_회원이_존재하면_회원을_조회한다() {
            SocialType socialType = SocialType.APPLE;
            String socialId = "abcd";
            memberTestPersister.builder()
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
    class 닉네임_존재_여부_조회_시 {

        @Test
        void 존재하는_닉네임이라면_true_반환한다() {
            Member saveMember = memberTestPersister.builder()
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
    void 회원을_저장한다() {
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
    void 회원을_삭제한다() {
        Member member = Member.builder()
                .email("foodBowl@gmail.com")
                .socialId("foodBowlId")
                .socialType(SocialType.APPLE)
                .nickname("foodbowl")
                .introduction("안녕하세요")
                .build();
        Member saveMember = memberRepository.save(member);

        memberRepository.delete(saveMember);

        assertThat(memberRepository.findById(saveMember.getId())).isEmpty();
    }
}
