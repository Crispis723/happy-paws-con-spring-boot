# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copiar código fuente
COPY src src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar JAR compilado del stage anterior
COPY --from=builder /build/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV DB_URL=jdbc:mysql://localhost:3306/veterinariadb?useSSL=false&serverTimezone=UTC
ENV DB_USERNAME=root
ENV DB_PASSWORD=
ENV DB_HOST=localhost
ENV DB_PORT=3306
ENV DB_NAME=veterinariadb
ENV PORT=8080
ENV SERVER_PORT=8080

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
