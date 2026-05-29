package com.open.spring.mvc.clubfeed;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@RestController
@RequestMapping("/api/club-feed/posts")
public class ClubFeedPostController {
    private final ClubFeedPostService service;

    public ClubFeedPostController(ClubFeedPostService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ClubFeedPost>> getPosts(@RequestParam(required = false) String clubId) {
        return new ResponseEntity<>(service.findPosts(clubId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClubFeedPost> createPost(@Valid @RequestBody ClubFeedPostRequest request) {
        ClubFeedPost post = new ClubFeedPost();
        post.setClubId(request.getClubId().trim());
        post.setTitle(request.getTitle().trim());
        post.setDescription(request.getDescription().trim());
        post.setImageUrl(request.getImageUrl());
        post.setTopicPath(request.getTopicPath());

        return new ResponseEntity<>(service.createPost(post), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<?> likePost(@PathVariable Long id) {
        return service.likePost(id)
                .<ResponseEntity<?>>map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(
                        Map.of("error", "Club feed post not found for id: " + id),
                        HttpStatus.NOT_FOUND
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError == null ? "Invalid club feed post" : fieldError.getDefaultMessage();
        return new ResponseEntity<>(Map.of("error", message), HttpStatus.BAD_REQUEST);
    }

    @Data
    public static class ClubFeedPostRequest {
        @NotBlank(message = "clubId is required")
        private String clubId;

        @NotBlank(message = "title is required")
        private String title;

        @NotBlank(message = "description is required")
        private String description;

        private String imageUrl;
        private String topicPath;
    }
}
