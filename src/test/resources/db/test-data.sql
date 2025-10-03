-- Очистка таблиц
DELETE FROM comments;
DELETE FROM posts;

-- Вставка тестовых данных
INSERT INTO posts (id, title, text, tags, likes_count, created_at, updated_at) VALUES
                                                                                   (1, 'Test Post 1', 'Content for test post 1', '{"test","java"}', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                   (2, 'Test Post 2', 'Content for test post 2', '{"spring","test"}', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                   (3, 'Spring Post', 'Spring framework content', '{"spring","boot"}', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO comments (id, text, post_id, created_at) VALUES
                                                         (1, 'Test comment 1', 1, CURRENT_TIMESTAMP),
                                                         (2, 'Test comment 2', 1, CURRENT_TIMESTAMP),
                                                         (3, 'Comment for post 2', 2, CURRENT_TIMESTAMP);