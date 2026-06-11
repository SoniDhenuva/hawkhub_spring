package com.open.spring.mvc.clubfeed;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FeedSaveRepository extends JpaRepository<FeedSave, Long> {
    Optional<FeedSave> findByPostIdAndUserUid(Long postId, String userUid);
    int countByPostId(Long postId);
    List<FeedSave> findByUserUidOrderByIdDesc(String userUid);

    @Transactional
    void deleteByPostIdAndUserUid(Long postId, String userUid);

    @Transactional
    void deleteByPostId(Long postId);
}
