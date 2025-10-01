-- Вставка тестовых постов
INSERT INTO posts (title, text, tags, likes_count, created_at, updated_at) VALUES
                                                                               ('Первый пост', 'Это мой первый пост в блоге. Здесь я расскажу о своих впечатлениях от Spring Framework.',
                                                                                ARRAY['spring', 'java', 'blog'], 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                               ('Второй пост о H2', 'H2 - отличная in-memory база данных для разработки и тестирования. Легко настраивается и работает быстро.',
                                                                                ARRAY['h2', 'database', 'development'], 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

                                                                               ('Spring Boot Tutorial', 'В этом посте я расскажу как работать с Spring Boot и создавать REST API.',
                                                                                ARRAY['spring', 'boot', 'tutorial'], 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Вставка тестовых комментариев
INSERT INTO comments (text, post_id, created_at) VALUES
                                                     ('Отличный пост! Спасибо за информацию.', 1, CURRENT_TIMESTAMP),
                                                     ('Жду продолжения!', 1, CURRENT_TIMESTAMP),
                                                     ('H2 действительно удобная база для разработки', 2, CURRENT_TIMESTAMP),
                                                     ('Отличный туториал по Spring Boot!', 3, CURRENT_TIMESTAMP),
                                                     ('Понравилось объяснение про автоконфигурацию', 3, CURRENT_TIMESTAMP);