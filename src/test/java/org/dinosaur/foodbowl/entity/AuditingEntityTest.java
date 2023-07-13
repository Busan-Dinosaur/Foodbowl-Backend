package org.dinosaur.foodbowl.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuditingEntityTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("생성 시간과 수정 시간이 존재하는 엔티티를 저장한다.")
    void saveEntityWithCreatedTimeAndUpdatedTime() {
        Member member = Member.builder()
                .socialType(Member.SocialType.APPLE)
                .socialId("abc")
                .nickname("foodbowl")
                .build();

        Member savedMember = memberRepository.save(member);

        assertThat(savedMember.getCreatedAt()).isNotNull();
        assertThat(savedMember.getUpdatedAt()).isNotNull();
    }
}
