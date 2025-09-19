package ru.ssergeev.myblog.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String text;
    private Long postId;
    private LocalDateTime createdAt;
}
