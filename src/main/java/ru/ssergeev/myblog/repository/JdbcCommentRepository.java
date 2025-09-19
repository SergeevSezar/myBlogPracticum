package ru.ssergeev.myblog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.ssergeev.myblog.model.Comment;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setText(rs.getString("text"));
        comment.setPostId(rs.getLong("post_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            comment.setCreatedAt(createdAt.toLocalDateTime());
        }

        return comment;
    };

    @Override
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        List<Comment> comments = jdbcTemplate.query(sql, commentRowMapper, id);
        return comments.stream().findFirst();
    }

    @Override
    public Comment save(Comment comment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO comments (text, post_id, created_at) VALUES (?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, comment.getText());
            ps.setLong(2, comment.getPostId());
            ps.setTimestamp(3, Timestamp.valueOf(comment.getCreatedAt()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            comment.setId(keyHolder.getKey().longValue());
        }
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        String sql = "UPDATE comments SET text = ? WHERE id = ?";
        jdbcTemplate.update(sql, comment.getText(), comment.getId());
        return comment;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
