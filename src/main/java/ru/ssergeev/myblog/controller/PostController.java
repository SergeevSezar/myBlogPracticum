package ru.ssergeev.myblog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.ssergeev.myblog.model.Post;
import ru.ssergeev.myblog.service.PostService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam String search,
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {

        List<Post> posts = postService.getPosts(pageNumber, pageSize, search);
        boolean hasPrev = postService.hasPreviousPage(pageNumber);
        boolean hasNext = postService.hasNextPage(pageNumber, pageSize, search);
        int lastPage = postService.getLastPage(pageSize, search);

        return ResponseEntity.ok(Map.of(
                "posts", posts,
                "hasPrev", hasPrev,
                "hasNext", hasNext,
                "lastPage", lastPage
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Optional<Post> post = postService.getPost(id);
        return post.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        if (!id.equals(post.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Post updatedPost = postService.updatePost(post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> likePost(@PathVariable Long id) {
        int likes = postService.likePost(id);
        return ResponseEntity.ok(likes);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updatePostImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {
        try {
            postService.updatePostImage(id, image.getBytes());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) {
        byte[] image = postService.getPostImage(id);
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(image);
    }
}
