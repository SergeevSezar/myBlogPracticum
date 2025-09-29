package ru.ssergeev.myblog.repository;

import ru.ssergeev.myblog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findAll(int pageNumber, int pageSize, String search);
    Optional<Post> findById(Long id);
    Post save(Post post);
    Post update(Post post);
    void delete(Long id);
    void incrementLikes(Long id);
    void updateImage(Long id, byte[] image);
    byte[] getImage(Long id);
    int getTotalPages(int pageSize, String search);
}
