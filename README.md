# java-filmorate
Template repository for Filmorate project.

## Database Schema

Ниже представлена схема базы данных приложения:

![Database Schema](docs/database-diagram.png)

### Пояснение к схеме

- Таблица `users` хранит информацию о пользователях, включая их email, логин, и дату рождения.
- Таблица `films` содержит данные о фильмах, такие как название, описание, дата выхода и длительность.
- Таблица `film_genres` связывает фильмы с жанрами, информация о которых содержится в таблице `genre_info`.
- Таблица `film_rating` хранит возрастные ограничения для фильмов (например, G, PG, R).
- Таблица `user_friends` хранит данные о дружбе пользователей, включая статус (подтверждена или нет).
- Таблица `film_likes` содержит информацию о фильмах, которые понравились пользователям.

### Примеры SQL-запросов

**1. Получить список всех фильмов с их жанрами и рейтингами:**
```sql
SELECT f.name AS film_name, gi.name AS genre_name, fr.rating AS film_rating
FROM films f
LEFT JOIN film_genres fg ON f.film_id = fg.film_id
LEFT JOIN genre_info gi ON fg.genre_id = gi.genre_id
LEFT JOIN film_rating fr ON f.rating_id = fr.rating_id;
