package com.open.spring.mvc.recommendation;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/recommendations")
public class UserRecommendationApiController {

    private final UserRecommendationRepository repo;
    private final ObjectMapper objectMapper;

    public UserRecommendationApiController(UserRecommendationRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    // Save (upsert) recommendations for the authenticated user
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body) {
        String uid = currentUid();
        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        Object recs = body.get("recommendations");
        if (recs == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "recommendations field required"));
        }

        try {
            String json = objectMapper.writeValueAsString(recs);
            UserRecommendation rec = repo.findFirstByUidOrderByIdDesc(uid)
                    .orElseGet(UserRecommendation::new);
            rec.setUid(uid);
            rec.setSubmittedAt(Instant.now());
            rec.setRecommendationsJson(json);
            repo.save(rec);
            return ResponseEntity.ok(Map.of("uid", uid, "saved", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get latest recommendations for a uid
    @GetMapping("/{uid}")
    public ResponseEntity<?> get(@PathVariable String uid) {
        return repo.findFirstByUidOrderByIdDesc(uid)
                .<ResponseEntity<?>>map(rec -> {
                    try {
                        List<?> recs = objectMapper.readValue(rec.getRecommendationsJson(), List.class);
                        return ResponseEntity.ok(Map.of(
                                "uid", rec.getUid(),
                                "submittedAt", rec.getSubmittedAt().toString(),
                                "recommendations", recs));
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No recommendations found for " + uid)));
    }

    private String currentUid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }
}
