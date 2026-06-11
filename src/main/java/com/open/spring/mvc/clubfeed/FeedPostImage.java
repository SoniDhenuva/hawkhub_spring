package com.open.spring.mvc.clubfeed;

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
@Table(name = "feed_post_image")
public class FeedPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "image_data", nullable = false, columnDefinition = "TEXT")
    private String imageData;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    public FeedPostImage(Long postId, String imageData, int displayOrder) {
        this.postId = postId;
        this.imageData = imageData;
        this.displayOrder = displayOrder;
    }
}
