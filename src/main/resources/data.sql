INSERT INTO Usuario (id, email, password, paquetes, paquetesPremium, monedas, activo, rol)
VALUES (1, 'eze@test.com', '123456789', 200, 200, 100, true, 'USER');


INSERT INTO Figurita (id, nombre, seleccion, rareza, score, imagen_url, ordenAlbum)
VALUES
    (1, 'Lionel Messi', 'Argentina', 'LEYENDA', 98, '/spring/img/players/FIG_MESSI.png', 10),
    (2, 'Emiliano Martinez', 'Argentina', 'ORO', 90, null, 1),
    (3, 'Angel Di Maria', 'Argentina', 'LEYENDA', 92, null, 11),
    (4, 'Julian Alvarez', 'Argentina', 'ORO', 85, null, 9),
    (5, 'Nicolas Otamendi', 'Argentina', 'PLATA', 84, null, 3),
    (6, 'Cristiano Ronaldo', 'Portugal', 'LEYENDA', 96, '/spring/img/players/FIG_RONALDO.png', 7),
    (7, 'Kylian Mbappé', 'Francia', 'LEYENDA', 97, null, 8),
    (8, 'Kevin De Bruyne', 'Bélgica', 'ORO', 91, null, 6),
    (9, 'Neymar Jr', 'Brasil', 'LEYENDA', 90, null, 12),
    (10, 'Marco Ruben', 'Argentina', 'COMUN', 83, null, 17),
    (11, 'Erling Haaland', 'Noruega', 'ORO', 92, null, 15),
    (12, 'Virgil van Dijk', 'Países Bajos', 'PLATA', 88, null, 4),
    (13, 'Alejandro Garnacho', 'Argentina', 'PLATA', 82, null, 14),
    (14, 'Enzo Fernandez', 'Argentina', 'PLATA', 85, null, 5),
    (15, 'Jaminton Campaz', 'Colombia', 'PLATA', 80, null, 16),
    (16, 'Facundo Medina', 'Argentina', 'COMUN', 79, null, 2),
    (17, 'Nico Paz', 'Argentina', 'COMUN', 78, null, 13);