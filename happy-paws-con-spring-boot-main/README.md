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

Próximos pasos sugeridos
- Revisar y limpiar `src/main/resources/templates/views` para eliminar vistas no utilizadas y unificar partials en `components`.
- Organizar `src/main/resources/static/assets` por tipo (img, css, js, fonts).
- Añadir README en español con instrucciones de despliegue y capturas (esto ya está en progreso).

Contacto
- Si quieres que organice las plantillas y mueva archivos para mejorar la jerarquía, dime qué reglas prefieres (por ejemplo: una carpeta por módulo, partials comunes en `components`).

