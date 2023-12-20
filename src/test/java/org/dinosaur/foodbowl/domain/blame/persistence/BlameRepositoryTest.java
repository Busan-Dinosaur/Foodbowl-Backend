package org.dinosaur.foodbowl.domain.blame.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

    @Nested
    class 멤버의_신고_데이터_삭제_시 {

        @Test
        void 신고_목록을_삭제한다() {
            Member member = memberTestPersister.builder().save();
            blameTestPersister.builder().member(member).targetId(1L).blameTarget(BlameTarget.REVIEW).save();
            blameTestPersister.builder().member(member).targetId(2L).blameTarget(BlameTarget.MEMBER).save();

            blameRepository.deleteByMember(member.getId(), BlameTarget.MEMBER);

            assertSoftly(softly -> {
                softly.assertThat(blameRepository.findByMemberAndTargetIdAndBlameTarget(member, 1L, BlameTarget.REVIEW))
                        .isNotPresent();
                softly.assertThat(blameRepository.findByMemberAndTargetIdAndBlameTarget(member, 2L, BlameTarget.MEMBER))
                        .isNotPresent();
            });
        }

        @Test
        void 신고_당한_목록을_삭제한다() {
            Member member = memberTestPersister.builder().save();
            Member blamerA = memberTestPersister.builder().save();
            Member blamerB = memberTestPersister.builder().save();
            blameTestPersister.builder().member(blamerA).targetId(member.getId()).blameTarget(BlameTarget.MEMBER);
            blameTestPersister.builder().member(blamerB).targetId(member.getId()).blameTarget(BlameTarget.MEMBER);

            blameRepository.deleteByMember(member.getId(), BlameTarget.MEMBER);

            assertSoftly(softly -> {
                softly.assertThat(
                        blameRepository.findByMemberAndTargetIdAndBlameTarget(
                                blamerA,
                                member.getId(),
                                BlameTarget.MEMBER
                        )
                ).isNotPresent();
                softly.assertThat(
                        blameRepository.findByMemberAndTargetIdAndBlameTarget(
                                blamerB,
                                member.getId(),
                                BlameTarget.MEMBER
                        )
                ).isNotPresent();
            });
        }
    }
}
