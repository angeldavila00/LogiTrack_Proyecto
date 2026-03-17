## Proyecto Spring Boot LogiTrack

## BASE DE DATOS MYSQL

````mysql
-- ==========================
-- CREAR BASE DE DATOS
-- ==========================
CREATE DATABASE IF NOT EXISTS logitrack_davila;
USE logitrack_davila;
show tables;

-- ==========================
-- TABLA USUARIOS
-- ==========================
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    documento VARCHAR(50),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN','EMPLEADO') NOT NULL
);

-- ==========================
-- TABLA BODEGAS
-- ==========================
CREATE TABLE bodega (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150),
    capacidad INT,
    encargado_id INT,
    
    FOREIGN KEY (encargado_id) REFERENCES usuario(id)
);

-- ==========================
-- TABLA PRODUCTOS
-- ==========================
CREATE TABLE producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    categoria VARCHAR(100),
    precio DECIMAL(10,2),
    stock INT DEFAULT 0,
    bodega_id INT,
    
    FOREIGN KEY (bodega_id) REFERENCES bodega(id)
);

-- ==========================
-- TABLA MOVIMIENTOS
-- ==========================
CREATE TABLE movimiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    tipo_movimiento ENUM('ENTRADA','SALIDA','TRANSFERENCIA') NOT NULL,
    
    usuario_id INT,
    bodega_origen_id INT,
    bodega_destino_id INT,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (bodega_origen_id) REFERENCES bodega(id),
    FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id)
);

-- ==========================
-- TABLA MOVIMIENTO DETALLES
-- ==========================
CREATE TABLE movimiento_detalle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    movimiento_id INT,
    producto_id INT,
    
    FOREIGN KEY (movimiento_id) REFERENCES movimiento(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- ==========================
-- TABLA AUDITORIAS
-- ==========================
CREATE TABLE auditoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    entidad VARCHAR(100),
    operacion ENUM('INSERT','UPDATE','DELETE'),
    fecha DATETIME,
    valor_anterior VARCHAR(255),
    valor_nuevo VARCHAR(255),
    usuario_id INT,
    usuario_nombre VARCHAR(100) NULL,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE SET NULL
);

-- ==============================
-- Usuario inicial ADMINISTRADOR
-- ==============================
INSERT INTO usuario (id, nombre, documento, username, password, rol)
VALUES (11, 'admin', '1324321', 'admin', '$2a$10$XYBc.2qoVcM0JL9vY02LbOa7Adk/gv7S.R0MTDGvAK0BejwAAziGi', 'ADMIN');

-- ==========================
-- USUARIOS (password: 123456)
-- ==========================
INSERT INTO usuario (nombre, documento, username, password, rol) VALUES
('Carlos Mendoza',    '1001234567', 'cmendoza',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'ADMIN'),
('Laura Jiménez',     '1009876543', 'ljimenez',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'ADMIN'),
('Pedro Ramírez',     '1007654321', 'pramirez',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'EMPLEADO'),
('Sofía Torres',      '1005432198', 'storres',    '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'EMPLEADO'),
('Miguel Herrera',    '1003219876', 'mherrera',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'EMPLEADO');

-- ==========================
-- BODEGAS
-- ==========================
INSERT INTO bodega (nombre, ubicacion, capacidad, encargado_id) VALUES
('Bodega Central',      'Bogotá - Zona Industrial',     500, 12),
('Bodega Norte',        'Medellín - Sector Laureles',   300, 13),
('Bodega Sur',          'Cali - Zona Franca',           400, 14),
('Bodega Oriente',      'Bucaramanga - Centro',         250, 15),
('Bodega Occidente',    'Barranquilla - Puerto',        350, 16);

-- ==========================
-- PRODUCTOS
-- ==========================
INSERT INTO producto (nombre, categoria, precio, stock, bodega_id) VALUES
('Laptop Dell Inspiron',        'Tecnología',    2500000, 45,  1),
('Monitor Samsung 24"',         'Tecnología',     850000, 30,  1),
('Teclado Mecánico Logitech',   'Accesorios',     320000, 60,  1),
('Mouse Inalámbrico HP',        'Accesorios',     95000,  80,  2),
('Silla Ergonómica Ejecutiva',  'Mobiliario',    1200000, 20,  2),
('Escritorio de Madera 1.5m',   'Mobiliario',     750000, 15,  2),
('Impresora Epson L3150',       'Tecnología',     650000, 25,  3),
('Papel Resma 500 hojas',       'Papelería',       18000, 200, 3),
('Archivador Metálico 4 gav.',  'Mobiliario',     480000, 12,  3),
('Auriculares Sony WH-1000',    'Accesorios',     890000, 18,  4),
('Cámara IP Hikvision',         'Seguridad',      420000, 22,  4),
('UPS APC 1200VA',              'Tecnología',     580000, 14,  4),
('Cable UTP Cat6 (rollo 100m)', 'Redes',           95000, 50,  5),
('Switch Cisco 24 puertos',     'Redes',          980000,  8,  5),
('Router Mikrotik RB750',       'Redes',          320000, 16,  5);

-- ==========================
-- MOVIMIENTOS
-- ==========================
INSERT INTO movimiento (fecha, tipo_movimiento, usuario_id, bodega_origen_id, bodega_destino_id) VALUES
                                                                                                     ('2026-01-10 08:30:00', 'ENTRADA',       12, 1, 1),
                                                                                                     ('2026-01-15 10:00:00', 'ENTRADA',       13, 2, 2),
                                                                                                     ('2026-02-03 14:20:00', 'TRANSFERENCIA', 12, 1, 3),
                                                                                                     ('2026-02-18 09:45:00', 'SALIDA',        14, 3, 3),
                                                                                                     ('2026-03-05 11:15:00', 'ENTRADA',       15, 4, 4),
                                                                                                     ('2026-03-10 16:00:00', 'TRANSFERENCIA', 12, 2, 5),
                                                                                                     ('2026-03-12 08:00:00', 'SALIDA',        16, 5, 5);


USE logitrack_davila;

-- ==========================
-- USUARIOS (password: 123456)
-- ==========================
INSERT INTO usuario (nombre, documento, username, password, rol) VALUES
('Carlos Mendoza',    '1001234567', 'cmendoza',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'ADMIN'),
('Laura Jiménez',     '1009876543', 'ljimenez',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'ADMIN'),
('Pedro Ramírez',     '1007654321', 'pramirez',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'EMPLEADO'),
('Sofía Torres',      '1005432198', 'storres',    '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'EMPLEADO'),
('Miguel Herrera',    '1003219876', 'mherrera',   '$2a$10$iZtniY0NARMMtlKCccsQkuTVAuSUSIzc1wTdH2.U1awuwpZxNbVh2', 'EMPLEADO');

-- ==========================
-- BODEGAS
-- ==========================
INSERT INTO bodega (nombre, ubicacion, capacidad, encargado_id) VALUES
('Bodega Central',      'Bogotá - Zona Industrial',     500, 12),
('Bodega Norte',        'Medellín - Sector Laureles',   300, 13),
('Bodega Sur',          'Cali - Zona Franca',           400, 14),
('Bodega Oriente',      'Bucaramanga - Centro',         250, 15),
('Bodega Occidente',    'Barranquilla - Puerto',        350, 16);

-- ==========================
-- PRODUCTOS
-- ==========================
INSERT INTO producto (nombre, categoria, precio, stock, bodega_id) VALUES
('Laptop Dell Inspiron',        'Tecnología',    2500000, 45,  1),
('Monitor Samsung 24"',         'Tecnología',     850000, 30,  1),
('Teclado Mecánico Logitech',   'Accesorios',     320000, 60,  1),
('Mouse Inalámbrico HP',        'Accesorios',     95000,  80,  2),
('Silla Ergonómica Ejecutiva',  'Mobiliario',    1200000, 20,  2),
('Escritorio de Madera 1.5m',   'Mobiliario',     750000, 15,  2),
('Impresora Epson L3150',       'Tecnología',     650000, 25,  3),
('Papel Resma 500 hojas',       'Papelería',       18000, 200, 3),
('Archivador Metálico 4 gav.',  'Mobiliario',     480000, 12,  3),
('Auriculares Sony WH-1000',    'Accesorios',     890000, 18,  4),
('Cámara IP Hikvision',         'Seguridad',      420000, 22,  4),
('UPS APC 1200VA',              'Tecnología',     580000, 14,  4),
('Cable UTP Cat6 (rollo 100m)', 'Redes',           95000, 50,  5),
('Switch Cisco 24 puertos',     'Redes',          980000,  8,  5),
('Router Mikrotik RB750',       'Redes',          320000, 16,  5);

-- ==========================
-- MOVIMIENTOS
-- ==========================
INSERT INTO movimiento (fecha, tipo_movimiento, usuario_id, bodega_origen_id, bodega_destino_id) VALUES
                                                                                                     ('2026-01-10 08:30:00', 'ENTRADA',       12, 1, 1),
                                                                                                     ('2026-01-15 10:00:00', 'ENTRADA',       13, 2, 2),
                                                                                                     ('2026-02-03 14:20:00', 'TRANSFERENCIA', 12, 1, 3),
                                                                                                     ('2026-02-18 09:45:00', 'SALIDA',        14, 3, 3),
                                                                                                     ('2026-03-05 11:15:00', 'ENTRADA',       15, 4, 4),
                                                                                                     ('2026-03-10 16:00:00', 'TRANSFERENCIA', 12, 2, 5),
                                                                                                     ('2026-03-12 08:00:00', 'SALIDA',        16, 5, 5);

-- ==========================
-- MOVIMIENTO DETALLES
-- ==========================
INSERT INTO movimiento_detalle (cantidad, movimiento_id, producto_id) VALUES
(10, 1, 1),
(15, 1, 2),
(20, 2, 4),
(10, 2, 5),
(5,  3, 3),
(8,  3, 7),
(3,  4, 6),
(50, 4, 8),
(7,  5, 10),
(5,  5, 11),
(10, 6, 13),
(4,  6, 14),
(6,  7, 12),
(20, 7, 8);

````