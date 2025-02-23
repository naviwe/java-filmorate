 DROP TABLE IF EXISTS USERS CASCADE;
 DROP TABLE IF EXISTS FILMS CASCADE;
 DROP TABLE IF EXISTS FILM_GENRES CASCADE;
 DROP TABLE IF EXISTS FILM_LIKES CASCADE;
 DROP TABLE IF EXISTS FILM_RATING CASCADE;
 DROP TABLE IF EXISTS USER_FRIENDS CASCADE;
 DROP TABLE IF EXISTS GENRE_INFO CASCADE;

 CREATE TABLE IF NOT EXISTS users (
 user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
 user_name varchar,
 email varchar(320),
 login varchar(20),
 birthday date
);

CREATE TABLE IF NOT EXISTS film_rating (
rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
rating varchar(5)
);

CREATE TABLE IF NOT EXISTS films (
film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
film_name varchar,
description varchar(200),
release_date date,
duration int,
rating_id INTEGER REFERENCES film_rating(rating_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
user_id INTEGER REFERENCES users(user_id),
film_id INTEGER REFERENCES films (film_id)
);

CREATE TABLE IF NOT EXISTS user_friends (
user_id INTEGER REFERENCES users(user_id),
friend_id INTEGER REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS genre_info (
genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
genre_name varchar(20)
);

CREATE TABLE IF NOT EXISTS film_genres (
film_id INTEGER REFERENCES films(film_id),
genre_id INTEGER REFERENCES genre_info(genre_id)
);