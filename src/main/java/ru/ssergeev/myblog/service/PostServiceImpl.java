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

        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        List<Post> posts = postRepository.findAll(pageNumber, pageSize, search);

        posts.forEach(this::truncateTextForList);

        return posts;
    }

    @Override
    public Optional<Post> getPost(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Post ID must be positive");
        }

        return postRepository.findById(id);
    }

    @Override
    @Transactional
    public Post createPost(Post post) {

        validatePost(post);

        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Post post) {

        if (post.getId() == null || post.getId() <= 0) {
            throw new IllegalArgumentException("Post ID must be positive");
        }

        validatePost(post);

        Optional<Post> existingPost = postRepository.findById(post.getId());
        if (existingPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + post.getId());
        }

        post.setLikesCount(existingPost.get().getLikesCount());
        post.setCommentsCount(existingPost.get().getCommentsCount());
        post.setCreatedAt(existingPost.get().getCreatedAt());
        post.setImage(existingPost.get().getImage());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.update(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Post ID must be positive");
        }

        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + id);
        }

        postRepository.delete(id);
    }

    @Override
    @Transactional
    public int likePost(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Post ID must be positive");
        }

        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + id);
        }

        postRepository.incrementLikes(id);

        Optional<Post> updatedPost = postRepository.findById(id);

        return updatedPost.map(Post::getLikesCount).orElse(0);
    }

    @Override
    @Transactional
    public void updatePostImage(Long id, byte[] image) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Post ID must be positive");
        }

        if (image == null || image.length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty");
        }

        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + id);
        }

        postRepository.updateImage(id, image);
    }

    @Override
    public byte[] getPostImage(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Post ID must be positive");
        }

        // Проверяем существование поста
        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found with id: " + id);
        }

        byte[] image = postRepository.getImage(id);
        if (image == null || image.length == 0) {
            throw new IllegalArgumentException("Image not found for post with id: " + id);
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

    /**
     * Валидация данных поста перед сохранением/обновлением
     */
    private void validatePost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }

        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }

        if (post.getText() == null || post.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Post text cannot be empty");
        }

        if (post.getTitle().length() > 255) {
            throw new IllegalArgumentException("Post title cannot exceed 255 characters");
        }

        if (post.getTags() != null) {
            for (String tag : post.getTags()) {
                if (tag != null && tag.length() > 50) {
                    throw new IllegalArgumentException("Tag cannot exceed 50 characters: " + tag);
                }
            }
        }
    }

    /**
     * Обрезает текст поста до 128 символов для отображения в списке
     */
    private void truncateTextForList(Post post) {
        if (post.getText() != null && post.getText().length() > 128) {
            String truncatedText = post.getText().substring(0, 128) + "…";
            post.setText(truncatedText);
        }
    }

    /**
     * Получение полного текста поста (без обрезки)
     * Вспомогательный метод для внутреннего использования
     */
    public String getFullPostText(Long id) {
        Optional<Post> post = postRepository.findById(id);
        return post.map(Post::getText).orElse(null);
    }

    /**
     * Получение количества постов (для статистики)
     */
    public long getTotalPostsCount(String search) {
        return getLastPage(1, search);
    }
}
