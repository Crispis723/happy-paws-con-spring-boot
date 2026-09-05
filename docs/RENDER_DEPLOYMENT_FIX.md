# Correcciones para Render + Supabase

## Cambios aplicados

1. `application.properties` ahora define `app.base-url` usando:
   - `APP_BASE_URL`, si existe.
   - `RENDER_EXTERNAL_URL`, si existe.
   - `http://localhost:8080` como último respaldo.
2. El enlace de recuperación de contraseña usa `token.getToken()` correctamente.
3. `render.yaml` ejecuta `chmod +x mvnw` antes del build para evitar errores de permisos.

## Base de datos

Este proyecto tiene una estructura Java que es distinta del SQL antiguo que contiene
`permissions.module_id` y `permissions.action` como NOT NULL.

Si la base de Supabase ya fue creada con ese SQL antiguo, ejecutar una sola vez:

`docs/supabase-migration-fix.sql`

Si la base es nueva, usar preferiblemente:

`docs/supabase-schema-fixed.sql`

No ejecutar repetidamente scripts que comiencen con `DROP TABLE` sobre una base de producción.

## Variables obligatorias en Render

- DB_URL
- DB_USERNAME
- DB_PASSWORD
- SUPABASE_URL
- SUPABASE_SERVICE_KEY

Para crear el primer administrador:

- ADMIN_INITIAL_EMAIL
- ADMIN_INITIAL_PASSWORD

Para recuperación de contraseña por correo:

- MAIL_USERNAME
- MAIL_PASSWORD

## Base de datos: modelo normalizado

La versión final usa `JPA_DDL_AUTO=validate` en producción. La estructura de Supabase debe crearse con `docs/supabase-schema-fixed.sql` (base nueva) o actualizarse con `docs/supabase-migration-fix.sql` (base existente).

El modelo RBAC queda normalizado:

`users -> user_roles -> roles -> role_permissions -> permissions -> modules`

Todas las relaciones tienen claves foráneas y las tablas puente tienen clave primaria compuesta. `permissions.module_id`, `permissions.code` y `permissions.action` son obligatorios.

No uses `ddl-auto=update` en producción para este proyecto: los cambios estructurales deben pasar por SQL/migraciones controladas.
