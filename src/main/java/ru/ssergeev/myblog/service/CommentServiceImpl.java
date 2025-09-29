package ru.ssergeev.myblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssergeev.myblog.model.Comment;
import ru.ssergeev.myblog.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @Override
    public Optional<Comment> getComment(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Comment comment) {
        Optional<Comment> existingComment = commentRepository.findById(comment.getId());
        if (existingComment.isEmpty()) {
            throw new IllegalArgumentException("Comment not found with id: " + comment.getId());
        }

        comment.setCreatedAt(existingComment.get().getCreatedAt());

        return commentRepository.update(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        commentRepository.delete(id);
    }
}
