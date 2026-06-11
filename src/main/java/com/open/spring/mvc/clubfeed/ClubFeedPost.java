package com.open.spring.mvc.clubfeed;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "club_feed_post")
public class ClubFeedPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Primary club ID (backward compat — first entry from clubIds)
    @Column(nullable = false)
    private String clubId;

    // Legacy title field kept for backward compat
    @Column(nullable = false)
    private String title;

    // Caption / post body text
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Legacy single image URL kept for backward compat
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private String topicPath;

    // Legacy like counter kept for backward compat with old /{id}/likes endpoint
    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private Instant createdAt;

    // New fields (added via FeedTablesMigration — nullable to not break existing rows)

    @Column(name = "author_uid")
    private String authorUid;

    // General | Event | Announcement | Achievement | Recruitment
    @Column(name = "post_type", length = 50)
    private String postType;

    // Comma-separated hashtags, e.g. "#robotics,#competition"
    @Column(name = "tags_csv", columnDefinition = "TEXT")
    private String tagsCsv;

    // Comma-separated club IDs for multi-club tagging, e.g. "optix,hosa"
    @Column(name = "club_ids", columnDefinition = "TEXT")
    private String clubIds;
}
