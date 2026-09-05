# Happy Paws - Spring Boot

Aplicacion de gestion para una clinica veterinaria. Es un monolito Spring Boot + Thymeleaf: el backend Java renderiza las vistas HTML y sirve los assets estaticos.

## Stack de despliegue

- **App web:** Render
- **Base de datos:** Supabase PostgreSQL
- **Archivos clinicos:** Supabase Storage
- **Build:** Maven + Java 17

## Variables requeridas en Render

Render detecta automaticamente [render.yaml](render.yaml). Las variables marcadas como `sync: false` se completan en el dashboard de Render:

```bash
DB_URL=jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require
DB_USERNAME=postgres.tu_project_ref
DB_PASSWORD=<password de Supabase>
SUPABASE_URL=https://tu_project_ref.supabase.co
SUPABASE_SERVICE_KEY=<service_role key>
SUPABASE_STORAGE_BUCKET=historial-clinico
MAIL_USERNAME=<correo SMTP>
MAIL_PASSWORD=<app password SMTP>
ADMIN_INITIAL_EMAIL=admin@happypaws.com
ADMIN_INITIAL_PASSWORD=<password seguro>
SEED_DEMO_USERS=false
```

Ver [ENV_VARIABLES.md](ENV_VARIABLES.md) para el detalle.

## Despliegue en Render

1. Sube este repositorio a GitHub.
2. En Render, crea un **Blueprint** y selecciona el repositorio.
3. Render leera [render.yaml](render.yaml).
4. Completa las variables secretas.
5. Aplica el Blueprint y espera el build.

El comando de build es:

```bash
bash ./mvnw -DskipTests clean package
```

El comando de inicio es:

```bash
java -jar target/*.jar
```

## Supabase

Para la base de datos usa el Connection Pooling de Supabase y copia la URL JDBC en `DB_URL`.

**Importante:** `JPA_DDL_AUTO` está en `validate` por defecto (ver `application.properties`). Esto significa que Hibernate **no** crea ni modifica tablas automáticamente; solo valida que el esquema de la base de datos coincida con las entidades JPA. Antes de desplegar (o de correr el proyecto localmente por primera vez), ejecuta manualmente el script `docs/supabase-schema-corregido.sql` en tu Supabase — es la única fuente de verdad del esquema, verificada columna por columna contra las entidades del proyecto. Los demás scripts SQL en `docs/` (`supabase-schema-fixed.sql`, `supabase-schema-v2-fixed.sql`, `supabase-migration-*.sql`) son versiones anteriores que ya no coinciden con las entidades actuales; se conservan solo como referencia histórica.

Para archivos clinicos:

1. En Supabase Storage crea un bucket privado llamado `historial-clinico`.
2. Copia la `service_role key` desde Project Settings > API.
3. Configura `SUPABASE_SERVICE_KEY` en Render. Nunca uses esta key en frontend.

## Primer administrador

En el primer despliegue, si no existe el usuario indicado en `ADMIN_INITIAL_EMAIL`, la app crea un administrador usando `ADMIN_INITIAL_PASSWORD`.

Mantener `SEED_DEMO_USERS=false` en produccion.

## Desarrollo local

Puedes ejecutar localmente usando las mismas variables de Supabase:

```powershell
$env:DB_URL="jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require"
$env:DB_USERNAME="postgres.tu_project_ref"
$env:DB_PASSWORD="tu_password"
$env:SUPABASE_URL="https://tu_project_ref.supabase.co"
$env:SUPABASE_SERVICE_KEY="tu_service_role_key"
$env:ADMIN_INITIAL_PASSWORD="Admin12345"
.\mvnw.cmd spring-boot:run
```

## Estructura principal

- `src/main/java`: controladores, servicios, repositorios y modelos.
- `src/main/resources/templates`: vistas Thymeleaf.
- `src/main/resources/static`: CSS, JS, imagenes y assets publicos.
- `render.yaml`: configuracion de despliegue para Render.


## Correcciones de despliegue Supabase/Render

Esta versión corrige la inicialización RBAC para que **cada permiso tenga siempre un `module_id` válido**. En particular, los permisos `CONFIGURACION_*` ahora tienen módulos propios y `DataInitializer` los resuelve explícitamente. También mantiene `spring.jpa.hibernate.ddl-auto=validate`, por lo que la base debe estar creada antes de iniciar la aplicación.

### Base de datos
- BD nueva: ejecutar `docs/supabase-schema-fixed.sql`.
- BD existente: ejecutar `docs/supabase-migration-fix.sql` antes del deploy.

### Render
El proyecto ya incluye `render.yaml` y `Dockerfile`. Mantén `JPA_DDL_AUTO=validate` y `SQL_INIT_MODE=never`.
