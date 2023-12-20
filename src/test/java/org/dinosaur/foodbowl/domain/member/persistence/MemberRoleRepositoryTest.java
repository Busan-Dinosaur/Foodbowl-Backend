package org.dinosaur.foodbowl.domain.member.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.MemberRole;
import org.dinosaur.foodbowl.domain.member.domain.Role;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class MemberRoleRepositoryTest extends PersistenceTest {

    @Autowired
    private MemberRoleRepository memberRoleRepository;

    @Test
    void 멤버_역할을_모두_조회한다() {
        Member member = memberTestPersister.builder().save();
        Role role = Role.builder()
                .id(RoleType.ROLE_회원.getId())
                .roleType(RoleType.ROLE_회원)
                .build();
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();
        MemberRole savedMemberRole = memberRoleRepository.save(memberRole);

        List<MemberRole> memberRoles = memberRoleRepository.findAllByMember(member);

        assertThat(memberRoles).containsExactly(savedMemberRole);
    }

    @Test
    void 멤버_역할을_저장한다() {
        Member member = memberTestPersister.builder().save();
        Role role = Role.builder()
                .id(RoleType.ROLE_회원.getId())
                .roleType(RoleType.ROLE_회원)
                .build();
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();

        MemberRole savedMemberRole = memberRoleRepository.save(memberRole);

        assertSoftly(softly -> {
            softly.assertThat(savedMemberRole.getId()).isNotNull();
            softly.assertThat(savedMemberRole.getMember()).isEqualTo(member);
            softly.assertThat(savedMemberRole.getRole()).isEqualTo(role);
        });
    }

    @Test
    void 멤버_역할을_삭제한다() {
        Member member = memberTestPersister.builder().save();
        Role role = Role.builder()
                .id(RoleType.ROLE_회원.getId())
                .roleType(RoleType.ROLE_회원)
                .build();
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();
        memberRoleRepository.save(memberRole);

        memberRoleRepository.deleteByMember(member);

        assertThat(memberRoleRepository.findAllByMember(member)).isEmpty();
    }
}
