-- Создание таблицы posts
CREATE TABLE IF NOT EXISTS posts (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    tags VARCHAR(255) ARRAY,
    likes_count INT DEFAULT 0,
    image BLOB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Создание таблицы comments
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        text TEXT NOT NULL,
                                        post_id BIGINT NOT NULL,
                                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
    );

-- Создание индексов для улучшения производительности
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts(created_at);
CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments(post_id);
CREATE INDEX IF NOT EXISTS idx_comments_created_at ON comments(created_at);