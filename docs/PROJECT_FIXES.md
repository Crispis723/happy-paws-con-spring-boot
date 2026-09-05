# Happy Paws - correcciones de compatibilidad

Se corrigieron las incompatibilidades entre las entidades JPA del proyecto y PostgreSQL/Supabase.

## Cambios principales

- `Appointment.fechaHora` ahora está mapeado explícitamente a `appointments.fecha_hora`.
- `appointments.veterinario_id` referencia `users.id`, porque `Appointment.veterinario` es un `User`.
- Se agregó `password_reset_tokens` con `expiry_date`, compatible con `PasswordResetToken`.
- El esquema de `permissions` permite que `DataInitializer` cree permisos usando `name` y `description` sin exigir `module_id`, `code` o `action`.
- `src/main/resources/data.sql` usa sintaxis PostgreSQL (`ON CONFLICT`) en lugar de `INSERT IGNORE`.

## Archivos SQL

- `docs/supabase-schema-fixed.sql`: esquema completo corregido. Úsalo para una base nueva.
- `docs/supabase-migration-fix.sql`: migración para una base que ya fue creada con el SQL anterior.
