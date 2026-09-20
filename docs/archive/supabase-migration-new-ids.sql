-- Happy Paws - Migración de IDs al nuevo convenio PostgreSQL/Supabase
--
-- Ejecutar UNA sola vez sobre una base que todavía utilice las columnas
-- antiguas (id, *_id). Si la base ya fue creada con los nombres nuevos,
-- NO ejecutar este script.
--
-- Las claves primarias y foráneas conservan sus restricciones; PostgreSQL
-- actualiza las referencias de las restricciones al renombrar las columnas.

BEGIN;

-- PKs
ALTER TABLE users RENAME COLUMN id TO id_usuario;
ALTER TABLE roles RENAME COLUMN id TO id_rol;
ALTER TABLE documento_tipos RENAME COLUMN id TO id_documento_tipo;
ALTER TABLE comprobante_tipos RENAME COLUMN id TO id_comprobante_tipo;
ALTER TABLE units RENAME COLUMN id TO id_unidad;
ALTER TABLE afectacion_tipos RENAME COLUMN id TO id_afectacion_tipo;
ALTER TABLE modules RENAME COLUMN id TO id_modulo;
ALTER TABLE permissions RENAME COLUMN id TO id_permiso;
ALTER TABLE password_reset_tokens RENAME COLUMN id TO id_token;
ALTER TABLE clientes RENAME COLUMN id TO id_cliente;
ALTER TABLE proveedores RENAME COLUMN id TO id_proveedor;
ALTER TABLE pets RENAME COLUMN id TO id_mascota;
ALTER TABLE appointments RENAME COLUMN id TO id_cita;
ALTER TABLE historial_mascotas RENAME COLUMN id TO id_historial;
ALTER TABLE productos RENAME COLUMN id TO id_producto;
ALTER TABLE comprobante_series RENAME COLUMN id TO id_serie;
ALTER TABLE compras RENAME COLUMN id TO id_compra;
ALTER TABLE compra_detalles RENAME COLUMN id TO id_detalle_compra;
ALTER TABLE ventas RENAME COLUMN id TO id_venta;
ALTER TABLE venta_detalles RENAME COLUMN id TO id_detalle_venta;
ALTER TABLE system_settings RENAME COLUMN id TO id_configuracion;

-- FK columns
ALTER TABLE user_roles RENAME COLUMN user_id TO id_usuario;
ALTER TABLE user_roles RENAME COLUMN role_id TO id_rol;
ALTER TABLE role_permissions RENAME COLUMN role_id TO id_rol;
ALTER TABLE role_permissions RENAME COLUMN permission_id TO id_permiso;
ALTER TABLE password_reset_tokens RENAME COLUMN user_id TO id_usuario;
ALTER TABLE pets RENAME COLUMN cliente_id TO id_cliente;
ALTER TABLE appointments RENAME COLUMN pet_id TO id_mascota;
ALTER TABLE appointments RENAME COLUMN cliente_id TO id_cliente;
ALTER TABLE appointments RENAME COLUMN veterinario_id TO id_veterinario;
ALTER TABLE historial_mascotas RENAME COLUMN pet_id TO id_mascota;
ALTER TABLE historial_mascotas RENAME COLUMN appointment_id TO id_cita;
ALTER TABLE productos RENAME COLUMN unidad_id TO id_unidad;
ALTER TABLE productos RENAME COLUMN afectacion_tipo_id TO id_afectacion_tipo;
ALTER TABLE compras RENAME COLUMN proveedor_id TO id_proveedor;
ALTER TABLE compra_detalles RENAME COLUMN compra_id TO id_compra;
ALTER TABLE compra_detalles RENAME COLUMN producto_id TO id_producto;
ALTER TABLE ventas RENAME COLUMN cliente_id TO id_cliente;
ALTER TABLE venta_detalles RENAME COLUMN venta_id TO id_venta;
ALTER TABLE venta_detalles RENAME COLUMN producto_id TO id_producto;

COMMIT;
