package ru.ssergeev.myblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssergeev.myblog.model.Post;
import ru.ssergeev.myblog.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<Post> getPosts(int pageNumber, int pageSize, String search) {
        log.debug("Getting posts: pageNumber={}, pageSize={}, search='{}'", pageNumber, pageSize, search);

        validatePaginationParams(pageNumber, pageSize);

        List<Post> posts = postRepository.findAll(pageNumber, pageSize, search);
        posts.forEach(this::truncateTextForList);

        return posts;
    }

    @Override
    public Optional<Post> getPost(Long id) {
        log.debug("Getting post by id: {}", id);
        validatePostId(id);
        return postRepository.findById(id);
    }

    @Override
    @Transactional
    public Post createPost(Post post) {
        log.debug("Creating new post: {}", post.getTitle());

        validatePost(post);

        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully with id: {}", savedPost.getId());

        return savedPost;
    }

    @Override
    @Transactional
    public Post updatePost(Post post) {
        log.debug("Updating post with id: {}", post.getId());

        validatePostId(post.getId());
        validatePost(post);

        Post existingPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException(post.getId().toString()));

        // Сохраняем неизменяемые поля
        post.setLikesCount(existingPost.getLikesCount());
        post.setCommentsCount(existingPost.getCommentsCount());
        post.setCreatedAt(existingPost.getCreatedAt());
        post.setImage(existingPost.getImage());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.update(post);
        log.info("Post updated successfully with id: {}", updatedPost.getId());

        return updatedPost;
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        log.debug("Deleting post with id: {}", id);

        validatePostId(id);

        if (!postRepository.findById(id).isPresent()) {
            throw new RuntimeException(id.toString());
        }

        postRepository.delete(id);
        log.info("Post deleted successfully with id: {}", id);
    }

    @Override
    @Transactional
    public int likePost(Long id) {
        log.debug("Incrementing likes for post with id: {}", id);

        validatePostId(id);

        if (!postRepository.findById(id).isPresent()) {
            throw new RuntimeException(id.toString());
        }

        postRepository.incrementLikes(id);

        int likesCount = postRepository.findById(id)
                .map(Post::getLikesCount)
                .orElse(0);

        log.info("Likes incremented for post id: {}, new likes count: {}", id, likesCount);
        return likesCount;
    }

    @Override
    @Transactional
    public void updatePostImage(Long id, byte[] image) {
        log.debug("Updating image for post with id: {}", id);

        validatePostId(id);
        validateImageData(image);

        if (!postRepository.findById(id).isPresent()) {
            throw new RuntimeException(id.toString());
        }

        postRepository.updateImage(id, image);
        log.info("Image updated for post with id: {}", id);
    }

    @Override
    public byte[] getPostImage(Long id) {
        log.debug("Getting image for post with id: {}", id);

        validatePostId(id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(id.toString()));

        byte[] image = postRepository.getImage(id);
        if (image == null || image.length == 0) {
            throw new RuntimeException("Image not found for post with id: " + id);
        }

        return image;
    }

    @Override
    public boolean hasPreviousPage(int pageNumber) {
        return pageNumber > 1;
    }

    @Override
    public boolean hasNextPage(int pageNumber, int pageSize, String search) {
        if (pageNumber < 1 || pageSize < 1) {
            return false;
        }

        int lastPage = getLastPage(pageSize, search);
        return pageNumber < lastPage;
    }

    @Override
    public int getLastPage(int pageSize, String search) {
        if (pageSize < 1) {
            return 0;
        }

        return postRepository.getTotalPages(pageSize, search);
    }

    // Валидационные методы
    private void validatePostId(Long id) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Post ID must be positive");
        }
    }

    private void validatePaginationParams(int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new RuntimeException("Page number must be greater than 0");
        }
        if (pageSize < 1) {
            throw new RuntimeException("Page size must be greater than 0");
        }
    }

    private void validatePost(Post post) {
        if (post == null) {
            throw new RuntimeException("Post cannot be null");
        }
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Post title cannot be empty");
        }
        if (post.getText() == null || post.getText().trim().isEmpty()) {
            throw new RuntimeException("Post text cannot be empty");
        }
        if (post.getTitle().length() > 255) {
            throw new RuntimeException("Post title cannot exceed 255 characters");
        }
    }

    private void validateImageData(byte[] image) {
        if (image == null || image.length == 0) {
            throw new RuntimeException("Image data cannot be null or empty");
        }
        if (image.length > 10 * 1024 * 1024) { // 10MB limit
            throw new RuntimeException("Image size cannot exceed 10MB");
        }
    }

    private void truncateTextForList(Post post) {
        if (post.getText() != null && post.getText().length() > 128) {
            post.setText(post.getText().substring(0, 128) + "…");
        }
    }
}
