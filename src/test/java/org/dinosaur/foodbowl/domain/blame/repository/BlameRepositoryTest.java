package org.dinosaur.foodbowl.domain.blame.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.blame.entity.Blame.BlameTarget;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class BlameRepositoryTest extends RepositoryTest {

    @Autowired
    private BlameRepository blameRepository;

    @Test
    @DisplayName("멤버가 신고한 목록을 삭제한다.")
    void deleteAllByMember() {
        Member member = memberTestSupport.memberBuilder().build();
        blameTestSupport.builder().member(member).build();

        blameRepository.deleteAllByMember(member);

        assertThat(blameRepository.findAllByMember(member)).isEmpty();
    }

    @Nested
    @DisplayName("deleteAllByTargetIdAndBlameTarget 메서드는 ")
    class DeleteAllByTargetIdAndBlameTarget {

        @Test
        @DisplayName("신고 대상과 타켓 ID에 알맞은 모든 신고를 삭제한다.")
        void deleteAllByTargetIdAndBlameTarget() {
            blameTestSupport.builder()
                    .targetId(1L)
                    .blameTarget(BlameTarget.REVIEW)
                    .build();
            blameTestSupport.builder()
                    .targetId(1L)
                    .blameTarget(BlameTarget.REVIEW)
                    .build();

            blameRepository.deleteAllByTargetIdAndBlameTarget(1L, BlameTarget.REVIEW);

            assertThat(blameRepository.findAll()).isEmpty();
        }
    }
}
