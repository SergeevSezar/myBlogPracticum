package ru.ssergeev.myblog.service;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.ssergeev.myblog.controller.PostController;
import ru.ssergeev.myblog.model.Post;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PostService postService;

    @Test
    public void getPosts_ShouldReturnPosts() throws Exception {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setText("Test Content");

        when(postService.getPosts(anyInt(), anyInt(), anyString()))
                .thenReturn(List.of(post));
        when(postService.hasPreviousPage(anyInt())).thenReturn(false);
        when(postService.hasNextPage(anyInt(), anyInt(), anyString())).thenReturn(false);
        when(postService.getLastPage(anyInt(), anyString())).thenReturn(1);

        // Act & Assert
        mockMvc.perform(get("/api/posts")
                        .param("search", "test")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.posts[0].title").value("Test Post"))
                .andExpect((ResultMatcher) jsonPath("$.hasPrev").value(false))
                .andExpect((ResultMatcher) jsonPath("$.hasNext").value(false));
    }

    @Test
    public void createPost_ShouldReturnCreatedPost() throws Exception {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setTitle("New Post");
        post.setText("New Content");

        when(postService.createPost(any(Post.class))).thenReturn(post);

        // Act & Assert
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value(1))
                .andExpect((ResultMatcher) jsonPath("$.title").value("New Post"));
    }

    @Test
    public void getPost_ShouldReturnPost() throws Exception {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");

        when(postService.getPost(1L)).thenReturn(Optional.of(post));

        // Act & Assert
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value(1))
                .andExpect((ResultMatcher) jsonPath("$.title").value("Test Post"));
    }

    @Test
    public void getPost_NotFound_ShouldReturn404() throws Exception {
        // Arrange
        when(postService.getPost(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }
}
