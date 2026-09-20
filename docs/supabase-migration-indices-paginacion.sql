-- ============================================================
-- HAPPY PAWS - Índices para las consultas paginadas
-- ============================================================
-- Migración ADITIVA (no reconstruye nada, segura de correr sobre una
-- base de datos ya poblada). Complementa docs/supabase-schema-corregido.sql,
-- que es la fuente de verdad del esquema.
--
-- Motivo: al agregar paginación a Ventas, Compras, Productos y Clientes
-- (ver VentaController/CompraController/ProductoController/ClienteController),
-- cada página se pide ordenada por una columna que hasta ahora no tenía
-- índice propio:
--   - ventas.fecha    (orden descendente, ventas más recientes primero)
--   - compras.fecha   (idem)
--   - productos.nombre    (orden alfabético)
--   - clientes.razon_social (orden alfabético)
--
-- Sin estos índices, Postgres puede resolver el ORDER BY con un sort
-- completo de la tabla en cada página en vez de recorrer un índice ya
-- ordenado. Con pocas filas no se nota; con miles de ventas/productos sí.
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_ventas_fecha
    ON ventas(fecha DESC);

CREATE INDEX IF NOT EXISTS idx_compras_fecha
    ON compras(fecha DESC);

CREATE INDEX IF NOT EXISTS idx_productos_nombre
    ON productos(nombre);

CREATE INDEX IF NOT EXISTS idx_clientes_razon_social
    ON clientes(razon_social);
