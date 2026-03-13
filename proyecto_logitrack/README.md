## Proyecto Spring Boot LogiTrack

````mysql
-- ==========================
-- CREAR BASE DE DATOS
-- ==========================
CREATE DATABASE IF NOT EXISTS logitrack_db;
USE logitrack_db;
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
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
````