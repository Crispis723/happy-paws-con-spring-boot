# 🚀 GUÍA RÁPIDA DE INICIO

## Requisitos
- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Git (opcional)

---

## 1️⃣ COMPILAR EL PROYECTO

```bash
cd /workspaces/happy-paws-con-spring-boot
mvn clean compile
```

**Esperado**: 
- Compila sin errores
- Genera archivos en `target/`

---

## 2️⃣ EJECUTAR PRUEBAS

```bash
mvn test
```

**Esperado**: Todas las pruebas pasan ✅

---

## 3️⃣ EJECUTAR LA APLICACIÓN

### Opción A: Con Maven (Recomendado)
```bash
mvn spring-boot:run
```

### Opción B: Construir JAR y ejecutar
```bash
mvn package
java -jar target/happy-paws-*.jar
```

**Esperado**: 
```
Started DemoApplication in 3.2 seconds
Application is ready
http://localhost:8080
```

---

## 4️⃣ ACCEDER A LA APLICACIÓN

**URL**: `http://localhost:8080`

### Credenciales de Prueba (crear antes)
```sql
-- Conectar a MySQL y ejecutar:
INSERT INTO users (name, email, password, es_activo, role_id) 
VALUES ('Administrador', 'admin@happypaws.com', 
        '$2a$10$someBCryptHash', true, 1);
```

---

## 🗄️ CONFIGURACIÓN DE BASE DE DATOS

### application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/happy_paws
spring.datasource.username=root
spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

server.port=8080
server.servlet.session.timeout=1800
```

### Crear Base de Datos

```sql
CREATE DATABASE IF NOT EXISTS happy_paws;
USE happy_paws;

-- Crear tabla de roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT
);

-- Crear tabla de usuarios
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(15),
    direccion VARCHAR(255),
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    role_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Crear tabla de veterinarios
CREATE TABLE veterinarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    cedula VARCHAR(20) NOT NULL UNIQUE,
    especialidad VARCHAR(100),
    biografia LONGTEXT,
    licencia VARCHAR(100),
    foto_url VARCHAR(255),
    horario_disponible VARCHAR(255),
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Crear tabla de mascotas
CREATE TABLE pets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(100) NOT NULL,
    raza VARCHAR(100),
    age INT,
    peso DECIMAL(5,2),
    color VARCHAR(50),
    fecha_nacimiento DATE,
    numero_microchip VARCHAR(50),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    notas_medicas LONGTEXT,
    owner_id BIGINT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Crear tabla de citas
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    notas LONGTEXT,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    precio DECIMAL(10,2),
    cliente_nombre VARCHAR(100),
    cliente_telefono VARCHAR(15),
    mascota_nombre VARCHAR(100),
    mascota_especie VARCHAR(100),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    pet_id BIGINT NOT NULL,
    veterinario_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (pet_id) REFERENCES pets(id),
    FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertar roles
INSERT INTO roles (name, descripcion) VALUES 
('ADMIN', 'Administrador del sistema'),
('VETERINARIO', 'Profesional veterinario'),
('RECEPCIONISTA', 'Personal de recepción'),
('CLIENTE', 'Cliente de la veterinaria');

-- Insertar usuario administrador (contraseña: admin123)
-- El hash es: $2a$10$FuqL./3JLflDQRFiP.xC1O9oeYs/xUwhNCl8xXLPPt.LxCVLMx/uC
INSERT INTO users (name, email, password, es_activo, role_id) VALUES 
('Administrador', 'admin@happypaws.com', 
 '$2a$10$FuqL./3JLflDQRFiP.xC1O9oeYs/xUwhNCl8xXLPPt.LxCVLMx/uC', 
 true, 1);
```

---

## ✅ VERIFICAR INSTALACIÓN

### 1. Verificar que se ejecuta
```bash
curl http://localhost:8080
```

**Esperado**: Respuesta HTML de la página de inicio

### 2. Probar API
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/api/citas
```

### 3. Acceder a la web
```
http://localhost:8080/dashboard
```

**Esperado**: Página de dashboard (puede pedir login)

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Puerto 8080 ya en uso
```bash
# Cambiar puerto en application.properties
server.port=8081

# O matar proceso
lsof -ti:8080 | xargs kill -9
```

### Conexión a base de datos falla
```bash
# Verificar que MySQL está corriendo
mysql -u root -p -e "SELECT 1"

# Verificar credenciales en application.properties
spring.datasource.username=root
spring.datasource.password=password
```

### Errores de compilación
```bash
# Limpiar cache
mvn clean
mvn compile

# Reinstalar dependencias
mvn clean install -U
```

---

## 🎯 PRÓXIMAS ACCIONES

1. **Crear Usuario Admin** (ejecutar SQL anterior)
2. **Acceder a http://localhost:8080**
3. **Login con admin@happypaws.com / admin123**
4. **Navegar a Veterinarios en el menú**
5. **Crear un veterinario de prueba**
6. **Ver citas del veterinario**

---

## 📚 RECURSOS

| Recurso | URL |
|---------|-----|
| Dashboard | http://localhost:8080/dashboard |
| Veterinarios | http://localhost:8080/veterinarios |
| Citas | http://localhost:8080/citas |
| API Citas | http://localhost:8080/api/citas |

---

## 🔗 RUTAS ÚTILES

```
GET  /                           ← Página de inicio
GET  /login                      ← Login
GET  /dashboard                  ← Dashboard (requiere login)
GET  /veterinarios               ← Listar veterinarios
GET  /veterinarios/create        ← Crear veterinario
GET  /veterinarios/{id}          ← Ver veterinario
GET  /citas                       ← Listar citas
POST /citas                       ← Crear cita
GET  /api/citas/veterinario/{id} ← API: Citas de vet
```

---

## 💾 BACKUP Y RECUPERACIÓN

### Backup de BD
```bash
mysqldump -u root -p happy_paws > backup.sql
```

### Restaurar BD
```bash
mysql -u root -p happy_paws < backup.sql
```

---

## 📝 LOGS

Ver logs en tiempo real:
```bash
tail -f target/spring-boot.log
```

Configurar nivel de log (application.properties):
```properties
logging.level.com.Happypaws.demo=DEBUG
logging.level.org.springframework=INFO
logging.level.org.hibernate=DEBUG
```

---

## ✨ CARACTERÍSTICAS IMPLEMENTADAS

✅ Veterinarios (CRUD completo)  
✅ Citas por veterinario  
✅ Roles y seguridad  
✅ BCrypt password  
✅ Validaciones  
✅ APIs REST  
✅ Vistas Thymeleaf  
✅ Bootstrap UI  

---

**¿Necesitas ayuda?** Consulta ARQUITECTURA.md o IMPLEMENTACION_COMPLETADA.md
