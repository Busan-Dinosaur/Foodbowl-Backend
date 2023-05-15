package org.dinosaur.foodbowl.domain.member.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameDuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("checkDuplicate 메서드는")
    class checkDuplicate {

        @Test
        @DisplayName("닉네임과 일치하는 회원이 존재하면 true를 반환한다.")
        void checkDuplicateMember() {
            String nickname = "gray";
            Member member = Member.builder()
                    .socialType(Member.SocialType.APPLE)
                    .socialId("1234")
                    .nickname(nickname)
                    .build();
            memberRepository.save(member);

            NicknameDuplicateCheckResponse nicknameDuplicateCheckResponse = memberService.checkDuplicate(nickname);

            assertThat(nicknameDuplicateCheckResponse.isHasDuplicate()).isTrue();
        }

        @Test
        @DisplayName("닉네임과 일치하는 회원이 존재하면 false를 반환한다.")
        void checkNoneDuplicateMember() {
            String nickname = "gray";
            Member member = Member.builder()
                    .socialType(Member.SocialType.APPLE)
                    .socialId("1234")
                    .nickname("dazzle")
                    .build();
            memberRepository.save(member);

            NicknameDuplicateCheckResponse nicknameDuplicateCheckResponse = memberService.checkDuplicate(nickname);

            assertThat(nicknameDuplicateCheckResponse.isHasDuplicate()).isFalse();
        }
    }

}
