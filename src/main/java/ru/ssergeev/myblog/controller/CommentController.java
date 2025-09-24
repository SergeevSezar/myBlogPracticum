package ru.ssergeev.myblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssergeev.myblog.model.Comment;
import ru.ssergeev.myblog.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Получение всех комментариев поста
     * GET /api/posts/1/comments
     */
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Получение конкретного комментария поста
     * GET /api/posts/1/comments/2
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {

        return commentService.getComment(commentId)
                .map(comment -> {
                    // Проверяем, что комментарий принадлежит указанному посту
                    if (!comment.getPostId().equals(postId)) {
                        return ResponseEntity.badRequest().<Comment>build();
                    }
                    return ResponseEntity.ok(comment);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Добавление комментария к посту
     * POST /api/posts/1/comments
     */
    @PostMapping
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestBody Comment comment) {

        // Устанавливаем postId из path variable
        comment.setPostId(postId);

        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }

    /**
     * Редактирование комментария
     * PUT /api/posts/1/comments/2
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody Comment comment) {

        // Проверяем соответствие ID в path и теле запроса
        if (!commentId.equals(comment.getId())) {
            return ResponseEntity.badRequest().build();
        }

        // Проверяем, что комментарий принадлежит указанному посту
        if (!postId.equals(comment.getPostId())) {
            return ResponseEntity.badRequest().build();
        }

        Comment updatedComment = commentService.updateComment(comment);
        return ResponseEntity.ok(updatedComment);
    }

    /**
     * Удаление комментария
     * DELETE /api/posts/1/comments/2
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {

        // Проверяем существование комментария и его принадлежность к посту
        return commentService.getComment(commentId)
                .map(comment -> {
                    if (!comment.getPostId().equals(postId)) {
                        return ResponseEntity.badRequest().<Void>build();
                    }

                    commentService.deleteComment(commentId);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
