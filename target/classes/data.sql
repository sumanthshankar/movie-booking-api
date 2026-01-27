---- Cities
INSERT INTO city (id, name) VALUES (1, 'Bengaluru');
INSERT INTO city (id, name) VALUES (2, 'Hyderabad');

---- Movies
INSERT INTO movie (id, name, language) VALUES (1, 'KGF', 'Kannada');
INSERT INTO movie (id, name, language) VALUES (2, 'Pushpa', 'Telugu');

---- Theatres
INSERT INTO theatre (id, name, city_id) VALUES (1, 'Orion Bengaluru', 1);
INSERT INTO theatre (id, name, city_id) VALUES (2, 'INOX Hyderabad', 2);

---- Shows
INSERT INTO show (id, movie_id, theatre_id, show_date, show_time, price) VALUES (1, 1, 1, '2026-01-28', '18:30:00', 250.00);
INSERT INTO show (id, movie_id, theatre_id, show_date, show_time, price) VALUES (2, 2, 2, '2026-01-28', '18:30:00', 250.00);
INSERT INTO show (id, movie_id, theatre_id, show_date, show_time, price) VALUES (3, 1, 1, '2026-01-28', '18:30:00', 250.00);
INSERT INTO show (id, movie_id, theatre_id, show_date, show_time, price) VALUES (4, 2, 2, '2026-01-28', '18:30:00', 250.00);

---- Seats
INSERT INTO seat (id, show_id, seat_number, status, version) VALUES (1, 1, 'A1', 'AVAILABLE', 1);
INSERT INTO seat (id, show_id, seat_number, status, version) VALUES (2, 1, 'A2', 'AVAILABLE', 2);
INSERT INTO seat (id, show_id, seat_number, status, version) VALUES (3, 1, 'A3', 'AVAILABLE', 3);
INSERT INTO seat (id, show_id, seat_number, status, version) VALUES (4, 1, 'A4', 'AVAILABLE', 4);
INSERT INTO seat (id, show_id, seat_number, status, version) VALUES (5, 1, 'A5', 'AVAILABLE', 5);