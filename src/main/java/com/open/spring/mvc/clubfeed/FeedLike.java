package com.open.spring.mvc.clubfeed;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feed_like",
    uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_uid"}))
public class FeedLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_uid", nullable = false)
    private String userUid;

    public FeedLike(Long postId, String userUid) {
        this.postId = postId;
        this.userUid = userUid;
    }
}
