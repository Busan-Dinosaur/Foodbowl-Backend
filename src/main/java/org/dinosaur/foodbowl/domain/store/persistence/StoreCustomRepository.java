package org.dinosaur.foodbowl.domain.store.persistence;

import static org.dinosaur.foodbowl.domain.bookmark.domain.QBookmark.bookmark;
import static org.dinosaur.foodbowl.domain.follow.domain.QFollow.follow;
import static org.dinosaur.foodbowl.domain.member.domain.QMember.member;
import static org.dinosaur.foodbowl.domain.review.domain.QReview.review;
import static org.dinosaur.foodbowl.domain.store.domain.QCategory.category;
import static org.dinosaur.foodbowl.domain.store.domain.QStore.store;
import static org.dinosaur.foodbowl.domain.store.domain.QStoreSchool.storeSchool;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.domain.store.dto.response.QStoreSearchResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponse;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class StoreCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<StoreSearchResponse> search(String name, double x, double y, int size) {
        return jpaQueryFactory.select(
                        new QStoreSearchResponse(
                                store.id,
                                store.storeName,
                                store.address.addressName,
                                store.category.categoryType.stringValue(),
                                calculateDistance(x, y),
                                review.id.count()
                        )
                )
                .from(store)
                .leftJoin(review).on(review.store.id.eq(store.id))
                .innerJoin(store.category, category)
                .where(store.storeName.contains(name))
                .groupBy(store.id)
                .orderBy(calculateDistance(x, y).asc())
                .limit(size)
                .offset(0)
                .fetch();
    }

    private NumberExpression<Double> calculateDistance(double x, double y) {
        StringTemplate point = Expressions.stringTemplate(
                String.format("ST_PointFromText('POINT(%s %s)', %s)", y, x, PointUtils.getSrId())
        );
        return Expressions.numberTemplate(
                Double.class,
                "ST_Distance_Sphere({0}, {1})",
                point,
                store.address.coordinate
        );
    }

    public List<Store> findStoresByInMapBounds(MapCoordinateBoundDto mapCoordinateBoundDto, CategoryType categoryType) {
        return jpaQueryFactory.selectDistinct(store)
                .from(store)
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(review).on(
                        review.store.eq(store)
                )
                .where(
                        containsPolygon(mapCoordinateBoundDto),
                        containsCategoryFilter(categoryType)
                )
                .fetch();
    }

    public List<Store> findStoresByMemberInMapBounds(Long memberId, MapCoordinateBoundDto mapCoordinateBoundDto) {
        return jpaQueryFactory.selectDistinct(store)
                .from(store)
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(review).on(
                        review.store.eq(store),
                        review.member.id.eq(memberId)
                )
                .where(containsPolygon(mapCoordinateBoundDto))
                .fetch();
    }

    public List<Store> findStoresByBookmarkInMapBounds(Long memberId, MapCoordinateBoundDto mapCoordinateBoundDto) {
        return jpaQueryFactory.selectDistinct(store)
                .from(store)
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(bookmark).on(
                        bookmark.store.eq(store),
                        bookmark.member.id.eq(memberId)
                )
                .where(containsPolygon(mapCoordinateBoundDto))
                .fetch();
    }

    public List<Store> findStoresByFollowingInMapBounds(Long memberId, MapCoordinateBoundDto mapCoordinateBoundDto) {
        return jpaQueryFactory.selectDistinct(store)
                .from(store)
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(review).on(review.store.eq(store))
                .innerJoin(member).on(review.member.eq(member))
                .leftJoin(follow).on(
                        review.member.id.eq(follow.following.id)
                )
                .where(
                        review.member.id.eq(memberId)
                                .or(follow.follower.id.eq(memberId)),
                        containsPolygon(mapCoordinateBoundDto)
                )
                .fetch();
    }

    public List<Store> findStoresBySchoolInMapBounds(Long schoolId, MapCoordinateBoundDto mapCoordinateBoundDto) {
        return jpaQueryFactory.selectDistinct(store)
                .from(store)
                .innerJoin(store.category, category).fetchJoin()
                .innerJoin(storeSchool).on(
                        storeSchool.store.eq(store),
                        storeSchool.school.id.eq(schoolId)
                )
                .where(containsPolygon(mapCoordinateBoundDto))
                .fetch();
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
        return store.category.categoryType.eq(categoryType);
    }
}
