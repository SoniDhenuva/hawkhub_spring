package com.open.spring.mvc.clubfeed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class ClubFeedPostTest {
    @Test
    public void testClubFeedPostFields() {
        ClubFeedPost post = new ClubFeedPost();
        Instant createdAt = Instant.parse("2026-05-28T17:30:00Z");

        post.setClubId("optix");
        post.setTitle("Robot reveal night");
        post.setDescription("Come see the new build after school.");
        post.setImageUrl("");
        post.setTopicPath("/student/club-feed");
        post.setLikes(0);
        post.setCreatedAt(createdAt);

        assertNull(post.getId(), "id should remain null until persistence assigns it");
        assertEquals("optix", post.getClubId(), "getClubId should return set clubId");
        assertEquals("Robot reveal night", post.getTitle(), "getTitle should return set title");
        assertEquals("Come see the new build after school.", post.getDescription(), "getDescription should return set description");
        assertEquals("", post.getImageUrl(), "getImageUrl should return set imageUrl");
        assertEquals("/student/club-feed", post.getTopicPath(), "getTopicPath should return set topicPath");
        assertEquals(0, post.getLikes(), "getLikes should return set likes");
        assertEquals(createdAt, post.getCreatedAt(), "getCreatedAt should return set createdAt");
    }
}
