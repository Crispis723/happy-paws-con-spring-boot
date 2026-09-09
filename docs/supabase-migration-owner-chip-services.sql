-- Cambios aditivos para propietarios, mascotas y citas.
-- Ejecutar después del esquema base. No elimina ni transforma datos existentes.

ALTER TABLE clientes
    ALTER COLUMN documento_tipo_codigo DROP NOT NULL,
    ALTER COLUMN numero_documento DROP NOT NULL;

ALTER TABLE pets
    ADD COLUMN IF NOT EXISTS chip VARCHAR(80);

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS servicio VARCHAR(100),
    ADD COLUMN IF NOT EXISTS tipo_servicio VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_clientes_numero_documento
    ON clientes(numero_documento);

CREATE INDEX IF NOT EXISTS idx_appointments_servicio_tipo
    ON appointments(servicio, tipo_servicio);
