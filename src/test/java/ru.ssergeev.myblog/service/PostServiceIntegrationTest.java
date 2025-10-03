package ru.ssergeev.myblog.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.ssergeev.myblog.model.Post;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@Transactional
class PostServiceIntegrationTest {

    @Autowired
    private PostServiceImpl postService;

    @Test
    @Sql("/db/test-data.sql")
    public void getPosts_ShouldReturnPostsWithPagination() {
        // Act
        List<Post> posts = postService.getPosts(1, 10, null);

        // Assert
        assertNotNull(posts);
        assertFalse(posts.isEmpty());
        assertEquals(3, posts.size());
    }

    @Test
    @Sql("/db/test-data.sql")
    public void createPost_ShouldSaveNewPost() {
        // Arrange
        Post newPost = new Post();
        newPost.setTitle("New Integration Test Post");
        newPost.setText("Content for integration test");
        newPost.setTags(List.of("test", "integration"));

        // Act
        Post savedPost = postService.createPost(newPost);
        List<Post> allPosts = postService.getPosts(1, 10, null);

        // Assert
        assertNotNull(savedPost.getId());
        assertEquals(4, allPosts.size());
        assertEquals("New Integration Test Post", savedPost.getTitle());
    }

    @Test
    @Sql("/db/test-data.sql")
    public void likePost_ShouldIncrementLikes() {
        // Act
        int likesAfterFirst = postService.likePost(1L);
        int likesAfterSecond = postService.likePost(1L);

        // Assert
        assertEquals(6, likesAfterFirst); // Было 5 + 1
        assertEquals(7, likesAfterSecond); // Стало 6 + 1
    }

    @Test
    @Sql("/db/test-data.sql")
    public void searchPosts_ShouldFilterBySearchTerm() {
        // Act
        List<Post> springPosts = postService.getPosts(1, 10, "spring");
        List<Post> javaPosts = postService.getPosts(1, 10, "java");

        // Assert
        assertEquals(2, springPosts.size());
        assertEquals(1, javaPosts.size());
    }
}
