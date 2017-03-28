--pallets(pallet_id, cookie, production_date,blocked)
--customers(name, address)
--orders(order_id, delivery_date, placed_date, customer_name)
--order_details(cookie, amount, order_id)
--deliveries(order_id, pallet_id, delivery_date)
--cookies(name)
--recipe_entries(ingredient, amount, cookie_name)
--raw_materials(name, amount, unit, last_delivery, amount_delivered)


PRAGMA foreign_keys = off;
DROP TABLE IF EXISTS pallets;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS deliveries;
DROP TABLE IF EXISTS cookies;
DROP TABLE IF EXISTS recipe_entries;
DROP TABLE IF EXISTS raw_materials;
PRAGMA foreign_keys = on;



CREATE TABLE pallets(
	pallet_id Integer PRIMARY KEY,
	cookie_name TEXT,
	production_date DATETIME,
	blocked int default 0,
	FOREIGN KEY (cookie_name) REFERENCES cookies(cookie_name)
);

CREATE TABLE customers(
	customer_name TEXT PRIMARY KEY,
	customer_address TEXT NOT NULL
);

CREATE TABLE orders(
	order_id Integer PRIMARY KEY,
	delivery_date DATE default '0000-00-00',
	placed_date DATE,
	customer_name TEXT default 'none',
	FOREIGN KEY (customer_name) REFERENCES customers(customer_name)
);


CREATE TABLE order_details(
	cookie_name TEXT,
	amount int NOT NULL,
	order_id Integer,
	FOREIGN KEY (cookie_name) REFERENCES cookies(cookie_name),
	FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
	

CREATE TABLE deliveries(
	order_id Integer,
	pallet_id Integer,
	delivered_date DATE,
	FOREIGN KEY (order_id) REFERENCES orders(order_id),
	FOREIGN KEY (pallet_id) REFERENCES pallets(pallet_id)
);

CREATE TABLE cookies(
	cookie_name TEXT PRIMARY KEY
);

CREATE TABLE recipe_entries(
	ingredient TEXT,
	amount int,
	cookie_name TEXT,
	PRIMARY KEY (ingredient, cookie_name),
	FOREIGN KEY (cookie_name) REFERENCES cookies(cookie_name)
	FOREIGN KEY (ingredient) REFERENCES raw_materials(ingredient)
);

CREATE TABLE raw_materials(
	ingredient TEXT PRIMARY KEY,
	total_amount double NOT NULL,
	unit TEXT NOT NULL,
	last_delivery DATE,
	amount_delivered int
);
	

INSERT INTO cookies(cookie_name)
VALUES ('Nut ring'),
	('Nut cookie'),
	('Amneris'),
	('Tango'),
	('Almond delight'),
	('Berliner');

INSERT INTO raw_materials(ingredient, total_amount, unit)
VALUES ('Flour',5000,'g'),
	('Butter',5000,'g'),
	('Icing sugar',5000,'g'),
	('Roasted, chopped nuts',5000,'g'),
	('Fine-ground nuts',5000,'g'),
	('Ground, roasted nuts',5000,'g'),
	('Bread crumbs',5000,'g'),
	('Sugar',5000,'g'),
	('Egg whites',500,'dl'),
	('Chocolate',5000,'g'),
	('Marzipan',5000,'g'),
	('Eggs',5000,'g'),
	('Potato starch',5000,'g'),
	('Wheat flour',5000,'g'),
	('Sodium bicarbonate',5000,'g'),
	('Vanilla',5000,'g'),
	('Chopped almonds',5000,'g'),
	('Cinnamon',5000,'g'),
	('Vanilla sugar',5000,'g');

INSERT INTO recipe_entries(ingredient, amount, cookie_name)
VALUES ('Flour', 450, 'Nut ring'),
	('Butter', 450, 'Nut ring'),
	('Icing sugar', 190, 'Nut ring'),
	('Roasted, chopped nuts', 225, 'Nut ring'),
	('Fine-ground nuts', 750, 'Nut cookie'),
	('Ground, roasted nuts', 625, 'Nut cookie'),
	('Bread crumbs', 125, 'Nut cookie'),	
	('Sugar', 375, 'Nut cookie'),
	('Egg whites', 3.5, 'Nut cookie'),
	('Chocolate', 50, 'Nut cookie'),
	('Marzipan', 750, 'Amneris'),
	('Butter', 250, 'Amneris'),
	('Eggs', 250, 'Amneris'),
	('Potato starch', 25, 'Amneris'),
	('Wheat flour', 25, 'Amneris'),
	('Butter', 200, 'Tango'),
	('Sugar', 250, 'Tango'),
	('Flour', 300, 'Tango'),
	('Sodium bicarbonate', 4, 'Tango'),
	('Vanilla', 2, 'Tango'),
	('Butter', 400, 'Almond delight'),
	('Sugar', 270, 'Almond delight'),
	('Chopped almonds', 279, 'Almond delight'),
	('Flour', 400, 'Almond delight'),
	('Cinnamon', 10, 'Almond delight'),
	('Flour', 350, 'Berliner'),
	('Butter', 250, 'Berliner'),
	('Icing sugar', 100, 'Berliner'),
	('Eggs', 50, 'Berliner'),
	('Vanilla sugar', 5, 'Berliner'),
	('Chocolate', 50, 'Berliner');	

INSERT INTO customers(customer_name, customer_address)
VALUES ('Finkakor AB', 'Helsingborg'),
	('Småbröd AB', 'Malmö'),
	('Kaffebröd AB', 'Landskrona'),
	('Bjudkakor AB', 'Ystad'),
	('Kalaskakor AB', 'Trelleborg'),
	('Partykakor AB', 'Kristianstad'),
	('Gästkakor AB', 'Hässleholm'),
	('Skånekakor AB', 'Perstorp');

INSERT INTO orders(delivery_date, placed_date, customer_name)
VALUES ('2017-04-05', '2017-03-27', 'Småbröd AB'),
	('2017-04-04', '2017-03-27', 'Finkakor AB'),
	('2017-04-06', '2017-03-27', 'Kaffebröd AB'),
	('2017-04-03', '2017-03-27', 'Bjudkakor AB'),
	('2017-04-09', '2017-03-27', 'Småbröd AB');

INSERT INTO order_details(cookie_name, amount, order_id)
VALUES	('Nut ring', 1, 1),
	('Nut ring', 1, 2),
	('Nut ring', 1, 3),
	('Nut ring', 1, 4),
	('Nut cookie', 1, 1),
	('Nut cookie', 1, 2),
	('Amneris', 2, 3),
	('Amneris', 2, 4),
	('Amneris', 3, 5);

INSERT INTO pallets(cookie_name, production_date)
VALUES ('Nut ring', '2017-03-27 10:52:03'),
	('Nut ring', '2017-03-27 10:52:04'),
	('Nut ring', '2017-03-27 10:52:05'),
	('Nut ring', '2017-03-27 10:52:06'),
	('Nut cookie', '2017-03-27 10:52:07'),
	('Nut cookie', '2017-03-27 10:52:08'),
	('Amneris', '2017-03-27 10:52:09'),
	('Amneris', '2017-03-27 10:52:10'),
	('Amneris', '2017-03-27 10:52:11'),
	('Amneris', '2017-03-27 10:52:12'),
	('Amneris', '2017-03-27 10:52:13'),
	('Amneris', '2017-03-27 10:52:14'),
	('Amneris', '2017-03-27 10:52:15');

INSERT INTO pallets(cookie_name, production_date, blocked)
VALUES ('Tango', '2017-03-27 10:52:16', 1);


INSERT INTO deliveries(order_id, pallet_id)
VALUES	(1, 1),
	(1, 5),
	(2, 2),
	(2, 6),
	(3, 3),
	(3, 7),
	(3, 8),
	(4, 4),
	(4, 9),
	(4, 10),
	(5, 11),
	(5, 12),
	(5, 13);
	



