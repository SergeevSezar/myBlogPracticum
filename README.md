MyBlog Backend Application (Spring Boot)
Spring Boot-based backend for a blog application with REST API.

📋 О проекте
Бэкенд-приложение для блога, реализованное на Spring Boot 3.2+. Приложение предоставляет REST API для управления постами, комментариями, лайками и изображениями. Мигрировано с Spring Framework на Spring Boot с сохранением всей функциональности.

🚀 Функциональность
✅ Создание, редактирование, удаление постов

✅ Пагинация и поиск постов

✅ Комментарии к постам

✅ Лайки для постов

✅ Загрузка и отображение изображений

✅ REST API с валидацией

✅ In-memory база данных H2

✅ Юнит и интеграционные тесты с Spring Boot Test

✅ Executable JAR со встроенным Tomcat

🛠 Технологии
Java 21

Spring Boot 3.2.0

Spring Web, Spring Data JDBC, Spring Boot Test

H2 Database (in-memory)

Gradle для сборки

JUnit 5, Mockito для тестирования

Embedded Tomcat (встроенный сервлет-контейнер)

🔄 Миграционные изменения
Что было изменено при переходе на Spring Boot:
✅ Заменен Maven на Gradle для сборки

✅ Удалены ручные конфигурации Spring (@Configuration классы)

✅ Удален web.xml и конфигурация деплоя WAR

✅ Добавлен встроенный Tomcat сервер

✅ Тесты переписаны на Spring Boot Test

✅ Добавлена автоконфигурация Spring Boot

✅ Приложение запускается как standalone JAR

🌐 API Endpoints

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

Комментарии
Метод	Endpoint	Описание
GET	/api/posts/{postId}/comments	Комментарии поста
GET	/api/posts/{postId}/comments/{commentId}	Получить комментарий
POST	/api/posts/{postId}/comments	Добавить комментарий
PUT	/api/posts/{postId}/comments/{commentId}	Обновить комментарий
DELETE	/api/posts/{postId}/comments/{commentId}	Удалить комментарий
