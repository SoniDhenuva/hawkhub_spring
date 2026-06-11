package com.open.spring.mvc.clubfeed;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubFeedPostRepository extends JpaRepository<ClubFeedPost, Long> {
    List<ClubFeedPost> findAllByOrderByCreatedAtDesc();
    List<ClubFeedPost> findByClubIdOrderByCreatedAtDesc(String clubId);
    List<ClubFeedPost> findByPostTypeOrderByCreatedAtDesc(String postType);
}
