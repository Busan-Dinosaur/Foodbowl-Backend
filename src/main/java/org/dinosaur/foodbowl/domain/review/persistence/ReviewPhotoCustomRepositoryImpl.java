package org.dinosaur.foodbowl.domain.review.persistence;

import static org.dinosaur.foodbowl.domain.follow.domain.QFollow.follow;
import static org.dinosaur.foodbowl.domain.member.domain.QMember.member;
import static org.dinosaur.foodbowl.domain.review.domain.QReview.review;
import static org.dinosaur.foodbowl.domain.review.domain.QReviewPhoto.reviewPhoto;
import static org.dinosaur.foodbowl.domain.store.domain.QCategory.category;
import static org.dinosaur.foodbowl.domain.store.domain.QStore.store;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.CoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewPhotoCustomRepositoryImpl implements ReviewPhotoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Review> getPaginationReviewsByFollowing(
            Long memberId,
            Long lastReviewId,
            CoordinateBoundDto coordinateBoundDto,
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
                        follow.follower.id.eq(memberId),
                        containsPolygon(coordinateBoundDto)
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

    private BooleanExpression containsPolygon(CoordinateBoundDto coordinateBoundDto) {
        return Expressions.booleanTemplate(
                "ST_Contains({0}, {1})",
                convertTextToPolygon(coordinateBoundDto),
                store.address.coordinate
        );
    }

    private StringExpression convertTextToPolygon(CoordinateBoundDto coordinateBoundDto) {
        return Expressions.stringTemplate(
                "ST_GeomFromText({0}, {1})",
                getPolygon(coordinateBoundDto),
                PointUtils.getSrId()
        );
    }

    private String getPolygon(CoordinateBoundDto coordinateBoundDto) {
        String polygonTemplate = "POLYGON((%s %s, %s %s, %s %s, %s %s, %s %s))";
        return String.format(
                polygonTemplate,
                coordinateBoundDto.downLeftPoint().getY(),
                coordinateBoundDto.downLeftPoint().getX(),
                coordinateBoundDto.downRightPoint().getY(),
                coordinateBoundDto.downRightPoint().getX(),
                coordinateBoundDto.topRightPoint().getY(),
                coordinateBoundDto.topRightPoint().getX(),
                coordinateBoundDto.topLeftPoint().getY(),
                coordinateBoundDto.topLeftPoint().getX(),
                coordinateBoundDto.downLeftPoint().getY(),
                coordinateBoundDto.downLeftPoint().getX()
        );
    }

    @Override
    public long deleteAllByReview(Review review) {
        return jpaQueryFactory.delete(reviewPhoto)
                .where(reviewPhoto.review.eq(review))
                .execute();
    }

    @Override
    public long deleteByReviewAndPhotos(Review review, List<Photo> photos) {
        return jpaQueryFactory.delete(reviewPhoto)
                .where(reviewPhoto.review.eq(review).and(reviewPhoto.photo.in(photos)))
                .execute();
    }
}
