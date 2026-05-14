# Happy Paws - Guía de Arquitectura e Implementación

## 🎯 OBJETIVO COMPLETADO

Se ha implementado un **Sistema Profesional de Gestión Veterinaria** siguiendo arquitectura **MVC** con Spring Boot, con las siguientes características:

### ✅ Completado

1. **CRUD Completo de Veterinarios**
2. **Sistema de Citas mejorado**
3. **Roles y Seguridad implementados**
4. **Modelos y Servicios refactorizados**
5. **Validaciones en todas las entidades**
6. **APIs REST funcionales**
7. **Vistas profesionales con Thymeleaf**

---

## 📁 ESTRUCTURA DEL PROYECTO

```
src/main/java/com/Happypaws/demo/
├── config/
│   └── SecurityConfig.java          ← Seguridad y roles
├── controller/
│   ├── VeterinarioController.java   ← CRUD Veterinarios
│   ├── AppointmentController.java   ← Gestión de Citas
│   └── AppointmentApiController.java ← APIs REST
├── model/
│   ├── User.java                    ← Mejorado con validaciones
│   ├── Veterinario.java             ← NEW
│   ├── Pet.java                     ← Mejorado
│   ├── Appointment.java             ← Mejorado con enum
│   └── Role.java                    ← Mejorado
├── repository/
│   ├── VeterinarioRepository.java   ← NEW
│   ├── AppointmentRepository.java   ← Mejorado
│   └── UserRepository.java          ← Mejorado
└── service/
    ├── VeterinarioService.java      ← NEW
    ├── AppointmentService.java      ← NEW
    └── UserService.java             ← NEW
```

---

## 🔐 SEGURIDAD Y ROLES

### Roles Disponibles:
- **ADMIN**: Acceso total al sistema
- **VETERINARIO**: Ver citas asignadas, mascotas, diagnósticos
- **RECEPCIONISTA**: Registrar clientes, mascotas, crear citas
- **CLIENTE**: Ver sus mascotas y citas

### Configuración:
```java
// SecurityConfig.java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() { ... }
}
```

### Protección de Rutas:
```java
@PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
public String index(...) { ... }
```

---

## 📋 VETERINARIOS - GUÍA DE USO

### Crear Veterinario
```
GET  /veterinarios/create    → Formulario
POST /veterinarios            → Guardar
```

### Listar Veterinarios
```
GET /veterinarios             → Lista con búsqueda y filtros
```

### Ver Perfil de Veterinario
```
GET /veterinarios/{id}        → Detalles completos
                              → Citas recientes
                              → Estadísticas
```

### Editar Veterinario
```
GET  /veterinarios/{id}/edit  → Formulario
PUT  /veterinarios/{id}       → Actualizar
```

### Campos de Veterinario:
- Nombre completo
- Email (único)
- Teléfono
- Cédula (única)
- Especialidad (enum)
- Licencia profesional
- Biografía
- Horario disponible
- Foto URL
- Estado (activo/inactivo)

---

## 📅 CITAS - VETERINARIOS PUEDEN VER SUS CITAS

### API REST para Veterinarios:
```
GET /api/citas/veterinario/{id}           → Sus citas
GET /api/citas/veterinario/{id}/hoy       → Citas de hoy
GET /api/citas/veterinario/{id}/recientes → Citas recientes
```

### Controller Web:
```
GET    /citas                    → Listar citas
GET    /citas/{id}               → Ver detalles
GET    /citas/veterinario/{id}   → Citas de veterinario
POST   /citas/{id}/estado        → Cambiar estado
```

### Estados de Cita:
- **PENDIENTE**: Cita programada pero no confirmada
- **CONFIRMADA**: Cliente confirmó asistencia
- **COMPLETADA**: Cita realizada
- **CANCELADA**: Cancelada por cliente o clínica
- **NO_PRESENTADA**: Cliente no asistió

---

## 🗂️ MODELOS JPA

### User (Mejorado)
```java
@Entity @Table(name = "users")
@Getter @Setter
public class User {
    @Id @GeneratedValue
    private Long id;
    
    @NotBlank @Email @Column(unique = true)
    private String email;
    
    @NotBlank @Size(min = 8)
    private String password;  // ← Encriptado con BCrypt
    
    @Column
    private String telefono;
    
    @Column
    private String direccion;
    
    @Column
    private Boolean esActivo = true;
    
    @ManyToOne
    private Role role;
}
```

### Veterinario (NEW)
```java
@Entity @Table(name = "veterinarios")
public class Veterinario {
    @Id @GeneratedValue
    private Long id;
    
    @NotBlank @Email @Column(unique = true)
    private String email;
    
    @Column(unique = true)
    private String cedula;
    
    @NotBlank
    private String especialidad;  // ← Enum sugerido
    
    @NotBlank
    private String licencia;
    
    @Column
    private String horarioDisponible;
    
    @OneToOne
    private User usuario;  // ← Opcional
    
    @Column
    private Boolean esActivo = true;
}
```

### Appointment (Mejorado)
```java
@Entity @Table(name = "appointments")
public class Appointment {
    @Id @GeneratedValue
    private Long id;
    
    @NotNull
    private LocalDateTime fechaHora;
    
    @NotBlank @Size(min = 10, max = 500)
    private String motivo;
    
    @Enumerated(EnumType.STRING)
    private EstadoCita estado = EstadoCita.PENDIENTE;
    
    @Column
    private Double precio;
    
    @ManyToOne
    private Pet mascota;
    
    @ManyToOne
    private Veterinario veterinario;  ← NEW
    
    @Column
    private String clienteNombre;
    
    @Column
    private String clienteTelefono;
}
```

### Pet (Mejorado)
```java
@Entity @Table(name = "pets")
public class Pet {
    @Id @GeneratedValue
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String species;
    
    @Column
    private String raza;
    
    @Min(0) @Max(100)
    private Integer age;
    
    @Column
    private String color;
    
    @Column
    private String numeroMicrochip;
    
    @ManyToOne
    private User owner;  // ← Propietario
    
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL)
    private List<Appointment> appointments;
}
```

---

## 🔑 SERVICIOS

### VeterinarioService
```java
public class VeterinarioService {
    public List<Veterinario> listarActivos()
    public Veterinario guardar(Veterinario vet)
    public Veterinario actualizar(Long id, Veterinario vet)
    public void eliminar(Long id)  // ← Soft delete
    public List<Veterinario> buscarPorEspecialidad(String esp)
    public boolean existeEmail(String email)
    public boolean existeCedula(String cedula)
}
```

### AppointmentService
```java
public class AppointmentService {
    // Citas de veterinario ← IMPORTANTE
    public List<Appointment> citasPorVeterinario(Long vetId)
    public List<Appointment> citasDeHoy(Long vetId)
    public List<Appointment> citasRecientes(Long vetId)
    
    // Gestión de citas
    public Appointment guardar(Appointment apt)
    public void cambiarEstado(Long id, EstadoCita estado)
    public List<Appointment> citasPorMascota(Long mascotaId)
    public List<Appointment> citasPorPropietario(Long propietarioId)
}
```

### UserService
```java
public class UserService {
    public User guardar(User user)  // ← Encripta password
    public User actualizar(Long id, User user)
    public boolean validarCredenciales(String email, String password)
    public Optional<User> buscarPorEmail(String email)
    public boolean existeEmail(String email)
}
```

---

## 📡 APIs REST

### Endpoints Disponibles:

#### Veterinarios
```
GET    /api/veterinarios/activos                    → Lista activos
GET    /api/veterinarios/especialidad/{esp}         → Por especialidad
GET    /api/veterinarios/buscar?q=nombre            → Buscar
```

#### Citas
```
GET    /api/citas                                   → Todas
GET    /api/citas/{id}                              → Una cita
GET    /api/citas/veterinario/{id}                  → Citas de vet
GET    /api/citas/veterinario/{id}/hoy              → Hoy
GET    /api/citas/mascota/{id}                      → De mascota
PUT    /api/citas/{id}/estado?nuevoEstado=COMPLETADA
POST   /api/citas                                   → Crear
PUT    /api/citas/{id}                              → Actualizar
DELETE /api/citas/{id}                              → Eliminar
```

---

## 🎨 VISTAS CREADAS

### Veterinarios
- **index.html**: Grid de veterinarios con búsqueda y filtros
- **create.html**: Formulario para registrar veterinario
- **edit.html**: Formulario para editar veterinario
- **show.html**: Perfil completo + citas recientes

### Citas
- Mejoras a vistas existentes para mostrar veterinario asignado
- Estados actualizables
- Filtros por veterinario

---

## 🔧 CONFIGURACIONES NECESARIAS

### 1. Actualizar Dependencias (pom.xml)
```xml
<!-- Spring Boot Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### 2. Configurar Base de Datos
```sql
-- Agregar columna a tabla users
ALTER TABLE users ADD COLUMN (
    telefono VARCHAR(15),
    direccion VARCHAR(255),
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla veterinarios
CREATE TABLE veterinarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15),
    cedula VARCHAR(20) UNIQUE NOT NULL,
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

-- Mejorar tabla appointments
ALTER TABLE appointments ADD COLUMN (
    veterinario_id BIGINT,
    cliente_nombre VARCHAR(100),
    cliente_telefono VARCHAR(15),
    mascota_nombre VARCHAR(100),
    mascota_especie VARCHAR(100),
    notas LONGTEXT,
    estado VARCHAR(20),
    precio DECIMAL(10,2),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (veterinario_id) REFERENCES veterinarios(id)
);
```

### 3. Configuración de Seguridad en application.properties
```properties
# Spring Security
spring.security.user.name=admin
spring.security.user.password=admin123
spring.security.user.roles=ADMIN

# Sesión
server.servlet.session.timeout=1800
```

---

## 📝 PRÓXIMOS PASOS

- [ ] Crear migración de base de datos (Liquibase/Flyway)
- [ ] Implementar DTOs para API
- [ ] Crear @ControllerAdvice para manejo de excepciones
- [ ] Agregar JWT para autenticación sin sesión
- [ ] Implementar historial médico de mascotas
- [ ] Crear reportes PDF
- [ ] Agregar notificaciones por email
- [ ] Dashboard con estadísticas
- [ ] Backup automático de datos
- [ ] Auditoría de cambios

---

## 🚀 CÓMO USAR

### 1. Compilar
```bash
mvn clean compile
```

### 2. Ejecutar
```bash
mvn spring-boot:run
```

### 3. Acceder
```
http://localhost:8080
```

### 4. Crear Admin (Primera vez)
```sql
INSERT INTO users (name, email, password, es_activo, role_id) 
VALUES ('Admin', 'admin@happypaws.com', '$2a$10...', true, 1);
```

---

## 📞 SOPORTE

Para problemas o dudas sobre la arquitectura, consultar:
1. Documentación en `/memories/repo/architecture-veterinarios.md`
2. Comentarios en los controllers
3. Validaciones en los modelos

---

**Proyecto: Happy Paws Veterinaria**
**Versión: 1.0**
**Fecha: Mayo 2026**
