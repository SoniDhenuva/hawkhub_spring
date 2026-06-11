package com.open.spring.mvc.clubfeed;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FeedPostImageRepository extends JpaRepository<FeedPostImage, Long> {
    List<FeedPostImage> findByPostIdOrderByDisplayOrder(Long postId);

    @Transactional
    void deleteByPostId(Long postId);
}
