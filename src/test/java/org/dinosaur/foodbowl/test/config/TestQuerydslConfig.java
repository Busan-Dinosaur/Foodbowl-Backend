package org.dinosaur.foodbowl.test.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewCustomRepository;
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
    public ReviewCustomRepository reviewCustomRepository() {
        return new ReviewCustomRepository(jpaQueryFactory());
    }
}
