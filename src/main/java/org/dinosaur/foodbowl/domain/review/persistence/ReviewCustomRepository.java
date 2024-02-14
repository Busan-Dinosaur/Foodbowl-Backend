package org.dinosaur.foodbowl.domain.review.persistence;

import static org.dinosaur.foodbowl.domain.bookmark.domain.QBookmark.bookmark;
import static org.dinosaur.foodbowl.domain.follow.domain.QFollow.follow;
import static org.dinosaur.foodbowl.domain.member.domain.QMember.member;
import static org.dinosaur.foodbowl.domain.review.domain.QReview.review;
import static org.dinosaur.foodbowl.domain.review.domain.QReviewPhoto.reviewPhoto;
import static org.dinosaur.foodbowl.domain.store.domain.QCategory.category;
import static org.dinosaur.foodbowl.domain.store.domain.QStore.store;
import static org.dinosaur.foodbowl.domain.store.domain.QStoreSchool.storeSchool;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.vo.ReviewFilter;
import org.dinosaur.foodbowl.domain.review.persistence.dto.QStoreReviewCountDto;
import org.dinosaur.foodbowl.domain.review.persistence.dto.StoreReviewCountDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<Review> findPaginationReviewsHavingPhoto(Long lastReviewId, int pageSize) {
        return jpaQueryFactory.selectDistinct(review)
                .from(review)
                .innerJoin(review.store, store).fetchJoin()
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(reviewPhoto).on(
                        review.id.eq(reviewPhoto.review.id)
                )
                .where(
                        ltLastReviewId(lastReviewId)
                )
                .orderBy(review.id.desc())
                .limit(pageSize)
                .fetch();
    }

    public List<StoreReviewCountDto> findReviewCountByStores(List<Store> stores) {
        return jpaQueryFactory.select(
                        new QStoreReviewCountDto(
                                store.id,
                                review.count()
                        )
                )
                .from(review)
                .innerJoin(review.store, store)
                .where(store.in(stores))
                .groupBy(store.id)
                .fetch();
    }

    public List<Review> findPaginationReviewsByMemberInMapBound(
            Long memberId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            CategoryType categoryType,
            int pageSize
    ) {
        return jpaQueryFactory.selectDistinct(review)
                .from(review)
                .innerJoin(review.store, store).fetchJoin()
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(store.category, category).fetchJoin()
                .where(
                        ltLastReviewId(lastReviewId),
                        member.id.eq(memberId),
                        containsPolygon(mapCoordinateBoundDto),
                        containsCategoryFilter(categoryType)
                )
                .orderBy(review.id.desc())
                .limit(pageSize)
                .fetch();
    }

    public List<Review> findPaginationReviewsByStore(
            Long storeId,
            ReviewFilter reviewFilter,
            Long memberId,
            Long lastReviewId,
            int pageSize
    ) {
        JPAQuery<Review> jpaQuery = jpaQueryFactory.select(review)
                .from(review)
                .innerJoin(review.member, member).fetchJoin();

        setReviewFilterIfExists(jpaQuery, reviewFilter);
        return jpaQuery.where(
                        applyReviewFilterIfExists(memberId, reviewFilter),
                        review.store.id.eq(storeId),
                        ltLastReviewId(lastReviewId)
                )
                .orderBy(review.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private void setReviewFilterIfExists(JPAQuery<Review> jpaQuery, ReviewFilter reviewFilter) {
        if (reviewFilter == ReviewFilter.ALL) {
            return;
        }
        jpaQuery.leftJoin(follow).on(
                review.member.id.eq(follow.following.id)
        );
    }

    private BooleanExpression applyReviewFilterIfExists(Long memberId, ReviewFilter reviewFilter) {
        if (reviewFilter == ReviewFilter.ALL) {
            return null;
        }
        return review.member.id.eq(memberId)
                .or(memberFollowingEq(memberId));
    }

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
            CategoryType categoryType,
            int pageSize
    ) {
        return jpaQueryFactory.selectDistinct(review)
                .from(review)
                .innerJoin(review.store, store).fetchJoin()
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(store.category, category).fetchJoin()
                .leftJoin(follow).on(
                        review.member.id.eq(follow.following.id)
                )
                .where(
                        review.member.id.eq(followerId)
                                .or(memberFollowingEq(followerId)),
                        ltLastReviewId(lastReviewId),
                        containsPolygon(mapCoordinateBoundDto),
                        containsCategoryFilter(categoryType)
                )
                .orderBy(review.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression memberFollowingEq(Long followerId) {
        return follow.follower.id.eq(followerId)
                .and(review.member.id.eq(follow.following.id));
    }

    public List<Review> findPaginationReviewsBySchoolInMapBounds(
            Long schoolId,
            Long lastReviewId,
            MapCoordinateBoundDto mapCoordinateBoundDto,
            CategoryType categoryType,
            int pageSize
    ) {
        return jpaQueryFactory.selectDistinct(review)
                .from(review)
                .innerJoin(review.store, store).fetchJoin()
                .innerJoin(review.member, member).fetchJoin()
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(storeSchool).on(
                        storeSchool.store.eq(review.store),
                        storeSchool.school.id.eq(schoolId)
                )
                .where(
                        ltLastReviewId(lastReviewId),
                        containsPolygon(mapCoordinateBoundDto),
                        containsCategoryFilter(categoryType)
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

    private BooleanExpression containsCategoryFilter(CategoryType categoryType) {
        if (categoryType == null) {
            return null;
        }
        return review.store.category.categoryType.eq(categoryType);
    }
}
