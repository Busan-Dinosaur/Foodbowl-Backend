package org.dinosaur.foodbowl.domain.blame.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.blame.entity.Blame.BlameTarget;
import org.dinosaur.foodbowl.testsupport.BlameTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class BlameRepositoryTest extends RepositoryTest {

    @Autowired
    private BlameTestSupport blameTestSupport;
    @Autowired
    private BlameRepository blameRepository;

    @Nested
    @DisplayName("deleteAllByTargetIdAndBlameTarget 메서드는 ")
    class DeleteAllByTargetIdAndBlameTarget {

        @Test
        @DisplayName("신고 대상과 타켓 ID에 알맞은 모든 신고를 삭제한다.")
        void deleteAllByTargetIdAndBlameTarget() {
            blameTestSupport.builder()
                    .targetId(1L)
                    .blameTarget(BlameTarget.POST)
                    .build();
            blameTestSupport.builder()
                    .targetId(1L)
                    .blameTarget(BlameTarget.POST)
                    .build();

            blameRepository.deleteAllByTargetIdAndBlameTarget(1L, BlameTarget.POST);

            assertThat(blameRepository.findAll()).isEmpty();
        }

        @ParameterizedTest
        @CsvSource(value = {"1,MEMBER", "2,POST", "2,COMMENT"})
        @DisplayName("신고 대상과 타겟 ID에 맞지 않으면 삭제하지 않는다.")
        void deleteAllByTargetIdAndBlameTargetWithNotMatch(Long targetId, BlameTarget blameTarget) {
            blameTestSupport.builder()
                    .targetId(1L)
                    .blameTarget(BlameTarget.POST)
                    .build();

            blameRepository.deleteAllByTargetIdAndBlameTarget(targetId, blameTarget);

            assertThat(blameRepository.findAll()).hasSize(1);
        }
    }
}
