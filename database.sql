-- ================================================
-- AVISO: este script esta DESACTUALIZADO.
-- Usa sintaxis de MySQL (AUTO_INCREMENT, ENGINE=InnoDB), pero el proyecto
-- ahora usa PostgreSQL/Supabase. Hibernate (ddl-auto=update) crea y
-- actualiza las tablas automaticamente al arrancar la app, asi que este
-- archivo NO es necesario para desplegar. Se deja solo como referencia
-- historica del modelo de datos.
-- ================================================

-- ================================================
-- BASE DE DATOS HAPPY PAWS - VETERINARIA
-- ================================================
-- Script generado automáticamente desde entidades JPA
-- Database: VeterinariaDB

-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS VeterinariaDB;
USE VeterinariaDB;

-- ================================================
-- TABLA: documento_tipos
-- ================================================
CREATE TABLE IF NOT EXISTS documento_tipos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(150) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: roles
-- ================================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (id),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: users
-- ================================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: user_roles (relación Many-to-Many)
-- ================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: clientes
-- ================================================
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    documento_tipo_codigo VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    razon_social VARCHAR(150) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(30),
    email VARCHAR(150),
    PRIMARY KEY (id),
    INDEX idx_numero_documento (numero_documento),
    INDEX idx_documento_tipo (documento_tipo_codigo),
    INDEX idx_razon_social (razon_social)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: proveedores
-- ================================================
CREATE TABLE IF NOT EXISTS proveedores (
    id BIGINT NOT NULL AUTO_INCREMENT,
    documento_tipo_codigo VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    razon_social VARCHAR(150) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(30),
    email VARCHAR(150),
    PRIMARY KEY (id),
    INDEX idx_numero_documento (numero_documento),
    INDEX idx_documento_tipo (documento_tipo_codigo),
    INDEX idx_razon_social (razon_social)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: pets (mascotas)
-- ================================================
CREATE TABLE IF NOT EXISTS pets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    especie VARCHAR(80) NOT NULL,
    raza VARCHAR(80),
    edad INT,
    cliente_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
    INDEX idx_cliente_id (cliente_id),
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: appointments (citas)
-- ================================================
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE,
    INDEX idx_pet_id (pet_id),
    INDEX idx_cliente_id (cliente_id),
    INDEX idx_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: historial_mascotas (historia clínica)
-- ================================================
CREATE TABLE IF NOT EXISTS historial_mascotas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pet_id BIGINT NOT NULL,
    appointment_id BIGINT,
    titulo VARCHAR(150) NOT NULL,
    detalle VARCHAR(1500) NOT NULL,
    diagnostico VARCHAR(500),
    tratamiento VARCHAR(500),
    fecha_registro DATE NOT NULL,
    fecha_expiracion DATE NOT NULL,
    archivo_original VARCHAR(255),
    archivo_guardado VARCHAR(255),
    archivo_tipo VARCHAR(120),
    archivo_tamanio BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE SET NULL,
    INDEX idx_historial_pet_id (pet_id),
    INDEX idx_historial_fecha_registro (fecha_registro),
    INDEX idx_historial_fecha_expiracion (fecha_expiracion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: unidades (units)
-- ================================================
CREATE TABLE IF NOT EXISTS units (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(150) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: afectacion_tipos
-- ================================================
CREATE TABLE IF NOT EXISTS afectacion_tipos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    letra VARCHAR(1) NOT NULL,
    porcentaje DECIMAL(5, 2) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: productos
-- ================================================
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    unidad_id BIGINT NOT NULL,
    afectacion_tipo_id BIGINT NOT NULL,
    precio_unitario DECIMAL(12, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    imagen VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (unidad_id) REFERENCES units(id) ON DELETE RESTRICT,
    FOREIGN KEY (afectacion_tipo_id) REFERENCES afectacion_tipos(id) ON DELETE RESTRICT,
    INDEX idx_codigo (codigo),
    INDEX idx_nombre (nombre),
    INDEX idx_unidad_id (unidad_id),
    INDEX idx_afectacion_tipo_id (afectacion_tipo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: comprobante_tipos
-- ================================================
CREATE TABLE IF NOT EXISTS comprobante_tipos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(150) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: comprobante_series
-- ================================================
CREATE TABLE IF NOT EXISTS comprobante_series (
    id BIGINT NOT NULL AUTO_INCREMENT,
    comprobante_tipo_codigo VARCHAR(20) NOT NULL,
    serie VARCHAR(10) NOT NULL,
    correlativo INT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_comprobante_tipo_codigo (comprobante_tipo_codigo),
    INDEX idx_serie (serie),
    UNIQUE KEY uk_tipo_serie (comprobante_tipo_codigo, serie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: compras
-- ================================================
CREATE TABLE IF NOT EXISTS compras (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(20) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    proveedor_id BIGINT NOT NULL,
    comprobante_tipo_codigo VARCHAR(20) NOT NULL,
    forma_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'registrada',
    total DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id),
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id) ON DELETE RESTRICT,
    INDEX idx_numero (numero),
    INDEX idx_fecha (fecha),
    INDEX idx_proveedor_id (proveedor_id),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: ventas
-- ================================================
CREATE TABLE IF NOT EXISTS ventas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(20) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    cliente_id BIGINT NOT NULL,
    forma_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'registrada',
    total DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT,
    INDEX idx_numero (numero),
    INDEX idx_fecha (fecha),
    INDEX idx_cliente_id (cliente_id),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- TABLA: system_settings
-- ================================================
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================
-- DATOS INICIALES
-- ================================================

-- Insertar tipos de documento
INSERT IGNORE INTO documento_tipos (codigo, descripcion) VALUES
('CC', 'Cédula de Ciudadanía'),
('CE', 'Cédula de Extranjería'),
('NIT', 'Número de Identificación Tributaria'),
('PA', 'Pasaporte');

-- Insertar roles
INSERT IGNORE INTO roles (name) VALUES
('ADMIN'),
('VETERINARIO'),
('RECEPCIONISTA'),
('CLIENTE');

-- ================================================
-- CASO DE ACCESO: VETERINARIO
-- ================================================
-- Este caso queda habilitado por configuración de seguridad en la aplicación:
-- 1) El rol VETERINARIO puede acceder y modificar inventario (/productos/**).
-- 2) Puede gestionar historial clínico, citas y mascotas.
-- 3) Puede descargar adjuntos clínicos del historial.
--
-- El primer usuario administrador se crea desde DataInitializer cuando la
-- tabla users esta vacia y existe ADMIN_INITIAL_PASSWORD.

-- Insertar tipos de comprobante
INSERT IGNORE INTO comprobante_tipos (codigo, descripcion) VALUES
('FV', 'Factura de Venta'),
('FC', 'Factura de Compra'),
('NC', 'Nota de Crédito'),
('ND', 'Nota de Débito'),
('RC', 'Recibo de Caja');

-- Insertar unidades
INSERT IGNORE INTO units (codigo, descripcion) VALUES
('UND', 'Unidad'),
('KG', 'Kilogramos'),
('LT', 'Litros'),
('CJ', 'Cajas'),
('ML', 'Mililitros'),
('DZ', 'Docenas');

-- Insertar tipos de afectación (impuestos)
INSERT IGNORE INTO afectacion_tipos (codigo, nombre, descripcion, letra, porcentaje) VALUES
('IVA', 'Impuesto al Valor Agregado', 'Impuesto estándar del 19%', 'S', 19.00),
('EXE', 'Exento', 'Producto exento de IVA', 'E', 0.00),
('RET', 'Retención en la Fuente', 'Retención según regulación', 'R', 8.00);

-- Insertar clientes iniciales
INSERT IGNORE INTO clientes (documento_tipo_codigo, numero_documento, razon_social, direccion, telefono, email)
VALUES 
('CC', '10000001', 'Cliente de Prueba', 'Calle 1 # 2-3', '3000000000', 'cliente.prueba@happypaws.com'),
('CC', '10000002', 'Juan Pérez García', 'Carrera 5 # 10-15', '3105551234', 'juan.perez@email.com'),
('NIT', '900123456', 'Peluquería Canina S.A.S', 'Calle Principal 123', '3155559876', 'contacto@pelucanina.com');

-- Insertar proveedores iniciales
INSERT IGNORE INTO proveedores (documento_tipo_codigo, numero_documento, razon_social, direccion, telefono, email)
VALUES
('NIT', '800456789', 'Distribuidor Veterinario Nacional', 'Calle Industrial 456', '3165554321', 'ventas@distribvet.com'),
('NIT', '800987654', 'Importadora de Medicinas', 'Av. Comercial 789', '3105552468', 'info@importmedicinas.com');

-- Insertar mascotas
INSERT IGNORE INTO pets (nombre, especie, raza, edad, cliente_id)
VALUES
('Max', 'Perro', 'Golden Retriever', 3, 1),
('Luna', 'Gato', 'Persa', 2, 1),
('Rocky', 'Perro', 'Pastor Alemán', 5, 2);

-- Insertar citas
INSERT IGNORE INTO appointments (pet_id, cliente_id, fecha, motivo)
VALUES
(1, 1, '2026-05-20', 'Chequeo general y vacunas'),
(2, 1, '2026-05-22', 'Limpieza dental'),
(3, 2, '2026-05-25', 'Consulta por alergias en la piel');

-- Insertar unidades de medida iniciales (si no están)
INSERT IGNORE INTO units (codigo, descripcion) VALUES
('UND', 'Unidad'),
('KG', 'Kilogramos'),
('LT', 'Litros');

-- Insertar productos
INSERT IGNORE INTO productos (codigo, nombre, descripcion, unidad_id, afectacion_tipo_id, precio_unitario, stock, imagen)
VALUES
('PROD001', 'Alimento Perros Premium', 'Alimento de alta calidad para perros adultos', 1, 1, 85000.00, 50, 'alimento-perros.jpg'),
('PROD002', 'Vitaminas para Mascotas', 'Complejo vitamínico completo', 1, 1, 45000.00, 30, 'vitaminas.jpg'),
('PROD003', 'Enjuague Bucal Veterinario', 'Enjuague para higiene bucal', 2, 2, 25000.00, 20, 'enjuague.jpg'),
('PROD004', 'Antibiótico Amoxicilina', 'Antibiótico para infecciones', 1, 2, 55000.00, 15, 'amoxicilina.jpg'),
('PROD005', 'Champú Medicado', 'Champú para problemas de piel', 1, 1, 38000.00, 40, 'champu.jpg');

-- ================================================
-- FIN DEL SCRIPT
-- ================================================
