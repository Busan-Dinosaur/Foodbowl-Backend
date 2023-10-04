package org.dinosaur.foodbowl.domain.follow.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.FollowerAndFollowingDto;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private FollowCustomRepository followCustomRepository;

    @Test
    void 멤버_목록에_존재하는_멤버의_팔로우_수를_조회한다() {
        Member memberA = memberTestPersister.builder().save();
        Member memberB = memberTestPersister.builder().save();
        Member memberC = memberTestPersister.builder().save();
        followTestPersister.builder().following(memberA).follower(memberB).save();
        followTestPersister.builder().following(memberA).follower(memberC).save();
        followTestPersister.builder().following(memberB).follower(memberC).save();

        List<MemberFollowerCountDto> result =
                followCustomRepository.findFollowerCountByMembers(List.of(memberA, memberB));

        List<MemberFollowerCountDto> expected = List.of(
                new MemberFollowerCountDto(memberA.getId(), 2),
                new MemberFollowerCountDto(memberB.getId(), 1)
        );
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void 사용자가_여러_사용자_중_팔로잉_하는_회원을_조회한다() {
        Member gray = memberTestPersister.builder().save();
        Member memberA = memberTestPersister.builder().save();
        Member memberB = memberTestPersister.builder().save();
        Member memberC = memberTestPersister.builder().save();
        Member memberD = memberTestPersister.builder().save();
        followTestPersister.builder().follower(gray).following(memberA).save();
        followTestPersister.builder().follower(gray).following(memberC).save();
        followTestPersister.builder().follower(gray).following(memberD).save();
        List<Member> members = List.of(memberA, memberB, memberC, memberD);

        List<FollowerAndFollowingDto> results = followCustomRepository.findFollowingsByFollowingsAndFollower(
                members, gray);

        assertSoftly(softly -> {
            softly.assertThat(results).hasSize(3);
            softly.assertThat(results.contains(new FollowerAndFollowingDto(gray.getId(), memberA.getId()))).isTrue();
            softly.assertThat(results.contains(new FollowerAndFollowingDto(gray.getId(), memberB.getId()))).isFalse();
            softly.assertThat(results.contains(new FollowerAndFollowingDto(gray.getId(), memberC.getId()))).isTrue();
            softly.assertThat(results.contains(new FollowerAndFollowingDto(gray.getId(), memberD.getId()))).isTrue();
        });
    }
}
