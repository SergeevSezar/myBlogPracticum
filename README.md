# myBlogPracticum
Создайте приложение-блог с использованием Spring Framework

MyBlog Backend app
Spring Framework-based backend for a blog application with REST API.

📋 О проекте
Бэкенд-приложение для блога, реализованное на Spring Framework 6.1+ без использования Spring Boot. Приложение предоставляет REST API для управления постами, комментариями, лайками и изображениями.

🚀 Функциональность
✅ Создание, редактирование, удаление постов

✅ Пагинация и поиск постов

✅ Комментарии к постам

✅ Лайки для постов

✅ Загрузка и отображение изображений

✅ REST API с валидацией

✅ In-memory база данных H2

✅ Юнит и интеграционные тесты

🛠 Технологии
Java 21

Spring Framework 6.1.0

Spring MVC, Spring JDBC

H2 Database (in-memory)

Maven для сборки

JUnit 5, Mockito для тестирования

Tomcat 10+ / Jetty 11+ для деплоя

📚 API Endpoints
Посты
Метод	Endpoint	Описание
GET	/api/posts?search=текст&pageNumber=1&pageSize=10	Список постов с пагинацией
GET	/api/posts/{id}	Получить пост по ID
POST	/api/posts	Создать новый пост
PUT	/api/posts/{id}	Обновить пост
DELETE	/api/posts/{id}	Удалить пост
POST	/api/posts/{id}/likes	Поставить лайк
PUT	/api/posts/{id}/image	Загрузить изображение
GET	/api/posts/{id}/image	Получить изображение

🚀 Быстрый старт
Предварительные требования
Java 21 или выше

Maven 3.6+

Tomcat 10+ или Jetty 11+

🌐 Деплой в сервлет-контейнер

Установка Tomcat
Скачайте Tomcat 10+ с официального сайта

Распакуйте в удобную директорию

Деплой в Tomcat

🧪 Тестирование
Запуск тестов
bash
# Все тесты
mvn test

# С генерацией отчета
mvn test surefire-report:report
Структура тестов
Unit тесты: *Test.java - тестирование сервисов с Mockito

Интеграционные тесты: *IntegrationTest.java - тестирование с реальной БД

Тестовые данные: src/test/resources/db/test-data.sql