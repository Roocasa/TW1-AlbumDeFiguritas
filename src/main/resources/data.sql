INSERT INTO Usuario (id, email, password, paquetes, paquetesPremium, monedas, activo, rol, pais)
VALUES (1, 'eze@test.com', '123456789', 200, 200, 100, true, 'USER', 'Argentina');


INSERT INTO Figurita (id, nombre, seleccion, rareza, score, imagen_url, bandera_url, ordenAlbum)
VALUES
    (1, 'Lionel Messi', 'Argentina', 'LEYENDA', 98, '/spring/img/players/FIG_MESSI.png','/spring/img/flags/ARG_FLAG.png' , 1),
    (2, 'Emiliano Martinez', 'Argentina', 'ORO', 90, null,'/spring/img/flags/ARG_FLAG.png', 2),
    (3, 'Angel Di Maria', 'Argentina', 'LEYENDA', 92, null,'/spring/img/flags/ARG_FLAG.png', 3),
    (4, 'Julian Alvarez', 'Argentina', 'ORO', 85, null, '/spring/img/flags/ARG_FLAG.png',4),
    (5, 'Nicolas Otamendi', 'Argentina', 'PLATA', 84, null, '/spring/img/flags/ARG_FLAG.png',5),
    (10, 'Marco Ruben', 'Argentina', 'COMUN', 83, null,'/spring/img/flags/ARG_FLAG.png', 6),
    (13, 'Alejandro Garnacho', 'Argentina', 'PLATA', 82, null,'/spring/img/flags/ARG_FLAG.png', 7),
    (14, 'Enzo Fernandez', 'Argentina', 'PLATA', 85, null, '/spring/img/flags/ARG_FLAG.png',8),
    (16, 'Facundo Medina', 'Argentina', 'COMUN', 79, null,'/spring/img/flags/ARG_FLAG.png', 9),
    (17, 'Nico Paz', 'Argentina', 'COMUN', 78, null, '/spring/img/flags/ARG_FLAG.png',10),
    (6, 'Cristiano Ronaldo', 'Portugal', 'LEYENDA', 96, '/spring/img/players/FIG_RONALDO.png', '/spring/img/flags/PORT_FLAG.png',11),
    (7, 'Kylian Mbappe', 'Francia', 'LEYENDA', 97, null, null,12),
    (8, 'Kevin De Bruyne', 'Bélgica', 'ORO', 91, null, null,13),
    (9, 'Neymar Jr', 'Brasil', 'LEYENDA', 90, null, '/spring/img/flags/BRA_FLAG.png',14),
    (11, 'Erling Haaland', 'Noruega', 'ORO', 92, null, null, 15),
    (12, 'Virgil van Dijk', 'Países Bajos', 'PLATA', 88, null, null, 16),
    (15, 'Jaminton Campaz', 'Colombia', 'PLATA', 80, null, null, 17);
