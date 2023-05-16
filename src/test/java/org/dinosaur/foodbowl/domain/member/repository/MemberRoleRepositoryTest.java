package org.dinosaur.foodbowl.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberRoleRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    @Test
    @DisplayName("멤버의 모든 역할을 삭제한다.")
    void deleteAllByMember() {
        Member member = memberTestSupport.memberBuilder().build();
        memberTestSupport.memberRoleBuilder().member(member).build();

        memberRoleRepository.deleteAllByMember(member);

        assertThat(memberRoleRepository.findAllByMember(member)).isEmpty();
    }
}
