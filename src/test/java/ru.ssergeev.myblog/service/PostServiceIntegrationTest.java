package ru.ssergeev.myblog.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.ssergeev.myblog.config.DataConfig;
import ru.ssergeev.myblog.model.Post;
import ru.ssergeev.myblog.repository.PostRepository;
import ru.ssergeev.myblog.service.PostServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(DataConfig.class)
@Transactional
class PostServiceIntegrationTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository);

        // Очистка базы данных перед каждым тестом
        jdbcTemplate.update("DELETE FROM comments");
        jdbcTemplate.update("DELETE FROM posts");
    }

    @Test
    void createAndRetrievePost_IntegrationTest() {
        // Arrange
        Post post = new Post();
        post.setTitle("Integration Test Post");
        post.setText("This is a test post for integration testing");
        post.setTags(Arrays.asList("integration", "test"));

        // Act
        Post savedPost = postService.createPost(post);
        List<Post> posts = postService.getPosts(1, 10, null);

        // Assert
        Assertions.assertNotNull(savedPost.getId());
        Assertions.assertEquals(1, posts.size());
        Assertions.assertEquals("Integration Test Post", posts.get(0).getTitle());

        // Проверяем, что текст обрезается для списка
        Assertions.assertTrue(posts.get(0).getText().length() <= 129); // 128 + "…"
    }

    @Test
    void updatePost_IntegrationTest() {
        // Arrange
        Post post = new Post();
        post.setTitle("Original Title");
        post.setText("Original Text");
        post.setTags(List.of("original"));

        Post savedPost = postService.createPost(post);

        // Act
        savedPost.setTitle("Updated Title");
        savedPost.setText("Updated Text");
        Post updatedPost = postService.updatePost(savedPost);

        // Assert
        Assertions.assertEquals("Updated Title", updatedPost.getTitle());
        Assertions.assertEquals("Updated Text", updatedPost.getText());
        Assertions.assertEquals(savedPost.getId(), updatedPost.getId());
    }

    @Test
    void likePost_IntegrationTest() {
        // Arrange
        Post post = new Post();
        post.setTitle("Post for Likes");
        post.setText("Content");
        post.setTags(List.of("likes"));

        Post savedPost = postService.createPost(post);

        // Act
        int likesAfterFirst = postService.likePost(savedPost.getId());
        int likesAfterSecond = postService.likePost(savedPost.getId());

        // Assert
        Assertions.assertEquals(1, likesAfterFirst);
        Assertions.assertEquals(2, likesAfterSecond);
    }

    @Test
    void searchPosts_IntegrationTest() {
        // Arrange
        Post post1 = new Post();
        post1.setTitle("Spring Framework Tutorial");
        post1.setText("Learn Spring Framework");
        post1.setTags(Arrays.asList("spring", "java"));
        postService.createPost(post1);

        Post post2 = new Post();
        post2.setTitle("Hibernate Guide");
        post2.setText("Hibernate ORM tutorial");
        post2.setTags(Arrays.asList("hibernate", "java"));
        postService.createPost(post2);

        // Act
        List<Post> springPosts = postService.getPosts(1, 10, "spring");
        List<Post> javaPosts = postService.getPosts(1, 10, "java");

        // Assert
        Assertions.assertEquals(1, springPosts.size());
        Assertions.assertEquals(2, javaPosts.size()); // Оба поста имеют тег "java"
        Assertions.assertEquals("Spring Framework Tutorial", springPosts.get(0).getTitle());
    }

    @Test
    void pagination_IntegrationTest() {
        // Arrange - создаем несколько постов
        for (int i = 1; i <= 15; i++) {
            Post post = new Post();
            post.setTitle("Post " + i);
            post.setText("Content " + i);
            post.setTags(List.of("tag" + i));
            postService.createPost(post);
        }

        // Act
        List<Post> page1 = postService.getPosts(1, 5, null);
        List<Post> page2 = postService.getPosts(2, 5, null);
        List<Post> page3 = postService.getPosts(3, 5, null);

        boolean hasPrevPage1 = postService.hasPreviousPage(1);
        boolean hasNextPage1 = postService.hasNextPage(1, 5, null);
        int lastPage = postService.getLastPage(5, null);

        // Assert
        Assertions.assertEquals(5, page1.size());
        Assertions.assertEquals(5, page2.size());
        Assertions.assertEquals(5, page3.size());

        Assertions.assertFalse(hasPrevPage1);
        Assertions.assertTrue(hasNextPage1);
        Assertions.assertEquals(3, lastPage); // 15 постов / 5 на страницу = 3 страницы
    }
}
