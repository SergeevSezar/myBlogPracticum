package ru.ssergeev.myblog.service;

import ru.ssergeev.myblog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> getPosts(int pageNumber, int pageSize, String search);
    Optional<Post> getPost(Long id);
    Post createPost(Post post);
    Post updatePost(Post post);
    void deletePost(Long id);
    int likePost(Long id);
    void updatePostImage(Long id, byte[] image);
    byte[] getPostImage(Long id);
    boolean hasPreviousPage(int pageNumber);
    boolean hasNextPage(int pageNumber, int pageSize, String search);
    int getLastPage(int pageSize, String search);
}
