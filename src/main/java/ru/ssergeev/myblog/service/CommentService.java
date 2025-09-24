package ru.ssergeev.myblog.service;

import ru.ssergeev.myblog.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    List<Comment> getCommentsByPostId(Long postId);
    Optional<Comment> getComment(Long id);
    Comment createComment(Comment comment);
    Comment updateComment(Comment comment);
    void deleteComment(Long id);
}
