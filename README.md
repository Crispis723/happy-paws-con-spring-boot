# Happy Paws - Spring Boot

Proyecto de ejemplo: aplicación de gestión para una clínica veterinaria (Happy Paws).

Resumen rápido
- **Tecnología:** Spring Boot, Thymeleaf, Maven
- **Contenido:** backend Java, plantillas Thymeleaf en `src/main/resources/templates`, recursos estáticos en `src/main/resources/static`.

Estructura principal
- `src/main/java`: código fuente Java (controladores, servicios, repositorios, modelos).
- `src/main/resources/templates`: vistas Thymeleaf (organizadas por módulos: `views/*`).
- `src/main/resources/static`: assets públicos (css, js, img, fonts).

Ejecutar localmente
1. Construir con Maven:

```
mvn clean package
```

2. Ejecutar la aplicación:

```
./mvnw spring-boot:run
```

O usar el JAR:

```
java -jar target/*.jar
```

Despliegue en Render (con Supabase como base de datos)
1. Sube el proyecto a GitHub (con el `.gitignore` ya actualizado para no exponer secretos).
2. En Render crea un **Blueprint** apuntando al repositorio: Render detecta automáticamente [render.yaml](render.yaml).
3. Completa las variables marcadas como secretas (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) con los datos del **Connection Pooling** de tu proyecto de Supabase. Ver [ENV_VARIABLES.md](ENV_VARIABLES.md) para el detalle.
4. Click en **Apply**. Render compila con Maven y levanta la app.

Nota: este proyecto es un único monolito Spring Boot + Thymeleaf (renderiza su propio HTML en el backend). No hay un frontend separado en React/Vue/Angular, así que **no se necesita Vercel**: todo vive en el mismo servicio de Render.

Variables de entorno recomendadas en Render:

```bash
PORT=10000
DB_URL=jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require
DB_USERNAME=postgres.vonswlsiujblwcbkkilc
DB_PASSWORD=<password>
JPA_DDL_AUTO=update
SQL_INIT_MODE=never
JPA_SHOW_SQL=false
SUPABASE_URL=https://vonswlsiujblwcbkkilc.supabase.co
SUPABASE_SERVICE_KEY=<service_role key, NUNCA la anon key>
SUPABASE_STORAGE_BUCKET=historial-clinico
ADMIN_INITIAL_EMAIL=admin@happypaws.com
ADMIN_INITIAL_PASSWORD=<password seguro>
SEED_DEMO_USERS=false
DEMO_USERS_PASSWORD=
```

Notas importantes para producción:
- Los archivos clínicos (radiografías, adjuntos) se guardan en un bucket privado de **Supabase Storage**, no en el disco de Render — así sobreviven a redeploys/restarts sin necesitar un disco persistente de pago. Ver [ENV_VARIABLES.md](ENV_VARIABLES.md) para crear el bucket.
- La lógica de historial clínico mantiene vencimiento automático a 1 año por registro.
- El primer administrador se crea solo si defines `ADMIN_INITIAL_PASSWORD` cuando la tabla de usuarios esta vacia. Manten `SEED_DEMO_USERS=false` en produccion para no crear usuarios con passwords de prueba.
- Desde `/dashboard` (personal del negocio) hay un botón **"Descargar datos"** que exporta clientes, mascotas, citas, productos, compras y ventas como CSV dentro de un `.zip`.

Presentación y entregables
- He añadido una presentación mínima en `docs/presentation.html` que resume el proyecto y muestra capturas de pantalla y puntos clave.
- Para la entrega final, se recomienda generar un PDF desde `docs/presentation.html` o usar una versión en `docs/`.

Guía para aprender
- Como referencia, ya puedes revisar los modelos completos de `Pet`, `Appointment` y `Producto`.
- Para practicar tú mismo, intenta crear primero `DocumentoTipo`, `AfectacionTipo` y `ComprobanteSerie` sin mirar la solución.
- Si quieres subir el nivel, después haz `Cliente` y `Proveedor` usando el mismo patrón de JPA y Lombok.
- La idea es que te acostumbres a repetir la estructura: `@Entity`, `@Table`, `@Id`, campos con nombres consistentes y, si aplica, validaciones simples con `@Column`.

Mapa base del sistema
- `User`: cuenta para entrar al sistema. Sirve para autenticación, contraseña y rol.
- `Role`: permiso o nivel de acceso. Sirve para definir si alguien administra, atiende o solo consulta.
- `Cliente`: persona o empresa que recibe el servicio. Sirve para guardar documento, razón social, correo y teléfono.
- `Pet`: mascota registrada. Sirve para guardar nombre, especie, raza, edad y su cliente dueño.
- `Appointment`: cita médica o de atención. Sirve para registrar mascota, cliente, fecha y motivo.

Paso a paso para construir bien cada modelo
1. Define si el dato es de acceso o de negocio.
2. Si es acceso, usa `User` y `Role`.
3. Si es negocio, crea una entidad propia como `Cliente`, `Pet` o `Appointment`.
4. Si el dato pertenece a otro registro, usa relación entre entidades y no texto plano.
5. Si el dato es una lista fija, crea un catálogo aparte como `DocumentoTipo` o `ComprobanteTipo`.
6. Crea primero la entidad, luego el repository y después el controller.
7. Al final conecta la vista con los campos reales del modelo.

Para qué sirve este orden
- Evita mezclar login con datos del negocio.
- Evita repetir texto donde debería haber una relación real.
- Hace más fácil validar formularios, guardar datos y luego consultarlos.
- Te deja el proyecto ordenado para crecer sin romper lo anterior.

Próximos pasos sugeridos
- Revisar y limpiar `src/main/resources/templates/views` para eliminar vistas no utilizadas y unificar partials en `components`.
- Organizar `src/main/resources/static/assets` por tipo (img, css, js, fonts).
- Añadir README en español con instrucciones de despliegue y capturas (esto ya está en progreso).

Contacto
- Si quieres que organice las plantillas y mueva archivos para mejorar la jerarquía, dime qué reglas prefieres (por ejemplo: una carpeta por módulo, partials comunes en `components`).
