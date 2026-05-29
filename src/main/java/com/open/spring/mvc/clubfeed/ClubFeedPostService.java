package com.open.spring.mvc.clubfeed;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ClubFeedPostService {
    private final ClubFeedPostRepository repository;

    public ClubFeedPostService(ClubFeedPostRepository repository) {
        this.repository = repository;
    }

    public List<ClubFeedPost> findPosts(String clubId) {
        if (clubId == null || clubId.isBlank()) {
            return repository.findAllByOrderByCreatedAtDesc();
        }

        return repository.findByClubIdOrderByCreatedAtDesc(clubId);
    }

    public ClubFeedPost createPost(ClubFeedPost post) {
        post.setId(null);
        post.setLikes(0);
        post.setCreatedAt(Instant.now());
        return repository.save(post);
    }

    public Optional<ClubFeedPost> likePost(Long id) {
        return repository.findById(id)
                .map(post -> {
                    post.setLikes(post.getLikes() + 1);
                    return repository.save(post);
                });
    }
}
