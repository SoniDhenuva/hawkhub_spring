package com.open.spring.mvc.clubfeed;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // ── WHOAMI ────────────────────────────────────────────────────────────────

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami() {
        String uid = currentUid();
        if (uid == null) return ResponseEntity.ok(Map.of("uid", (Object) null));
        return ResponseEntity.ok(Map.of("uid", uid));
    }

    // ── GET FEED ──────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<FeedPostDTO>> getPosts(
            @RequestParam(required = false) String clubId,
            @RequestParam(required = false) String postType,
            @RequestParam(required = false) String search) {
        String uid = currentUid();
        return ResponseEntity.ok(service.findPostsAsDTO(clubId, postType, search, uid));
    }

    // ── GET SAVED POSTS ───────────────────────────────────────────────────────

    @GetMapping("/saved")
    public ResponseEntity<?> getSavedPosts() {
        String uid = currentUid();
        if (uid == null) return unauthorized();
        return ResponseEntity.ok(service.getSavedPostsDTO(uid));
    }

    // ── CREATE POST ───────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody ClubFeedPostService.CreateFeedPostRequest request) {
        String uid = currentUid();
        if (uid == null) return unauthorized();
        FeedPostDTO created = service.createFeedPost(request, uid);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── LIKE (TOGGLE) ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id) {
        String uid = currentUid();
        if (uid == null) return unauthorized();
        return ResponseEntity.ok(service.toggleLike(id, uid));
    }

    // ── SAVE (TOGGLE) ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/save")
    public ResponseEntity<?> toggleSave(@PathVariable Long id) {
        String uid = currentUid();
        if (uid == null) return unauthorized();
        return ResponseEntity.ok(service.toggleSave(id, uid));
    }

    // ── DELETE (owner only) ───────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        String uid = currentUid();
        if (uid == null) return unauthorized();
        boolean deleted = service.deletePost(id, uid);
        if (!deleted) return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Post not found or you are not the author"));
        return ResponseEntity.noContent().build();
    }

    // ── LEGACY LIKE (increment counter, kept for backward compat) ────────────

    @PostMapping("/{id}/likes")
    public ResponseEntity<?> likePost(@PathVariable Long id) {
        return service.likePost(id)
                .<ResponseEntity<?>>map(post -> ResponseEntity.ok(post))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Post not found: " + id)));
    }

    // ── VALIDATION HANDLER ────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError == null ? "Invalid request" : fieldError.getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private String currentUid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication required"));
    }

    // ── LEGACY REQUEST DTO (kept so existing code compiles) ───────────────────

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
