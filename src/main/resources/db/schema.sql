CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       text TEXT NOT NULL,
                       tags VARCHAR(255) ARRAY,
                       likes_count INT DEFAULT 0,
                       image BLOB,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
);

CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          text TEXT NOT NULL,
                          post_id BIGINT NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);