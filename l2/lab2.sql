PRAGMA FOREIGN_KEYS = off;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS theaters;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS performances;
DROP TABLE IF EXISTS movies;

PRAGMA FOREIGN_KEYS = on;

-- Create the tables.

CREATE TABLE users (
	username TEXT PRIMARY KEY,
	name TEXT NOT NULL,
	address TEXT,
	phone_nbr TEXT NOT NULL

);

CREATE TABLE bookings (
	reservation_nbr integer PRIMARY KEY,
	username TEXT,
	movie_name TEXT,
	date DATE,
	FOREIGN KEY (movie_name, date) REFERENCES performances(movie_name, date),
	FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE theaters(
	theater_name TEXT PRIMARY KEY,
	max_seats integer NOT NULL

);

CREATE TABLE performances(
	movie_name TEXT,
	theater_name TEXT,
	date DATE,
	PRIMARY KEY (movie_name, date),
	FOREIGN KEY (movie_name) REFERENCES movies(movie_name),
	FOREIGN KEY (theater_name) REFERENCES theaters(theater_name)
);

CREATE TABLE movies(
	movie_name TEXT PRIMARY KEY
);

INSERT INTO movies (movie_name)
VALUES ('The Godfather'),
	('The Shawshank Redemption'),
	('The Dark Knight'),
	('12 Angry Men'),
	('Pulp Fiction');

INSERT INTO users (username, name, address, phone_nbr)
VALUES ('Kalle', 'Karl', 'Eslövsvägen 666, Eslöv', '123456789'),
	('Kevve', 'Kevin', 'SåSjuktLostVägen -1', '123456789'),
	('Robbe', 'Robin', 'Aiolivägen 1337', '9876654');

INSERT INTO users (username, name, phone_nbr)
VALUES ('Abbe', 'Albin', '123123123');

INSERT INTO theaters (theater_name, max_seats)
VALUES ('SF LTH', 100),
	('SF EHL', 10),
	('SF SOL', 30),
	('SF BMC', 400),
	('SF NATFAK', 87);

INSERT INTO performances (movie_name, theater_name, date)
VALUES ('The Dark Knight', 'SF LTH', '2017-02-01'),
	('The Dark Knight', 'SF LTH', '2017-02-02'),
	('The Godfather', 'SF EHL', '2017-02-01'),
	('The Shawshank Redemption', 'SF SOL', '2017-02-01'),
	('12 Angry Men', 'SF BMC', '2017-02-02'),
	('Pulp Fiction', 'SF NATFAK', '2017-02-02');
