package org.dinosaur.foodbowl.domain.member.persistence;

import static org.dinosaur.foodbowl.domain.member.domain.QMember.member;
import static org.dinosaur.foodbowl.domain.review.domain.QReview.review;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MemberCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Member> search(String name, int size) {
        NumberExpression<Integer> nameSort = new CaseBuilder()
                .when(member.nickname.value.eq(name)).then(1)
                .otherwise(2);

        return jpaQueryFactory.select(member)
                .from(member)
                .where(member.nickname.value.startsWith(name))
                .orderBy(nameSort.asc())
                .limit(size)
                .offset(0)
                .fetch();
    }

    public List<Member> getMembersSortByReviewCounts(int page, int size) {
        return jpaQueryFactory.select(member)
                .from(review)
                .innerJoin(review.member, member)
                .groupBy(member.id)
                .orderBy(review.count().desc())
                .limit(size)
                .offset((long) page * size)
                .fetch();
    }
}
