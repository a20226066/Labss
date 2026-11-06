-- SCRIPT DEL LABORATORIO 9 (PRACTICA EDUCATIVA)
-- Ejecuta todo este archivo en MySQL Workbench ANTES de iniciar el servidor.
-- NOTA: El PDF menciona que existe un SQL adjunto; como no fue cargado, se incluye
-- un esquema propio equivalente a las 3 tablas del enunciado + 2 de Spring Security.

CREATE DATABASE IF NOT EXISTS gtics_lab9 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gtics_lab9;

-- Equipos: un equipo puede tener muchas APIs.
CREATE TABLE IF NOT EXISTS equipos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL
);

-- APIs: la FK equipo_id permite la relación @ManyToOne en ApiCatalogo.
CREATE TABLE IF NOT EXISTS apis (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    version VARCHAR(20) NOT NULL,
    fecha_registro DATE NOT NULL,
    estado VARCHAR(20) NOT NULL,
    equipo_id INT NOT NULL,
    CONSTRAINT fk_apis_equipo FOREIGN KEY (equipo_id) REFERENCES equipos(id)
);

-- Endpoints: cada endpoint pertenece a una API.
-- ON DELETE CASCADE: al borrar una API de práctica, se borran sus endpoints.
CREATE TABLE IF NOT EXISTS endpoints (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ruta VARCHAR(255) NOT NULL,
    metodo_http VARCHAR(10) NOT NULL,
    descripcion VARCHAR(255),
    api_id INT NOT NULL,
    CONSTRAINT fk_endpoints_api FOREIGN KEY (api_id) REFERENCES apis(id) ON DELETE CASCADE
);

-- Estas dos tablas siguen la configuración por defecto de JdbcUserDetailsManager.
-- Contraseñas en BCrypt: NO son texto plano.
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities (
    username VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_user FOREIGN KEY (username) REFERENCES users(username),
    CONSTRAINT unq_authority UNIQUE (username, authority)
);

-- Datos de demostración: INSERT ... SELECT para evitar duplicados al repetir el script.
INSERT INTO equipos(nombre, area)
SELECT 'Plataforma', 'Backend'
WHERE NOT EXISTS(SELECT 1 FROM equipos WHERE nombre = 'Plataforma');

INSERT INTO equipos(nombre, area)
SELECT 'Aplicaciones', 'Mobile'
WHERE NOT EXISTS(SELECT 1 FROM equipos WHERE nombre = 'Aplicaciones');

INSERT INTO apis(nombre, version, fecha_registro, estado, equipo_id)
SELECT 'API de Usuarios', '1.0', '2026-06-15', 'activa', e.id
FROM equipos e WHERE e.nombre = 'Plataforma'
AND NOT EXISTS(SELECT 1 FROM apis WHERE nombre = 'API de Usuarios');

INSERT INTO apis(nombre, version, fecha_registro, estado, equipo_id)
SELECT 'API de Catálogo', '2.0', '2026-06-16', 'deprecada', e.id
FROM equipos e WHERE e.nombre = 'Aplicaciones'
AND NOT EXISTS(SELECT 1 FROM apis WHERE nombre = 'API de Catálogo');

INSERT INTO endpoints(ruta, metodo_http, descripcion, api_id)
SELECT '/usuarios', 'GET', 'Lista usuarios', a.id
FROM apis a WHERE a.nombre = 'API de Usuarios'
AND NOT EXISTS (SELECT 1 FROM endpoints WHERE ruta = '/usuarios' AND metodo_http = 'GET');

-- CUENTAS SOLO PARA PRACTICAR EN TU PC.
-- usuario: admin   / contraseña: Admin123!
-- usuario: usuario / contraseña: Usuario123!
-- Hashes generados con bcrypt, coste 10, compatibles con BCryptPasswordEncoder.
INSERT INTO users(username, password, enabled)
VALUES ('admin', '$2y$10$.DI6pRoGXVDw5T2Et.69DuKwVlFzJ2o23KcNcgURIKyKf8Cagifh2', TRUE)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO users(username, password, enabled)
VALUES ('usuario', '$2y$10$YOpaihl0Gd2HuqigPpY.Rui7Oez2N0z.v9DTq5ZtPMGOzziKWGS7e', TRUE)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO authorities(username, authority) VALUES ('admin', 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE username = username;
INSERT INTO authorities(username, authority) VALUES ('usuario', 'ROLE_USER')
ON DUPLICATE KEY UPDATE username = username;

-- Comprobación visual:
SELECT * FROM equipos;
SELECT * FROM apis;
SELECT * FROM endpoints;
SELECT username, enabled FROM users;
SELECT * FROM authorities;
