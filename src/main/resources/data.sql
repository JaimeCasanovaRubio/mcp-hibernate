-- RA3: Datos iniciales para testing y desarrollo
-- Estos datos se cargan automáticamente al arrancar la aplicación Spring Boot

-- ===== Insertar NPCs de prueba =====
INSERT INTO npcs (id, nombre, activo, created_at, updated_at) VALUES
(1, 'Chef Marco', true, '2024-01-15 09:30:00', '2024-01-15 09:30:00'),
(2, 'Cocinera Elena', true, '2024-01-16 10:15:00', '2024-01-20 14:20:00'),
(3, 'Pastelero Luis', true, '2024-01-17 11:00:00', '2024-01-17 11:00:00'),
(4, 'Sous Chef Ana', true, '2024-01-18 08:45:00', '2024-01-25 16:30:00'),
(5, 'Barista Carlos', false, '2024-01-19 13:20:00', '2024-02-01 10:00:00');

-- ===== Insertar Pedidos de prueba =====
-- Pedidos del Chef Marco (NPC 1)
INSERT INTO pedidos (id, npc_id, comentario, created_at) VALUES
(1, 1, 'Necesito ingredientes para la sopa del día', '2024-02-01 10:00:00'),
(2, 1, 'Preparar ensalada mediterránea', '2024-02-02 11:30:00');

-- Pedidos de Cocinera Elena (NPC 2)
INSERT INTO pedidos (id, npc_id, comentario, created_at) VALUES
(3, 2, 'Ingredientes para paella valenciana', '2024-02-01 12:00:00'),
(4, 2, 'Postre especial del chef', '2024-02-03 14:00:00');

-- Pedidos del Pastelero Luis (NPC 3)
INSERT INTO pedidos (id, npc_id, comentario, created_at) VALUES
(5, 3, 'Tarta de cumpleaños con fresas', '2024-02-02 09:00:00');

-- Pedidos de Sous Chef Ana (NPC 4)
INSERT INTO pedidos (id, npc_id, comentario, created_at) VALUES
(6, 4, 'Mise en place para el servicio de noche', '2024-02-03 16:00:00');

-- ===== Insertar Ingredientes de prueba =====
-- Ingredientes del Pedido 1 (Sopa del día)
INSERT INTO ingredientes (id, pedido_id, nombre, cantidad) VALUES
(1, 1, 'Zanahoria', 3),
(2, 1, 'Cebolla', 2),
(3, 1, 'Apio', 2),
(4, 1, 'Caldo de pollo', 1);

-- Ingredientes del Pedido 2 (Ensalada mediterránea)
INSERT INTO ingredientes (id, pedido_id, nombre, cantidad) VALUES
(5, 2, 'Tomate', 4),
(6, 2, 'Pepino', 2),
(7, 2, 'Aceitunas negras', 1),
(8, 2, 'Queso feta', 1);

-- Ingredientes del Pedido 3 (Paella valenciana)
INSERT INTO ingredientes (id, pedido_id, nombre, cantidad) VALUES
(9, 3, 'Arroz bomba', 2),
(10, 3, 'Pollo', 1),
(11, 3, 'Judías verdes', 1),
(12, 3, 'Azafrán', 1),
(13, 3, 'Garrofón', 1);

-- Ingredientes del Pedido 4 (Postre especial)
INSERT INTO ingredientes (id, pedido_id, nombre, cantidad) VALUES
(14, 4, 'Chocolate negro', 2),
(15, 4, 'Nata', 1),
(16, 4, 'Frambuesas', 1);

-- Ingredientes del Pedido 5 (Tarta de cumpleaños)
INSERT INTO ingredientes (id, pedido_id, nombre, cantidad) VALUES
(17, 5, 'Harina', 2),
(18, 5, 'Huevos', 6),
(19, 5, 'Azúcar', 1),
(20, 5, 'Fresas frescas', 2),
(21, 5, 'Nata montada', 1);

-- Ingredientes del Pedido 6 (Mise en place)
INSERT INTO ingredientes (id, pedido_id, nombre, cantidad) VALUES
(22, 6, 'Hierbas frescas', 3),
(23, 6, 'Mantequilla', 2),
(24, 6, 'Ajo', 4),
(25, 6, 'Limones', 3);

-- Resetear las secuencias de IDs para que el próximo ID sea 100
-- Esto permite que los tests inserten con IDs predecibles
ALTER TABLE npcs ALTER COLUMN id RESTART WITH 100;
ALTER TABLE pedidos ALTER COLUMN id RESTART WITH 100;
ALTER TABLE ingredientes ALTER COLUMN id RESTART WITH 100;
