package org.dinosaur.foodbowl.domain.follow.persistence;

import static org.dinosaur.foodbowl.domain.follow.domain.QFollow.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.FollowerAndFollowingDto;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowerCountDto;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.QFollowerAndFollowingDto;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.QMemberFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FollowCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<MemberFollowerCountDto> findFollowerCountByMembers(List<Member> members) {
        return jpaQueryFactory.select(
                        new QMemberFollowerCountDto(
                                follow.following.id,
                                follow.count()
                        )
                )
                .from(follow)
                .where(follow.following.in(members))
                .groupBy(follow.following.id)
                .fetch();
    }

    public List<FollowerAndFollowingDto> findFollowingsByFollowingsAndFollower(List<Member> followings, Member member) {
        return jpaQueryFactory.select(
                        new QFollowerAndFollowingDto(
                                follow.follower.id,
                                follow.following.id
                        )
                )
                .from(follow)
                .where(
                        follow.follower.id.eq(member.getId()),
                        follow.following.in(followings)
                )
                .fetch();
    }
}
