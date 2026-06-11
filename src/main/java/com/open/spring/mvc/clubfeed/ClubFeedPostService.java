package com.open.spring.mvc.clubfeed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClubFeedPostService {

    private final ClubFeedPostRepository repository;
    private final FeedPostImageRepository imageRepository;
    private final FeedLikeRepository likeRepository;
    private final FeedSaveRepository saveRepository;

    public ClubFeedPostService(
            ClubFeedPostRepository repository,
            FeedPostImageRepository imageRepository,
            FeedLikeRepository likeRepository,
            FeedSaveRepository saveRepository) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.likeRepository = likeRepository;
        this.saveRepository = saveRepository;
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    public List<FeedPostDTO> findPostsAsDTO(String clubId, String postType, String search, String currentUserUid) {
        List<ClubFeedPost> posts;

        if (clubId != null && !clubId.isBlank()) {
            posts = repository.findByClubIdOrderByCreatedAtDesc(clubId);
        } else if (postType != null && !postType.isBlank() && !postType.equalsIgnoreCase("all")) {
            posts = repository.findByPostTypeOrderByCreatedAtDesc(postType);
        } else {
            posts = repository.findAllByOrderByCreatedAtDesc();
        }

        if (search != null && !search.isBlank()) {
            String lowerSearch = search.toLowerCase();
            posts = posts.stream()
                    .filter(p -> (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerSearch))
                            || (p.getTitle() != null && p.getTitle().toLowerCase().contains(lowerSearch))
                            || (p.getTagsCsv() != null && p.getTagsCsv().toLowerCase().contains(lowerSearch)))
                    .collect(Collectors.toList());
        }

        return posts.stream()
                .map(p -> buildDTO(p, currentUserUid))
                .collect(Collectors.toList());
    }

    public List<FeedPostDTO> getSavedPostsDTO(String currentUserUid) {
        if (currentUserUid == null) return List.of();
        List<FeedSave> saves = saveRepository.findByUserUidOrderByIdDesc(currentUserUid);
        return saves.stream()
                .map(s -> repository.findById(s.getPostId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(p -> buildDTO(p, currentUserUid))
                .collect(Collectors.toList());
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Transactional
    public FeedPostDTO createFeedPost(CreateFeedPostRequest request, String authorUid) {
        String primaryClub = request.getClubIds().get(0);
        String clubIdsCsv = String.join(",", request.getClubIds());
        String tagsCsv = normalizeTags(request.getCaption(), request.getTags());

        ClubFeedPost post = new ClubFeedPost();
        post.setClubId(primaryClub);
        post.setClubIds(clubIdsCsv);
        post.setTitle(request.getCaption()); // backward compat
        post.setDescription(request.getCaption());
        post.setPostType(request.getPostType() != null ? request.getPostType() : "General");
        post.setTagsCsv(tagsCsv);
        post.setAuthorUid(authorUid);
        post.setLikes(0);
        post.setCreatedAt(Instant.now());
        ClubFeedPost saved = repository.save(post);

        // Store each image in feed_post_image
        List<String> images = request.getImages() != null ? request.getImages() : List.of();
        for (int i = 0; i < images.size(); i++) {
            imageRepository.save(new FeedPostImage(saved.getId(), images.get(i), i));
        }
        // Backward compat: store first image in legacy field too
        if (!images.isEmpty()) {
            saved.setImageUrl(images.get(0));
            repository.save(saved);
        }

        return buildDTO(saved, authorUid);
    }

    // ── LEGACY CREATE (kept for old frontend) ─────────────────────────────────

    public ClubFeedPost createPost(ClubFeedPost post) {
        post.setId(null);
        post.setLikes(0);
        post.setCreatedAt(Instant.now());
        if (post.getPostType() == null) post.setPostType("General");
        return repository.save(post);
    }

    // ── LIKE / SAVE ───────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> toggleLike(Long postId, String userUid) {
        Optional<FeedLike> existing = likeRepository.findByPostIdAndUserUid(postId, userUid);
        boolean nowLiked;
        if (existing.isPresent()) {
            likeRepository.deleteByPostIdAndUserUid(postId, userUid);
            nowLiked = false;
        } else {
            likeRepository.save(new FeedLike(postId, userUid));
            nowLiked = true;
        }
        int count = likeRepository.countByPostId(postId);
        return Map.of("liked", nowLiked, "likeCount", count);
    }

    @Transactional
    public Map<String, Object> toggleSave(Long postId, String userUid) {
        Optional<FeedSave> existing = saveRepository.findByPostIdAndUserUid(postId, userUid);
        boolean nowSaved;
        if (existing.isPresent()) {
            saveRepository.deleteByPostIdAndUserUid(postId, userUid);
            nowSaved = false;
        } else {
            saveRepository.save(new FeedSave(postId, userUid));
            nowSaved = true;
        }
        int count = saveRepository.countByPostId(postId);
        return Map.of("saved", nowSaved, "saveCount", count);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Transactional
    public boolean deletePost(Long id, String userUid) {
        Optional<ClubFeedPost> opt = repository.findById(id);
        if (opt.isEmpty()) return false;
        ClubFeedPost post = opt.get();
        if (!userUid.equals(post.getAuthorUid())) return false;
        imageRepository.deleteByPostId(id);
        likeRepository.deleteByPostId(id);
        saveRepository.deleteByPostId(id);
        repository.deleteById(id);
        return true;
    }

    // ── LEGACY LIKE ───────────────────────────────────────────────────────────

    public Optional<ClubFeedPost> likePost(Long id) {
        return repository.findById(id)
                .map(post -> {
                    post.setLikes(post.getLikes() + 1);
                    return repository.save(post);
                });
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    FeedPostDTO buildDTO(ClubFeedPost post, String currentUserUid) {
        List<String> imageUrls = new ArrayList<>();

        // Collect images from feed_post_image table first
        List<FeedPostImage> storedImages = imageRepository.findByPostIdOrderByDisplayOrder(post.getId());
        storedImages.forEach(img -> imageUrls.add(img.getImageData()));

        // Fallback: legacy imageUrl field (only add if not already included)
        if (imageUrls.isEmpty() && post.getImageUrl() != null && !post.getImageUrl().isBlank()) {
            imageUrls.add(post.getImageUrl());
        }

        int likeCount = likeRepository.countByPostId(post.getId());
        int saveCount = saveRepository.countByPostId(post.getId());
        boolean liked = currentUserUid != null && likeRepository.findByPostIdAndUserUid(post.getId(), currentUserUid).isPresent();
        boolean saved = currentUserUid != null && saveRepository.findByPostIdAndUserUid(post.getId(), currentUserUid).isPresent();
        boolean isOwner = currentUserUid != null && currentUserUid.equals(post.getAuthorUid());

        List<String> clubIdList = parseClubIds(post);
        List<String> tags = parseTags(post.getTagsCsv());

        return FeedPostDTO.builder()
                .id(post.getId())
                .clubId(post.getClubId())
                .clubIds(clubIdList)
                .caption(post.getDescription())
                .imageUrls(imageUrls)
                .postType(post.getPostType() != null ? post.getPostType() : "General")
                .tags(tags)
                .likeCount(likeCount > 0 ? likeCount : post.getLikes())
                .saveCount(saveCount)
                .likedByCurrentUser(liked)
                .savedByCurrentUser(saved)
                .owner(isOwner)
                .authorUid(post.getAuthorUid())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private List<String> parseClubIds(ClubFeedPost post) {
        if (post.getClubIds() != null && !post.getClubIds().isBlank()) {
            return Arrays.stream(post.getClubIds().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        return List.of(post.getClubId());
    }

    private List<String> parseTags(String tagsCsv) {
        if (tagsCsv == null || tagsCsv.isBlank()) return List.of();
        return Arrays.stream(tagsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String normalizeTags(String caption, List<String> explicitTags) {
        List<String> tags = new ArrayList<>();

        // Extract #hashtags from caption
        if (caption != null) {
            Arrays.stream(caption.split("\\s+"))
                    .filter(word -> word.startsWith("#") && word.length() > 1)
                    .forEach(tags::add);
        }

        // Merge explicit tags, normalize to start with #
        if (explicitTags != null) {
            explicitTags.stream()
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .map(t -> t.startsWith("#") ? t : "#" + t)
                    .filter(t -> !tags.contains(t))
                    .forEach(tags::add);
        }

        return tags.isEmpty() ? null : String.join(",", tags);
    }

    // ── REQUEST DTO ───────────────────────────────────────────────────────────

    @lombok.Data
    public static class CreateFeedPostRequest {
        @jakarta.validation.constraints.NotBlank(message = "caption is required")
        private String caption;

        @jakarta.validation.constraints.NotEmpty(message = "at least one club is required")
        private List<String> clubIds;

        private String postType;
        private List<String> tags;
        private List<String> images;
    }
}
