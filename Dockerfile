# Este Dockerfile es OPCIONAL: render.yaml ya despliega la app usando el
# runtime "java" nativo de Render (sin Docker). Se deja por si quieres
# desplegar en otro proveedor que sí requiera contenedor (Railway, Fly.io, etc).

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

# IMPORTANTE: no se ponen valores por defecto de DB_URL/USERNAME/PASSWORD.
# Deben pasarse siempre como variables de entorno reales al correr el
# contenedor (docker run -e DB_URL=... -e DB_USERNAME=... -e DB_PASSWORD=...),
# nunca quemados en la imagen.
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]
