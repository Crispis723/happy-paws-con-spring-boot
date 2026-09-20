-- ============================================================
-- HAPPY PAWS - Estados de citas
-- ============================================================
-- Migración ADITIVA (no reconstruye nada, segura de correr sobre una
-- base de datos ya poblada). Complementa docs/supabase-schema-corregido.sql,
-- que ya incluye este mismo cambio para instalaciones nuevas.
--
-- Agrega la columna "estado" a "appointments" con los 7 estados del
-- ciclo de vida de una cita: AGENDADA, CONFIRMADA, EN_ESPERA,
-- EN_CONSULTA, ATENDIDA, CANCELADA, NO_ASISTIO (ver EstadoCita.java).
-- Todas las citas existentes quedan como AGENDADA por defecto, ya que
-- no hay forma de reconstruir su estado real retroactivamente.
-- ============================================================

BEGIN;

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS estado VARCHAR(20) NOT NULL DEFAULT 'AGENDADA';

ALTER TABLE appointments
    DROP CONSTRAINT IF EXISTS chk_appointments_estado;

ALTER TABLE appointments
    ADD CONSTRAINT chk_appointments_estado
        CHECK (estado IN ('AGENDADA','CONFIRMADA','EN_ESPERA','EN_CONSULTA','ATENDIDA','CANCELADA','NO_ASISTIO'));

CREATE INDEX IF NOT EXISTS idx_appointments_estado ON appointments(estado);

COMMIT;
