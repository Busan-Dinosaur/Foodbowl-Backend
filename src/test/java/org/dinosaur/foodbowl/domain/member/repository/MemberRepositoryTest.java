package org.dinosaur.foodbowl.domain.member.repository;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.entity.Member.SocialType;
import static org.dinosaur.foodbowl.domain.member.entity.Member.builder;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("findBySocialTypeAndSocialId 메서드는 ")
    class FindBySocialTypeAndSocialId {

        @BeforeEach
        void setUp() {
            Member member = builder()
                    .socialType(SocialType.APPLE)
                    .socialId("1234")
                    .nickname("member1234")
                    .build();
            memberRepository.save(member);
        }

        @Test
        @DisplayName("소셜 타입과 소셜 ID에 일치하는 멤버가 존재하면 해당 멤버를 가져온다.")
        void getMember() {
            Optional<Member> findMember = memberRepository.findBySocialTypeAndSocialId(SocialType.APPLE, "1234");

            assertAll(
                    () -> assertThat(findMember).isNotEmpty(),
                    () -> assertThat(findMember.get().getSocialType()).isEqualTo(SocialType.APPLE),
                    () -> assertThat(findMember.get().getSocialId()).isEqualTo("1234"),
                    () -> assertThat(findMember.get().getNickname()).isEqualTo("member1234"),
                    () -> assertThat(findMember.get().getCreatedAt()).isNotNull(),
                    () -> assertThat(findMember.get().getUpdatedAt()).isNotNull()
            );
        }

        @Test
        @DisplayName("소셜 타입과 소셜 ID에 일치하는 멤버가 존재하지 않으면 가져오지 않는다.")
        void getNoMember() {
            Optional<Member> findMember = memberRepository.findBySocialTypeAndSocialId(SocialType.APPLE, "9876");

            assertThat(findMember).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByNickname 메서드는 ")
    class FindByNickname {

        @BeforeEach
        void setUp() {
            Member member = builder()
                    .socialType(SocialType.APPLE)
                    .socialId("1234")
                    .nickname("gray")
                    .build();
            memberRepository.save(member);
        }

        @Test
        @DisplayName("일치하는 닉네임이 존재하는 경우 회원을 가져온다.")
        void getMember() {
            String nickName = "gray";

            assertThat(memberRepository.findByNickname(nickName).isPresent()).isTrue();
        }

        @Test
        @DisplayName("일치하는 닉네임이 존재하지 않는 경우 회원을 가져오지 않는다.")
        void getNoMember() {
            String nickName = "hoy";

            assertThat(memberRepository.findByNickname(nickName).isPresent()).isFalse();
        }
    }
}
