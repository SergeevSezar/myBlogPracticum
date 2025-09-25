package ru.ssergeev.myblog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ssergeev.myblog.model.Post;
import ru.ssergeev.myblog.repository.PostRepository;
import ru.ssergeev.myblog.service.PostServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getPosts_ShouldReturnPostsWithTruncatedText() {
        // Arrange
        Post post = createTestPost(1L);
        post.setText("A".repeat(200)); // Длинный текст

        Mockito.when(postRepository.findAll(1, 10, "test"))
                .thenReturn(List.of(post));

        // Act
        List<Post> result = postService.getPosts(1, 10, "test");

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(128 + 1, result.get(0).getText().length()); // 128 символов + "…"
        Assertions.assertTrue(result.get(0).getText().endsWith("…"));
        Mockito.verify(postRepository).findAll(1, 10, "test");
    }

    @Test
    void getPost_ShouldReturnPost() {
        // Arrange
        Post post = createTestPost(1L);
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        Optional<Post> result = postService.getPost(1L);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(1L, result.get().getId());
        Mockito.verify(postRepository).findById(1L);
    }

    @Test
    void createPost_ShouldSavePostWithCorrectData() {
        // Arrange
        Post postToSave = createTestPost(null);
        Post savedPost = createTestPost(1L);

        Mockito.when(postRepository.save(ArgumentMatchers.any(Post.class))).thenReturn(savedPost);

        // Act
        Post result = postService.createPost(postToSave);

        // Assert
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(0, result.getLikesCount());
        Assertions.assertEquals(0, result.getCommentsCount());
        Assertions.assertNotNull(result.getCreatedAt());
        Assertions.assertNotNull(result.getUpdatedAt());

        Mockito.verify(postRepository).save(ArgumentMatchers.argThat(post ->
                post.getLikesCount() == 0 &&
                        post.getCommentsCount() == 0 &&
                        post.getCreatedAt() != null
        ));
    }

    @Test
    void updatePost_ShouldUpdateExistingPost() {
        // Arrange
        Post existingPost = createTestPost(1L);
        existingPost.setLikesCount(5);
        existingPost.setCommentsCount(3);
        existingPost.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post updateData = createTestPost(1L);
        updateData.setTitle("Updated Title");
        updateData.setText("Updated Text");

        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        Mockito.when(postRepository.update(ArgumentMatchers.any(Post.class))).thenReturn(updateData);

        // Act
        Post result = postService.updatePost(updateData);

        // Assert
        Assertions.assertEquals("Updated Title", result.getTitle());
        Assertions.assertEquals("Updated Text", result.getText());
        Assertions.assertEquals(5, result.getLikesCount()); // Сохранено из оригинала
        Assertions.assertEquals(3, result.getCommentsCount()); // Сохранено из оригинала

        Mockito.verify(postRepository).findById(1L);
        Mockito.verify(postRepository).update(ArgumentMatchers.any(Post.class));
    }

    @Test
    void deletePost_ShouldDeleteExistingPost() {
        // Arrange
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(createTestPost(1L)));

        // Act
        postService.deletePost(1L);

        // Assert
        Mockito.verify(postRepository).delete(1L);
    }

    @Test
    void likePost_ShouldIncrementLikes() {
        // Arrange
        Post post = createTestPost(1L);
        post.setLikesCount(5);

        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Mockito.doNothing().when(postRepository).incrementLikes(1L);

        // Act
        int result = postService.likePost(1L);

        // Assert
        Mockito.verify(postRepository).incrementLikes(1L);
        Mockito.verify(postRepository, Mockito.times(2)).findById(1L); // До и после инкремента
    }

    @Test
    void updatePostImage_ShouldUpdateImage() {
        // Arrange
        byte[] imageData = "fake image data".getBytes();
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(createTestPost(1L)));

        // Act
        postService.updatePostImage(1L, imageData);

        // Assert
        Mockito.verify(postRepository).updateImage(1L, imageData);
    }

    @Test
    void hasPreviousPage_ShouldReturnCorrectValue() {
        Assertions.assertTrue(postService.hasPreviousPage(2));
        Assertions.assertFalse(postService.hasPreviousPage(1));
    }

    @Test
    void hasNextPage_ShouldReturnCorrectValue() {
        // Arrange
        Mockito.when(postRepository.getTotalPages(10, "test")).thenReturn(3);

        // Act & Assert
        Assertions.assertTrue(postService.hasNextPage(1, 10, "test"));
        Assertions.assertFalse(postService.hasNextPage(3, 10, "test"));
    }

    @Test
    void getLastPage_ShouldReturnCorrectValue() {
        // Arrange
        Mockito.when(postRepository.getTotalPages(10, "test")).thenReturn(5);

        // Act
        int result = postService.getLastPage(10, "test");

        // Assert
        Assertions.assertEquals(5, result);
        Mockito.verify(postRepository).getTotalPages(10, "test");
    }

    private Post createTestPost(Long id) {
        Post post = new Post();
        post.setId(id);
        post.setTitle("Test Post");
        post.setText("Test Content");
        post.setTags(Arrays.asList("tag1", "tag2"));
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }
}
