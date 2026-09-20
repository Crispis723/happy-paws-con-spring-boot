-- ============================================================
-- HAPPY PAWS - Corrección: módulo y ruta de permisos CONFIGURACION_*
-- ============================================================
-- Migración ADITIVA/correctiva, segura de correr varias veces.
--
-- El script de esquema (docs/supabase-schema-corregido.sql) tenía un
-- error: los 4 módulos de configuración (comprobantes, documentos,
-- unidades, afectaciones) se creaban con la ruta genérica
-- "/configuracion" en vez de su propia ruta, y los 4 permisos
-- CONFIGURACION_* quedaban asignados al módulo "ROLES" en vez de a su
-- propio módulo. Esto ya está corregido en el script para instalaciones
-- nuevas.
--
-- Si tu base de datos corrió el script ANTES de esta corrección, el
-- error queda "pegado": DataInitializer corrige el módulo de cada
-- permiso en cada arranque, pero solo crea el módulo la primera vez
-- (no le corrige la ruta después si ya existía). Esta migración arregla
-- ambas cosas directamente.
-- ============================================================

BEGIN;

UPDATE modules SET route = '/configuracion/comprobantes' WHERE code = 'CONFIGURACION_COMPROBANTES' AND route <> '/configuracion/comprobantes';
UPDATE modules SET route = '/configuracion/documentos'   WHERE code = 'CONFIGURACION_DOCUMENTOS'   AND route <> '/configuracion/documentos';
UPDATE modules SET route = '/configuracion/unidades'     WHERE code = 'CONFIGURACION_UNIDADES'     AND route <> '/configuracion/unidades';
UPDATE modules SET route = '/configuracion/afectaciones' WHERE code = 'CONFIGURACION_AFECTACIONES' AND route <> '/configuracion/afectaciones';

UPDATE permissions p
SET id_modulo = m.id_modulo
FROM modules m
WHERE p.code = m.code
  AND p.code IN ('CONFIGURACION_COMPROBANTES','CONFIGURACION_DOCUMENTOS','CONFIGURACION_UNIDADES','CONFIGURACION_AFECTACIONES')
  AND p.id_modulo <> m.id_modulo;

COMMIT;
