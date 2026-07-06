package com.open.spring.mvc.recommendation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, Long> {
    Optional<UserRecommendation> findFirstByUidOrderByIdDesc(String uid);
}
