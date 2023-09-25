package org.dinosaur.foodbowl.domain.follow.persistence;

import static org.dinosaur.foodbowl.domain.follow.domain.QFollow.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowCountDto;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.QMemberFollowCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FollowCustomRepositoryImpl implements FollowCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MemberFollowCountDto> getFollowCountByMembers(List<Member> members) {
        return jpaQueryFactory.select(new QMemberFollowCountDto(
                        follow.following.id,
                        follow.count()
                ))
                .from(follow)
                .where(follow.following.in(members))
                .groupBy(follow.following.id)
                .fetch();
    }
}
