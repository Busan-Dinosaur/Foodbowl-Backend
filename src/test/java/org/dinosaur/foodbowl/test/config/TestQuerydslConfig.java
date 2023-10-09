package org.dinosaur.foodbowl.test.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowCustomRepository;
import org.dinosaur.foodbowl.domain.member.persistence.MemberCustomRepository;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewCustomRepository;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoCustomRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreCustomRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@TestConfiguration
public class TestQuerydslConfig {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public FollowCustomRepository followCustomRepository() {
        return new FollowCustomRepository(jpaQueryFactory());
    }

    @Bean
    public MemberCustomRepository memberCustomRepository() {
        return new MemberCustomRepository(jpaQueryFactory());
    }

    @Bean
    public ReviewCustomRepository reviewCustomRepository() {
        return new ReviewCustomRepository(jpaQueryFactory());
    }

    @Bean
    public ReviewPhotoCustomRepository reviewPhotoCustomRepository() {
        return new ReviewPhotoCustomRepository(jpaQueryFactory());
    }

    @Bean
    public StoreCustomRepository storeCustomRepository() {
        return new StoreCustomRepository(jpaQueryFactory());
    }
}
