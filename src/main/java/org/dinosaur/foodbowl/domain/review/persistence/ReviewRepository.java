package org.dinosaur.foodbowl.domain.review.persistence;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends Repository<Review, Long> {

    Optional<Review> findById(Long id);

    @EntityGraph(attributePaths = {"member", "store"})
    Optional<Review> findWithStoreAndMemberById(Long id);

    List<Review> findAllByMember(Member member);

    Review save(Review review);

    void delete(Review review);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Review r where r.member = :member")
    void deleteByMember(@Param("member") Member member);
}
