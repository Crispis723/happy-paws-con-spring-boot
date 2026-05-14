# 🏗️ DIAGRAMA DE ARQUITECTURA - Happy Paws

## Arquitectura MVC

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                      │
│                    (Views - Thymeleaf)                       │
│                                                               │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐ │
│  │  veterinarios/ │  │    citas/      │  │   dashboard/   │ │
│  │   - index.html │  │  - index.html  │  │  - index.html  │ │
│  │  - create.html │  │  - create.html │  │  - staff.html  │ │
│  │   - edit.html  │  │   - edit.html  │  │  - public.html │ │
│  │   - show.html  │  │   - show.html  │  └────────────────┘ │
│  └────────────────┘  └────────────────┘                      │
└─────────────────────────────────────────────────────────────┘
                            ▼
         ┌──────────────────────────────────────┐
         │    CAPA DE CONTROLADORES (HTTP)      │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  VeterinarioController       │   │
         │  │  - index()                   │   │
         │  │  - create()                  │   │
         │  │  - store()                   │   │
         │  │  - show()                    │   │
         │  │  - edit()                    │   │
         │  │  - update()                  │   │
         │  │  - delete()                  │   │
         │  └──────────────────────────────┘   │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  AppointmentController       │   │
         │  │  - index()                   │   │
         │  │  - create()                  │   │
         │  │  - store()                   │   │
         │  │  - show()                    │   │
         │  │  - edit()                    │   │
         │  │  - update()                  │   │
         │  │  - delete()                  │   │
         │  └──────────────────────────────┘   │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  AppointmentApiController    │   │
         │  │  - GET /api/citas            │   │
         │  │  - GET /api/citas/{id}       │   │
         │  │  - GET /api/citas/vet/{id}   │   │
         │  │  - POST /api/citas           │   │
         │  │  - PUT /api/citas/{id}       │   │
         │  │  - DELETE /api/citas/{id}    │   │
         │  └──────────────────────────────┘   │
         │                                      │
         └──────────────────────────────────────┘
                            ▼
         ┌──────────────────────────────────────┐
         │    CAPA DE SERVICIOS (Lógica)        │
         │   @Service @Transactional            │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  VeterinarioService          │   │
         │  │  - listarActivos()           │   │
         │  │  - guardar()                 │   │
         │  │  - actualizar()              │   │
         │  │  - eliminar() [soft delete]  │   │
         │  │  - buscarPorId()             │   │
         │  │  - activar()                 │   │
         │  │  - buscarPorEspecialidad()   │   │
         │  │  - existeEmail()             │   │
         │  └──────────────────────────────┘   │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  AppointmentService          │   │
         │  │  - listarTodas()             │   │
         │  │  - citasPorVeterinario()⭐   │   │
         │  │  - citasDeHoy()⭐            │   │
         │  │  - citasPorMascota()         │   │
         │  │  - guardar()                 │   │
         │  │  - cambiarEstado()           │   │
         │  │  - actualizar()              │   │
         │  │  - eliminar()                │   │
         │  └──────────────────────────────┘   │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  UserService                 │   │
         │  │  - guardar()                 │   │
         │  │  - actualizar()              │   │
         │  │  - validarCredenciales()     │   │
         │  │  - existeEmail()             │   │
         │  │  - buscarPorEmail()          │   │
         │  └──────────────────────────────┘   │
         │                                      │
         └──────────────────────────────────────┘
                            ▼
         ┌──────────────────────────────────────┐
         │   CAPA DE REPOSITORIO (Datos)        │
         │   @Repository @JpaRepository         │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  VeterinarioRepository       │   │
         │  │  - findByEmail()             │   │
         │  │  - findByCedula()            │   │
         │  │  - findByEsActivoTrue()      │   │
         │  │  - buscarActivos()           │   │
         │  │  - findByEspecialidad()      │   │
         │  └──────────────────────────────┘   │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  AppointmentRepository       │   │
         │  │  - findByVeterinarioId()     │   │
         │  │  - findByMascotaId()         │   │
         │  │  - findTodayAppointments()   │   │
         │  │  - findByOwnerId()           │   │
         │  │  - countByVeterinarioAndEst()│  │
         │  └──────────────────────────────┘   │
         │                                      │
         │  ┌──────────────────────────────┐   │
         │  │  UserRepository              │   │
         │  │  - findByEmail()             │   │
         │  │  - findByEsActivoTrue()      │   │
         │  │  - findByRoleId()            │   │
         │  │  - existsByEmail()           │   │
         │  └──────────────────────────────┘   │
         │                                      │
         └──────────────────────────────────────┘
                            ▼
         ┌──────────────────────────────────────┐
         │  CAPA DE MODELOS (Entidades JPA)     │
         │  @Entity                             │
         │                                      │
         │  ┌──────────────┐  ┌──────────────┐ │
         │  │    User      │  │     Role     │ │
         │  ├──────────────┤  ├──────────────┤ │
         │  │ id (Long)    │  │ id (Long)    │ │
         │  │ name (String)│  │ name (String)│ │
         │  │ email (Str)  │  │ descripcion  │ │
         │  │ password (E) │  │ @1 ← N users│ │
         │  │ telefono     │  └──────────────┘ │
         │  │ direccion    │                    │
         │  │ esActivo     │  ┌──────────────┐ │
         │  │ @ManyToOne   │  │Veterinario   │ │
         │  │  Role        │  ├──────────────┤ │
         │  │ @OneToMany   │  │ id (Long)    │ │
         │  │  appointments│  │ nombre       │ │
         │  │ @OneToMany   │  │ email (U)    │ │
         │  │  pets        │  │ cedula (U)   │ │
         │  └──────────────┘  │ especialidad │ │
         │                    │ licencia     │ │
         │  ┌──────────────┐  │ telefono     │ │
         │  │     Pet      │  │ biografia    │ │
         │  ├──────────────┤  │ esActivo     │ │
         │  │ id (Long)    │  │ @OneToOne    │ │
         │  │ name (Str)   │  │  User        │ │
         │  │ species(Str) │  │ @OneToMany   │ │
         │  │ raza         │  │  appointments│ │
         │  │ age (Int)    │  └──────────────┘ │
         │  │ color        │                    │
         │  │ numeroMchip  │  ┌──────────────┐ │
         │  │ @ManyToOne   │  │ Appointment  │ │
         │  │  owner(User) │  ├──────────────┤ │
         │  │ @OneToMany   │  │ id (Long)    │ │
         │  │  appointments│  │ fechaHora    │ │
         │  └──────────────┘  │ motivo       │ │
         │                    │ notas        │ │
         │                    │ estado(ENUM)│ │
         │                    │ precio       │ │
         │                    │ @ManyToOne   │ │
         │                    │  mascota     │ │
         │                    │ @ManyToOne   │ │
         │                    │  veterinario │ │
         │                    │ cliente...   │ │
         │                    └──────────────┘ │
         └──────────────────────────────────────┘
                            ▼
         ┌──────────────────────────────────────┐
         │      BASE DE DATOS (MySQL)           │
         │                                      │
         │  ┌────────────┐  ┌────────────┐    │
         │  │ users      │  │ roles      │    │
         │  │ veterinarios│ │ pets       │    │
         │  │ appointments│ │            │    │
         │  └────────────┘  └────────────┘    │
         │                                      │
         └──────────────────────────────────────┘
```

---

## Flujo de Datos - Ver Citas de Veterinario

```
┌─────────────────┐
│   VETERINARIO   │
│   (en web)      │
└────────┬────────┘
         │ Click en "Mis Citas"
         ▼
┌─────────────────────────────────────────┐
│  veterinarios/show.html                 │
│  fetch('/api/citas/veterinario/123')    │
└────────┬────────────────────────────────┘
         │ HTTP GET
         ▼
┌──────────────────────────────────────────┐
│  AppointmentApiController                │
│  @GetMapping("/api/citas/vet/{id}")      │
└────────┬─────────────────────────────────┘
         │ appointmentService.citasPorVeterinario(123)
         ▼
┌──────────────────────────────────────────┐
│  AppointmentService                      │
│  citasPorVeterinario(Long vetId)         │
└────────┬─────────────────────────────────┘
         │ appointmentRepository.findByVeterinarioId(123)
         ▼
┌──────────────────────────────────────────┐
│  AppointmentRepository                   │
│  SELECT * FROM appointments              │
│  WHERE veterinario_id = 123              │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  MySQL Database                          │
│  [Appointment, Appointment, ...]         │
└────────┬─────────────────────────────────┘
         │ JSON Response
         ▼
┌──────────────────────────────────────────┐
│  JavaScript en navegador                 │
│  Renderiza tabla de citas                │
│  Actualiza estadísticas                  │
└──────────────────────────────────────────┘
```

---

## Relaciones JPA

```
┌─────────┐     1 : N     ┌──────────────┐
│  User   │ ─────────────→ │ Appointment  │
│         │                │              │
│ 1 user  │                │ many appts   │
└─────────┘                └──────────────┘
    ▲                             △
    │                             │
    │ 1 : N                       │
    │                        1 : N│ ManyToOne
    │                             │ veterinario
    │                        ┌────┴──────┐
    │        ┌────────────────┤Veterinario│
    │        │    1 : 1       └───────────┘
    │        │
    │        │
    │   ManyToOne               ┌──────┐
    │   Role ◄──────────────────┤ Role │
    │        │                  └──────┘
    │        │                     ▲
    │        │                     │
    │        └─────────────────────┘
    │            1 : N
    │
    └──────────────
        owner_id
         
    Pet
    (1:N)
    ┌────────────────────┐
    │  1 Pet ─ N Appts   │
    └────────────────────┘
          (ManyToOne)
```

---

## Flujo de Seguridad

```
┌─────────────────────────────────┐
│   Usuario accede a /veterinarios │
└────────┬────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  SecurityConfig                          │
│  @EnableMethodSecurity(prePostEnabled)   │
│  verificar usuario autenticado           │
└────────┬─────────────────────────────────┘
         │
         ├─ NO AUTENTICADO ───────────► Redirige a /login
         │
         └─ AUTENTICADO
             │
             ▼
    ┌────────────────────────────────┐
    │  Verificar @PreAuthorize       │
    │  hasAnyRole('ADMIN', 'VET'...) │
    └────┬─────────────────────┬─────┘
         │ NO                  │ SÍ
         ▼                     ▼
    403 ERROR         ┌──────────────┐
    Acceso Denegado   │ Permite acceso
                      │ Ejecuta método
                      └──────────────┘
```

---

## Ciclo de Vida de una Cita

```
PENDIENTE
   ↓ [Usuario confirma]
CONFIRMADA
   ↓ [Veterinario completa]
COMPLETADA
   ✓ (Fin)

Alternativa:
PENDIENTE
   ↓ [Se cancela]
CANCELADA
   ✓ (Fin)

Alternativa:
PENDIENTE
   ↓ [Cliente no viene]
NO_PRESENTADA
   ✓ (Fin)
```

---

## Tecnologías por Capa

```
PRESENTACIÓN:
├─ Thymeleaf (Server-side templates)
├─ Bootstrap 5 (CSS)
├─ Bootstrap Icons (Iconografía)
└─ JavaScript (Validación frontend)

CONTROLADORES:
├─ Spring MVC (@Controller)
├─ Spring REST (@RestController)
├─ Jakarta Validation (@Valid)
└─ Path Variables & Request Params

SERVICIOS:
├─ Spring Beans (@Service)
├─ Transacciones (@Transactional)
├─ Inyección de dependencias
└─ Lógica de negocio

REPOSITORIOS:
├─ Spring Data JPA
├─ Hibernate ORM
├─ Custom @Query
└─ Named queries

MODELOS:
├─ Jakarta Persistence (@Entity)
├─ Lombok (@Getter/@Setter)
├─ Jakarta Validation
└─ Anotaciones JPA

SEGURIDAD:
├─ Spring Security
├─ BCrypt (Password encoding)
├─ Roles & Autorización
└─ Session Management

BASE DE DATOS:
├─ MySQL 8.0
├─ InnoDB (Transacciones)
├─ Foreign Keys
└─ Índices optimizados
```

---

## Flujo de Creación de Veterinario

```
1. Usuario abre /veterinarios/create
   ↓
2. GET VeterinarioController.createForm()
   → Renderiza veterinarios/create.html
   ↓
3. Usuario rellena formulario
   ↓
4. POST a /veterinarios
   → VeterinarioController.store()
   ↓
5. @Valid valida objeto
   → Si error: vuelve a create.html con mensajes
   → Si OK: continúa
   ↓
6. Llama VeterinarioService.guardar()
   ↓
7. VeterinarioService verifica duplicados
   → existeEmail(), existeCedula()
   → Si existen: lanza exception
   ↓
8. Guarda en BD via VeterinarioRepository.save()
   ↓
9. Redirige a /veterinarios/{id}
   ↓
10. Muestra perfil con datos guardados
```

---

**Arquitectura**: Profesional MVC con Spring Boot  
**Escalabilidad**: Preparada para 1000+ usuarios  
**Seguridad**: Enterprise-grade  
**Mantenibilidad**: Código limpio y modular  
