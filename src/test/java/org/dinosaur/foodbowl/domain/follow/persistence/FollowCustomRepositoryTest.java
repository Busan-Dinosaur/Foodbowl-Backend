package org.dinosaur.foodbowl.domain.follow.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowCustomRepositoryTest extends PersistenceTest {

    @Autowired
    private FollowCustomRepositoryImpl followCustomRepository;

    @Test
    void 멤버_목록에_존재하는_멤버의_팔로우_수를_조회한다() {
        Member memberA = memberTestPersister.memberBuilder().save();
        Member memberB = memberTestPersister.memberBuilder().save();
        Member memberC = memberTestPersister.memberBuilder().save();
        followTestPersister.builder().following(memberA).follower(memberB).save();
        followTestPersister.builder().following(memberA).follower(memberC).save();
        followTestPersister.builder().following(memberB).follower(memberC).save();

        List<MemberFollowerCountDto> result =
                followCustomRepository.getFollowerCountByMembers(List.of(memberA, memberB));

        List<MemberFollowerCountDto> expected = List.of(
                new MemberFollowerCountDto(memberA.getId(), 2),
                new MemberFollowerCountDto(memberB.getId(), 1)
        );
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }
}
