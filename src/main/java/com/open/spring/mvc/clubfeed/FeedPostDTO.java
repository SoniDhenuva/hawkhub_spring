package com.open.spring.mvc.clubfeed;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedPostDTO {

    private Long id;

    // Primary club for backward compat
    private String clubId;

    // All tagged clubs as a list
    private List<String> clubIds;

    // Caption / description text
    private String caption;

    // All image URLs/data in carousel order
    private List<String> imageUrls;

    // Post category: General | Event | Announcement | Achievement | Recruitment
    private String postType;

    // Hashtags extracted from caption or entered separately
    private List<String> tags;

    private int likeCount;
    private int saveCount;

    private boolean likedByCurrentUser;
    private boolean savedByCurrentUser;
    private boolean owner;

    private String authorUid;
    private Instant createdAt;

    // Backward-compat aliases so the old frontend still works
    public String getTitle() {
        return caption;
    }

    public String getDescription() {
        return caption;
    }

    public String getImageUrl() {
        return (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
    }

    public int getLikes() {
        return likeCount;
    }
}
