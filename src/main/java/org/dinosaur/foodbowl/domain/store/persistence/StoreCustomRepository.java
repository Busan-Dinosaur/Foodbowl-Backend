package org.dinosaur.foodbowl.domain.store.persistence;

import static org.dinosaur.foodbowl.domain.review.domain.QReview.review;
import static org.dinosaur.foodbowl.domain.store.domain.QStore.store;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
                                calculateDistance(x, y),
                                review.id.count()
                        )
                )
                .from(store)
                .leftJoin(review).on(review.store.id.eq(store.id))
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
}
