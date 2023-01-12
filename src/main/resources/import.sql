INSERT INTO clientes(id, nombre, apellido, email, create_at, imagen) VALUES (1, 'Andres', 'Guzman', 'aguzman@gmail.com', '2021-05-29', '');
INSERT INTO clientes(id, nombre, apellido, email, create_at, imagen) VALUES(2, 'Jhon', 'Doe', 'jdoe@gmail.com', '2022-03-21', '');
INSERT INTO clientes(id, nombre, apellido, email, create_at, imagen) VALUES(3, 'Cristhian', 'Quispe', 'cquisper@gmail.com', '2022-03-29', '');

INSERT INTO productos(nombre, precio, create_at) VALUES ('Panasonic Pantalla LCD', 2599, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES ('Sony Camra digital DSC-W3200', 12599, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES ('Aple iPod shuffle', 1499, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES ('Sony Notebook Z110', 3499, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES ('Hewlett Packard Mutifuncional F2280', 6199, NOW());
INSERT INTO productos(nombre, precio, create_at) VALUES ('Bianchi Bicicleta Aro 26', 6099, NOW());

INSERT INTO facturas(descripcion, observacion, create_at, cliente_id) VALUES ('Factura equipos de oficina', null, NOW() ,1);
INSERT INTO facturas(descripcion, observacion, create_at, cliente_id) VALUES ('Factura bicicleta', 'Alguna nota importante', NOW(),2);

INSERT INTO facturas_items(cantidad, producto_id, factura_id) VALUES (5, 1, 1);
INSERT INTO facturas_items(cantidad, producto_id, factura_id) VALUES (9, 2, 1);
INSERT INTO facturas_items(cantidad, producto_id, factura_id) VALUES (12, 3, 1);
INSERT INTO facturas_items(cantidad, producto_id, factura_id) VALUES (3, 4, 1);
INSERT INTO facturas_items(cantidad, producto_id, factura_id) VALUES (2, 5, 1);

INSERT INTO facturas_items(cantidad, producto_id, factura_id) VALUES (1, 6, 2);

/*Se insertan datos de ejemplos de los usuarios*/
INSERT INTO usuarios(username, password, enabled, fecha_creacion) VALUES ('shinbot', '$2a$10$h1MBp6s03Uee8L.rO/VsHOYtq2wPa7GhqErKZbGVOLXqRWSw2GS3e', 1, '2021-05-29');
INSERT INTO usuarios(username, password, enabled, fecha_creacion) VALUES ('cristhian', '$2a$10$qvHzwwbNtLm2ez6VCp9B6ujp5SSeHO80c3fOLDNNANZoA1jE6Nxg6', 1, '2021-05-29');

INSERT INTO authorities(user_id, authority) VALUES (1, 'ROLE_USER');
INSERT INTO authorities(user_id, authority) VALUES (1, 'ROLE_ADMIN');
INSERT INTO authorities(user_id, authority) VALUES (2, 'ROLE_USER');