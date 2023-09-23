package org.dinosaur.foodbowl.domain.blame.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.blame.domain.Blame;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class BlameRepositoryTest extends PersistenceTest {

    @Autowired
    private BlameRepository blameRepository;

    @Nested
    class 신고자와_타겟ID와_타겟대상으로_조회_시 {

        @Test
        void 존재한다면_신고를_반환한다() {
            Member member = memberTestPersister.builder().save();
            Member target = memberTestPersister.builder().save();
            blameTestPersister.builder().member(member).targetId(target.getId()).blameTarget(BlameTarget.MEMBER).save();

            Optional<Blame> blame =
                    blameRepository.findByMemberAndTargetIdAndBlameTarget(member, target.getId(), BlameTarget.MEMBER);

            assertThat(blame).isPresent();
        }

        @Test
        void 신고자만_달라도_조회되지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member target = memberTestPersister.builder().save();
            blameTestPersister.builder().targetId(target.getId()).blameTarget(BlameTarget.MEMBER).save();

            Optional<Blame> blame =
                    blameRepository.findByMemberAndTargetIdAndBlameTarget(member, target.getId(), BlameTarget.MEMBER);

            assertThat(blame).isNotPresent();
        }

        @Test
        void 타겟ID만_달라도_조회되지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member target = memberTestPersister.builder().save();
            blameTestPersister.builder().targetId(target.getId()).blameTarget(BlameTarget.MEMBER).save();

            Long otherTargetId = target.getId() + 1;
            Optional<Blame> blame =
                    blameRepository.findByMemberAndTargetIdAndBlameTarget(member, otherTargetId, BlameTarget.MEMBER);

            assertThat(blame).isNotPresent();
        }

        @Test
        void 타겟대상만_달라도_조회되지_않는다() {
            Member member = memberTestPersister.builder().save();
            Member target = memberTestPersister.builder().save();
            blameTestPersister.builder().targetId(target.getId()).blameTarget(BlameTarget.MEMBER).save();

            Optional<Blame> blame =
                    blameRepository.findByMemberAndTargetIdAndBlameTarget(member, target.getId(), BlameTarget.REVIEW);

            assertThat(blame).isNotPresent();
        }
    }
}
