INSERT INTO posts (id, title, text, tags, likes_count, created_at, updated_at) VALUES
                                                                                   (100, 'Test Post 1', 'Content for test post 1', ARRAY['test', 'java'], 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                   (101, 'Test Post 2', 'Content for test post 2', ARRAY['spring', 'test'], 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                   (102, 'Another Post', 'Another content', ARRAY['another'], 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO comments (id, text, post_id, created_at) VALUES
                                                         (100, 'Test comment 1', 100, CURRENT_TIMESTAMP),
                                                         (101, 'Test comment 2', 100, CURRENT_TIMESTAMP),
                                                         (102, 'Comment for post 2', 101, CURRENT_TIMESTAMP);