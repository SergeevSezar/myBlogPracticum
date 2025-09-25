package ru.ssergeev.myblog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ssergeev.myblog.model.Comment;
import ru.ssergeev.myblog.repository.CommentRepository;
import ru.ssergeev.myblog.service.CommentServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void getCommentsByPostId_ShouldReturnComments() {
        // Arrange
        Comment comment1 = createTestComment(1L, 1L);
        Comment comment2 = createTestComment(2L, 1L);

        Mockito.when(commentRepository.findByPostId(1L))
                .thenReturn(Arrays.asList(comment1, comment2));

        // Act
        List<Comment> result = commentService.getCommentsByPostId(1L);

        // Assert
        Assertions.assertEquals(2, result.size());
        Mockito.verify(commentRepository).findByPostId(1L);
    }

    @Test
    void getComment_ShouldReturnComment() {
        // Arrange
        Comment comment = createTestComment(1L, 1L);
        Mockito.when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        Optional<Comment> result = commentService.getComment(1L);

        // Assert
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(1L, result.get().getId());
        Mockito.verify(commentRepository).findById(1L);
    }

    @Test
    void createComment_ShouldSaveCommentWithTimestamp() {
        // Arrange
        Comment commentToSave = createTestComment(null, 1L);
        Comment savedComment = createTestComment(1L, 1L);

        Mockito.when(commentRepository.save(ArgumentMatchers.any(Comment.class))).thenReturn(savedComment);

        // Act
        Comment result = commentService.createComment(commentToSave);

        // Assert
        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getCreatedAt());
        Mockito.verify(commentRepository).save(ArgumentMatchers.argThat(comment ->
                comment.getCreatedAt() != null
        ));
    }

    @Test
    void updateComment_ShouldUpdateExistingComment() {
        // Arrange
        Comment existingComment = createTestComment(1L, 1L);
        existingComment.setCreatedAt(LocalDateTime.now().minusDays(1));

        Comment updateData = createTestComment(1L, 1L);
        updateData.setText("Updated Text");

        Mockito.when(commentRepository.findById(1L)).thenReturn(Optional.of(existingComment));
        Mockito.when(commentRepository.update(ArgumentMatchers.any(Comment.class))).thenReturn(updateData);

        // Act
        Comment result = commentService.updateComment(updateData);

        // Assert
        Assertions.assertEquals("Updated Text", result.getText());
        Assertions.assertEquals(existingComment.getCreatedAt(), result.getCreatedAt()); // Время создания сохраняется

        Mockito.verify(commentRepository).findById(1L);
        Mockito.verify(commentRepository).update(ArgumentMatchers.any(Comment.class));
    }

    @Test
    void updateComment_NonExistingComment_ShouldThrowException() {
        // Arrange
        Comment comment = createTestComment(999L, 1L);
        Mockito.when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(comment));
    }

    @Test
    void deleteComment_ShouldDeleteExistingComment() {
        // Arrange
        Mockito.when(commentRepository.findById(1L)).thenReturn(Optional.of(createTestComment(1L, 1L)));

        // Act
        commentService.deleteComment(1L);

        // Assert
        Mockito.verify(commentRepository).delete(1L);
    }

    private Comment createTestComment(Long id, Long postId) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("Test Comment");
        comment.setPostId(postId);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }
}
