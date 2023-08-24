package org.dinosaur.foodbowl.domain.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberRoleTest {

    @Test
    void 멤버_역할을_생성한다() {
        Member member = Member.builder()
                .socialType(SocialType.APPLE)
                .socialId("1")
                .email("email@email.com")
                .nickname("hello")
                .introduction("hello world")
                .build();
        Role role = Role.builder()
                .id(1L)
                .roleType(RoleType.ROLE_회원)
                .build();
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();

        assertThat(memberRole.getMember()).isEqualTo(member);
    }
}
