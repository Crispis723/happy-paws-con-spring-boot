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

