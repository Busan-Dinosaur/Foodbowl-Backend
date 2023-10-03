package org.dinosaur.foodbowl.domain.review.persistence;

import static org.dinosaur.foodbowl.domain.bookmark.domain.QBookmark.bookmark;
import static org.dinosaur.foodbowl.domain.follow.domain.QFollow.follow;
import static org.dinosaur.foodbowl.domain.member.domain.QMember.member;
import static org.dinosaur.foodbowl.domain.review.domain.QReview.review;
import static org.dinosaur.foodbowl.domain.store.domain.QCategory.category;
import static org.dinosaur.foodbowl.domain.store.domain.QStore.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Review> findPaginationReviewsByBookmarkInMapBounds(
            Long memberId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            int pageSize
    ) {
        return jpaQueryFactory.selectDistinct(review)
                .from(review)
                .innerJoin(review.store, store).fetchJoin()
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(bookmark).on(
                        bookmark.store.eq(review.store),
                        bookmark.member.id.eq(memberId)
                )
                .where(
                        ltLastReviewId(lastReviewId),
                        containsPolygon(mapCoordinateBoundDto)
                )
                .orderBy(review.id.desc())
                .limit(pageSize)
                .fetch();
    }

    public List<Review> findPaginationReviewsByFollowingInMapBounds(
            Long followerId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            int pageSize
    ) {
        return jpaQueryFactory.selectDistinct(review)
                .from(review)
                .innerJoin(review.store, store).fetchJoin()
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(follow).on(review.member.id.eq(follow.following.id))
                .where(
                        ltLastReviewId(lastReviewId),
                        follow.follower.id.eq(followerId),
                        containsPolygon(mapCoordinateBoundDto)
                )
                .orderBy(review.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltLastReviewId(Long lastReviewId) {
        if (lastReviewId == null) {
            return null;
        }
        return review.id.lt(lastReviewId);
    }

    private BooleanExpression containsPolygon(MapCoordinateBoundDto mapCoordinateBoundDto) {
        return Expressions.booleanTemplate(
                "ST_Contains({0}, {1})",
                convertTextToPolygon(mapCoordinateBoundDto),
                store.address.coordinate
        );
    }

    private StringExpression convertTextToPolygon(MapCoordinateBoundDto mapCoordinateBoundDto) {
        return Expressions.stringTemplate(
                "ST_GeomFromText({0}, {1})",
                getPolygon(mapCoordinateBoundDto),
                PointUtils.getSrId()
        );
    }

    private String getPolygon(MapCoordinateBoundDto mapCoordinateBoundDto) {
        String polygonTemplate = "POLYGON((%s %s, %s %s, %s %s, %s %s, %s %s))";
        return String.format(
                polygonTemplate,
                mapCoordinateBoundDto.downLeftPoint().getY(),
                mapCoordinateBoundDto.downLeftPoint().getX(),
                mapCoordinateBoundDto.downRightPoint().getY(),
                mapCoordinateBoundDto.downRightPoint().getX(),
                mapCoordinateBoundDto.topRightPoint().getY(),
                mapCoordinateBoundDto.topRightPoint().getX(),
                mapCoordinateBoundDto.topLeftPoint().getY(),
                mapCoordinateBoundDto.topLeftPoint().getX(),
                mapCoordinateBoundDto.downLeftPoint().getY(),
                mapCoordinateBoundDto.downLeftPoint().getX()
        );
    }
}
