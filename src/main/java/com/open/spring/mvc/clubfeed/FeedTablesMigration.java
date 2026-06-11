package com.open.spring.mvc.clubfeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class FeedTablesMigration {

    private static final Logger log = LoggerFactory.getLogger(FeedTablesMigration.class);

    private final JdbcTemplate jdbc;

    public FeedTablesMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Bean(name = "migrateFeedTables")
    public ApplicationRunner migrateFeedTables() {
        return args -> {
            createFeedPostImageTable();
            createFeedLikeTable();
            createFeedSaveTable();
            addColumnIfMissing("club_feed_post", "author_uid", "TEXT");
            addColumnIfMissing("club_feed_post", "post_type", "TEXT DEFAULT 'General'");
            addColumnIfMissing("club_feed_post", "tags_csv", "TEXT");
            addColumnIfMissing("club_feed_post", "club_ids", "TEXT");
            log.info("Feed tables migration complete");
        };
    }

    private void createFeedPostImageTable() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS feed_post_image (
                id       INTEGER PRIMARY KEY,
                post_id  INTEGER NOT NULL,
                image_data TEXT NOT NULL,
                display_order INTEGER NOT NULL DEFAULT 0
            )
            """);
    }

    private void createFeedLikeTable() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS feed_like (
                id       INTEGER PRIMARY KEY,
                post_id  INTEGER NOT NULL,
                user_uid TEXT NOT NULL,
                UNIQUE(post_id, user_uid)
            )
            """);
    }

    private void createFeedSaveTable() {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS feed_save (
                id       INTEGER PRIMARY KEY,
                post_id  INTEGER NOT NULL,
                user_uid TEXT NOT NULL,
                UNIQUE(post_id, user_uid)
            )
            """);
    }

    private void addColumnIfMissing(String table, String column, String definition) {
        try {
            jdbc.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        } catch (Exception e) {
            // Column already exists — safe to ignore
        }
    }
}
