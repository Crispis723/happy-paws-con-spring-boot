# Variables de entorno (Render + Supabase)

| Variable | Descripcion | Ejemplo | Obligatoria |
|----------|-------------|---------|-------------|
| `DB_URL` | Connection string JDBC de Postgres usando el pooler de Supabase | `jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require` | Si |
| `DB_USERNAME` | Usuario del pooler de Supabase | `postgres.tu_project_ref` | Si |
| `DB_PASSWORD` | Password de la base de datos de Supabase | `********` | Si |
| `PORT` | Puerto usado por Render | `10000` | Si |
| `JPA_DDL_AUTO` | Estrategia de Hibernate para crear/actualizar tablas | `update` | Opcional |
| `SQL_INIT_MODE` | Si Spring debe correr `data.sql` al iniciar | `never` | Opcional |
| `JPA_SHOW_SQL` | Mostrar SQL generado en logs | `false` | Opcional |
| `SUPABASE_URL` | URL de tu proyecto Supabase | `https://tu_project_ref.supabase.co` | Si |
| `SUPABASE_SERVICE_KEY` | `service_role key` de Supabase. Nunca uses la anon key | `eyJ...` | Si |
| `SUPABASE_STORAGE_BUCKET` | Nombre del bucket privado para adjuntos | `historial-clinico` | Opcional |
| `SENDGRID_API_KEY` | API Key de SendGrid con permiso de envío de correo | `SG...` | Si usas correo |
| `SENDGRID_FROM_ADDRESS` | Dirección remitente verificada en SendGrid | `tu-sender@dominio.com` | Si usas correo |
| `SENDGRID_FROM_NAME` | Nombre visible del remitente | `Happy Paws` | Opcional |
| `ADMIN_INITIAL_EMAIL` | Email del primer administrador si la tabla de usuarios esta vacia | `admin@happypaws.com` | Opcional |
| `ADMIN_INITIAL_PASSWORD` | Password del primer administrador. Configuralo como secreto en Render | `********` | Si para primer despliegue |
| `SEED_DEMO_USERS` | Crea usuarios demo con passwords conocidos. Mantener `false` en produccion | `false` | Opcional |
| `DEMO_USERS_PASSWORD` | Password comun para usuarios demo, solo si `SEED_DEMO_USERS=true` | `********` | Solo pruebas locales |

## Supabase Storage

Los archivos clinicos se suben a Supabase Storage para que no se pierdan en redeploys o reinicios de Render.

1. En Supabase, ve a Storage y crea un bucket.
2. Nombralo `historial-clinico`, o usa el valor que pongas en `SUPABASE_STORAGE_BUCKET`.
3. Mantenlo privado.
4. Copia la `service_role key` desde Project Settings > API y ponla en `SUPABASE_SERVICE_KEY`.

## Base de datos

1. En Supabase, abre Connect.
2. Usa el Session pooler o Transaction pooler, recomendado para Render por el limite de conexiones.
3. Copia la URL JDBC a `DB_URL`, el usuario a `DB_USERNAME` y el password a `DB_PASSWORD`.
4. Si alguna credencial estuvo expuesta en codigo o capturas, rota el password en Supabase antes de desplegar.

## Primer administrador

En el primer despliegue, si la tabla `users` esta vacia, la app crea un administrador usando:

```bash
ADMIN_INITIAL_EMAIL=admin@happypaws.com
ADMIN_INITIAL_PASSWORD=<password seguro>
```

Despues de entrar, puedes cambiar el password desde la app o rotarlo manualmente. Manten `SEED_DEMO_USERS=false` en produccion.

## Pasos en Render

1. Sube el proyecto a GitHub.
2. En Render, crea un Blueprint apuntando al repositorio.
3. Render detecta `render.yaml`.
4. Completa las variables secretas: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SUPABASE_URL`, `SUPABASE_SERVICE_KEY`, `SENDGRID_API_KEY`, `SENDGRID_FROM_ADDRESS` y `ADMIN_INITIAL_PASSWORD`.
5. Aplica el Blueprint y espera el build.
