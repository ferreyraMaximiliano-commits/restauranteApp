CREATE DATABASE IF NOT EXISTS restaurante;
USE restaurante;


CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    fecha_registro DATE NOT NULL,
    estado VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_usuario)
);

CREATE TABLE sector (
    id_sector INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_sector)
);

CREATE TABLE mesas (
    id_mesa INT,
    estado VARCHAR(50) NOT NULL,
    capacidad INT NOT NULL,
    zona VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_mesa)
);


CREATE TABLE empleados (
    id_usuario INT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(100),
    turno VARCHAR(50),
    salario DOUBLE NOT NULL,
    PRIMARY KEY (id_usuario),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
);

CREATE TABLE mozos (
    id_usuario INT,
    PRIMARY KEY (id_usuario),
    FOREIGN KEY (id_usuario) REFERENCES empleados(id_usuario) ON DELETE CASCADE
);

CREATE TABLE personalOperativo (
    id_usuario INT,
    id_sector INT NOT NULL,
    PRIMARY KEY (id_usuario),
    FOREIGN KEY (id_usuario) REFERENCES empleados(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_sector) REFERENCES sector(id_sector)
);

CREATE TABLE gerentes (
    id_usuario INT,
    codigo_autorizacion INT NOT NULL UNIQUE,
    PRIMARY KEY (id_usuario),
    FOREIGN KEY (id_usuario) REFERENCES empleados(id_usuario) ON DELETE CASCADE
);



CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    id_sector INT NOT NULL,
    precio DOUBLE NOT NULL,
    estado VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_producto),
    FOREIGN KEY (id_sector) REFERENCES sector(id_sector)
);

CREATE TABLE pedidos (
    numero_pedido INT AUTO_INCREMENT,
    fecha DATE NOT NULL,
    estado VARCHAR(50) NOT NULL,
    id_mesa INT NOT NULL,
    id_mozo INT NOT NULL,
    PRIMARY KEY (numero_pedido),
    FOREIGN KEY (id_mesa) REFERENCES mesas(id_mesa),
    FOREIGN KEY (id_mozo) REFERENCES mozos(id_usuario)
);

CREATE TABLE registroPedidos (
    numero_pedido INT,
    renglon INT NOT NULL,
    id_producto INT,
    cantidad INT,
    nota_extra VARCHAR(50),
    precio_unitario DOUBLE NOT NULL,
    total DOUBLE NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'Enviado',

    PRIMARY KEY (numero_pedido, renglon), 
    FOREIGN KEY (numero_pedido) REFERENCES pedidos(numero_pedido) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);



USE Restaurante;


INSERT INTO sector (id_sector, nombre) 
VALUES 
(1, 'Cocina'),
(2, 'Barra');

INSERT INTO productos (nombre, id_sector, precio, estado) 
VALUES 
('Hamburguesa Clásica', 1, 1200, 'Disponible'),
('Papas Fritas', 1, 600, 'Disponible'),
('Pizza Muzzarella', 1, 1800, 'Disponible'),
('Cerveza Tirada', 2, 700, 'Disponible'),
('Gaseosa de Cola', 2, 400, 'Disponible');


INSERT INTO mesas (id_mesa, estado, capacidad, zona) 
VALUES 
(1, 'Libre', 4, 'Interior'),
(2, 'Ocupada', 2, 'Interior'),
(3, 'Libre', 6, 'Interior');


INSERT INTO usuarios (id_usuario, email, contrasena, fecha_registro, estado) 
VALUES 
(1, 'gerente@restaurante.com', '123456', '2026-01-15', 'Activo'),
(2, 'mozo1@restaurante.com', '123456', '2026-02-01', 'Activo'),
(3, 'cocina1@restaurante.com', '123456', '2026-02-10', 'Activo'),
(4, 'barra1@restaurante.com', '123456', '2026-10-15', 'Activo');


INSERT INTO empleados (id_usuario, nombre, apellido, telefono, turno, salario) 
VALUES 
(1, 'Carlos', 'Gómez', '11223344', 'Mañana', 2500),
(2, 'Laura', 'Martínez', '55667788', 'Noche', 1500),
(3, 'Jorge', 'Rodríguez', '99001122', 'Noche', 1800),
(4, 'Maxi', 'Ferreyra', '888555434', 'Tarde', 1800);

INSERT INTO gerentes (id_usuario, codigo_autorizacion) 
VALUES 
(1, 9999);


INSERT INTO mozos (id_usuario) 
VALUES 
(2);


INSERT INTO personalOperativo (id_usuario, id_sector) 
VALUES 
(3, 1),
(4, 2);



INSERT INTO pedidos (numero_pedido, fecha, estado, id_mesa, id_mozo) 
VALUES 
(1, '2026-09-28', 'En Preparación', 2, 2);


INSERT INTO registroPedidos (numero_pedido, renglon, id_producto, cantidad, nota_extra, precio_unitario, total) 
VALUES 
(1, 1, 1, 2, 'Sin cebolla', 1200, 2400),
(1, 2, 2, 1, 'Bien crocantes', 600, 600),   
(1, 3, 4, 2, 'Con limón', 700, 1400);