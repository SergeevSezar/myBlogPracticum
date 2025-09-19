package ru.ssergeev.myblog.repository;

import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.ssergeev.myblog.model.Post;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));

        // Обработка массива тегов
        Array tagsArray = rs.getArray("tags");
        if (tagsArray != null) {
            String[] tags = (String[]) tagsArray.getArray();
            post.setTags(Arrays.asList(tags));
        } else {
            post.setTags(List.of());
        }

        post.setLikesCount(rs.getInt("likes_count"));
        post.setCommentsCount(rs.getInt("comments_count"));
        post.setImage(rs.getBytes("image"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            post.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            post.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return post;
    };

    @Override
    public List<Post> findAll(int pageNumber, int pageSize, String search) {
        int offset = (pageNumber - 1) * pageSize;
        String sql = """
            SELECT p.*, 
                   (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as comments_count
            FROM posts p
            WHERE (? IS NULL OR p.title ILIKE ? OR EXISTS (
                SELECT 1 FROM unnest(p.tags) AS tag 
                WHERE tag ILIKE ?
            ))
            ORDER BY p.created_at DESC
            LIMIT ? OFFSET ?
            """;

        String searchPattern = search != null && !search.trim().isEmpty() ? "%" + search + "%" : null;
        return jdbcTemplate.query(sql, postRowMapper,
                searchPattern, searchPattern, searchPattern, pageSize, offset);
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = """
            SELECT p.*, 
                   (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) as comments_count
            FROM posts p WHERE p.id = ?
            """;
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, id);
        return posts.stream().findFirst();
    }

    @Override
    public Post save(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = """
            INSERT INTO posts (title, text, tags, likes_count, image, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setArray(3, connection.createArrayOf("VARCHAR",
                    post.getTags() != null ? post.getTags().toArray() : new String[0]));
            ps.setInt(4, post.getLikesCount());
            ps.setBytes(5, post.getImage());
            ps.setTimestamp(6, Timestamp.valueOf(post.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(post.getUpdatedAt()));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            post.setId(keyHolder.getKey().longValue());
        }
        return post;
    }

    @Override
    @SneakyThrows
    public Post update(Post post) {
        String sql = """
            UPDATE posts 
            SET title = ?, text = ?, tags = ?, updated_at = ?
            WHERE id = ?
            """;

        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getText(),
                jdbcTemplate.getDataSource().getConnection().createArrayOf("VARCHAR",
                        post.getTags() != null ? post.getTags().toArray() : new String[0]),
                Timestamp.valueOf(post.getUpdatedAt()),
                post.getId());

        return post;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void incrementLikes(Long id) {
        String sql = "UPDATE posts SET likes_count = likes_count + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateImage(Long id, byte[] image) {
        String sql = "UPDATE posts SET image = ? WHERE id = ?";
        jdbcTemplate.update(sql, image, id);
    }

    @Override
    public byte[] getImage(Long id) {
        String sql = "SELECT image FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, byte[].class, id);
    }

    @Override
    public int getTotalPages(int pageSize, String search) {
        String sql = """
            SELECT COUNT(*) 
            FROM posts p
            WHERE (? IS NULL OR p.title ILIKE ? OR EXISTS (
                SELECT 1 FROM unnest(p.tags) AS tag 
                WHERE tag ILIKE ?
            ))
            """;

        String searchPattern = search != null && !search.trim().isEmpty() ? "%" + search + "%" : null;
        Integer totalCount = jdbcTemplate.queryForObject(sql, Integer.class,
                searchPattern, searchPattern, searchPattern);

        if (totalCount == null) {
            return 0;
        }

        return (int) Math.ceil((double) totalCount / pageSize);
    }
}
