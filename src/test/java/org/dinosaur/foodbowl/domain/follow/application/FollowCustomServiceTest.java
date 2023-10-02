package org.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowCustomServiceTest extends IntegrationTest {

    @Autowired
    private FollowCustomService followCustomService;

    @Test
    void 멤버_별_팔로워_수를_집계한다() {
        Member memberA = memberTestPersister.builder().save();
        Member memberB = memberTestPersister.builder().save();
        followTestPersister.builder().following(memberA).follower(memberB).save();
        followTestPersister.builder().following(memberB).follower(memberA).save();

        MemberToFollowerCountDto result = followCustomService.getFollowerCountByMembers(List.of(memberA, memberB));

        assertSoftly(softly -> {
            softly.assertThat(result.getFollowCount(memberA.getId())).isEqualTo(1);
            softly.assertThat(result.getFollowCount(memberB.getId())).isEqualTo(1);
        });
    }
}
