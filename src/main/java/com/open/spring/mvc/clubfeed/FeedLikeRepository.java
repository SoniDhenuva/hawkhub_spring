package com.open.spring.mvc.clubfeed;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    Optional<FeedLike> findByPostIdAndUserUid(Long postId, String userUid);
    int countByPostId(Long postId);

    @Transactional
    void deleteByPostIdAndUserUid(Long postId, String userUid);

    @Transactional
    void deleteByPostId(Long postId);
}
