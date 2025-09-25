package service;

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

        when(postRepository.findAll(1, 10, "test"))
                .thenReturn(List.of(post));

        // Act
        List<Post> result = postService.getPosts(1, 10, "test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(128 + 1, result.get(0).getText().length()); // 128 символов + "…"
        assertTrue(result.get(0).getText().endsWith("…"));
        verify(postRepository).findAll(1, 10, "test");
    }

    @Test
    void getPost_ShouldReturnPost() {
        // Arrange
        Post post = createTestPost(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        Optional<Post> result = postService.getPost(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(postRepository).findById(1L);
    }

    @Test
    void createPost_ShouldSavePostWithCorrectData() {
        // Arrange
        Post postToSave = createTestPost(null);
        Post savedPost = createTestPost(1L);

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        // Act
        Post result = postService.createPost(postToSave);

        // Assert
        assertNotNull(result.getId());
        assertEquals(0, result.getLikesCount());
        assertEquals(0, result.getCommentsCount());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(postRepository).save(argThat(post ->
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

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(postRepository.update(any(Post.class))).thenReturn(updateData);

        // Act
        Post result = postService.updatePost(updateData);

        // Assert
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Text", result.getText());
        assertEquals(5, result.getLikesCount()); // Сохранено из оригинала
        assertEquals(3, result.getCommentsCount()); // Сохранено из оригинала

        verify(postRepository).findById(1L);
        verify(postRepository).update(any(Post.class));
    }

    @Test
    void deletePost_ShouldDeleteExistingPost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(createTestPost(1L)));

        // Act
        postService.deletePost(1L);

        // Assert
        verify(postRepository).delete(1L);
    }

    @Test
    void likePost_ShouldIncrementLikes() {
        // Arrange
        Post post = createTestPost(1L);
        post.setLikesCount(5);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        doNothing().when(postRepository).incrementLikes(1L);

        // Act
        int result = postService.likePost(1L);

        // Assert
        verify(postRepository).incrementLikes(1L);
        verify(postRepository, times(2)).findById(1L); // До и после инкремента
    }

    @Test
    void updatePostImage_ShouldUpdateImage() {
        // Arrange
        byte[] imageData = "fake image data".getBytes();
        when(postRepository.findById(1L)).thenReturn(Optional.of(createTestPost(1L)));

        // Act
        postService.updatePostImage(1L, imageData);

        // Assert
        verify(postRepository).updateImage(1L, imageData);
    }

    @Test
    void hasPreviousPage_ShouldReturnCorrectValue() {
        assertTrue(postService.hasPreviousPage(2));
        assertFalse(postService.hasPreviousPage(1));
    }

    @Test
    void hasNextPage_ShouldReturnCorrectValue() {
        // Arrange
        when(postRepository.getTotalPages(10, "test")).thenReturn(3);

        // Act & Assert
        assertTrue(postService.hasNextPage(1, 10, "test"));
        assertFalse(postService.hasNextPage(3, 10, "test"));
    }

    @Test
    void getLastPage_ShouldReturnCorrectValue() {
        // Arrange
        when(postRepository.getTotalPages(10, "test")).thenReturn(5);

        // Act
        int result = postService.getLastPage(10, "test");

        // Assert
        assertEquals(5, result);
        verify(postRepository).getTotalPages(10, "test");
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
