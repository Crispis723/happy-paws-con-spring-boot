# 📧 Guía Completa de Validación y Envío de Correos

## 🚀 Introducción

Este documento explica cómo usar los nuevos servicios de **validación de campos** y **envío de correos con recordatorios automáticos** en Happy Paws.

---

## 🔧 Configuración Inicial

### 1. Variables de Entorno Requeridas

Agrega estas variables a tu archivo `.env` o en Render:

```bash
# Configuración de Sendlib
SENDLIB_API_KEY=tu-api-key-de-sendlib
SENDLIB_FROM=Happy Paws <tu-email@gmail.com>
SENDLIB_ENDPOINT=https://sendlib.samueltuoyo.com/api/send

# URL base de la aplicación
APP_BASE_URL=https://happy-paws.onrender.com
```

### 2. Obtener la API key de Sendlib

Conecta la cuenta Gmail desde Sendlib, crea una API key y configúrala como
`SENDLIB_API_KEY` en Render. El remitente de `SENDLIB_FROM` debe ser la cuenta
Gmail conectada a Sendlib.

### 3. Configuración en application.properties

Ya está configurado automáticamente, pero puedes verificar:

```properties
# API REST de Sendlib
sendlib.api-key=${SENDLIB_API_KEY:}
sendlib.from=${SENDLIB_FROM:}
sendlib.endpoint=${SENDLIB_ENDPOINT:https://sendlib.samueltuoyo.com/api/send}
```

---

## 📝 Servicios de Validación

### FieldValidationService

Servicio inyectable que valida campos comunes:

```java
@Autowired
private FieldValidationService validationService;

// Ejemplos de uso:
boolean emailValido = validationService.esEmailValido("user@example.com");
boolean telefonoValido = validationService.esTelefonoValido("999999999");
boolean rucValido = validationService.esRucValido("12345678901");
boolean dniValido = validationService.esDniValido("12345678");
boolean fechaValida = validationService.esFechaValida("25/12/2024");
boolean porcentajeValido = validationService.esPorcentajeValido(50.0);
boolean numeroPositivo = validationService.esNumeroPositivo(100.0);
```

### Validaciones Disponibles

| Validación | Método | Ejemplo |
|---|---|---|
| **Email** | `esEmailValido(String)` | user@example.com |
| **Teléfono** | `esTelefonoValido(String)` | 999999999 o +51999999999 |
| **RUC** | `esRucValido(String)` | 12345678901 (11 dígitos) |
| **DNI** | `esDniValido(String)` | 12345678 (8 dígitos) |
| **Fecha** | `esFechaValida(String)` | 25/12/2024 (DD/MM/YYYY) |
| **Fecha y Hora** | `esFechaHoraValida(String)` | 25/12/2024 14:30 |
| **Número Positivo** | `esNumeroPositivo(Number)` | 100, 99.99 |
| **Número No Negativo** | `esNumeroNoNegativo(Number)` | 0, 100, 99.99 |
| **Porcentaje** | `esPorcentajeValido(Number)` | 0-100 |
| **Fecha Futura** | `esFechaFutura(LocalDateTime)` | Mañana en adelante |
| **Longitud de Texto** | `esLongitudValida(String, min, max)` | Entre X y Y caracteres |

---

## 📧 Servicio de Correos

### EmailService

Proporciona métodos para enviar correos simples, HTML y múltiples:

```java
@Autowired
private EmailService emailService;

// Correo simple
emailService.enviarCorreoSimple(
    "cliente@example.com",
    "Asunto",
    "Cuerpo del mensaje"
);

// Correo HTML
emailService.enviarCorreoHTML(
    "cliente@example.com",
    "Asunto",
    "<h1>Hola</h1><p>Contenido HTML</p>"
);

// Correo múltiple
String[] emails = {"email1@example.com", "email2@example.com"};
emailService.enviarCorreoMultiple(emails, "Asunto", "Cuerpo");

// Usar plantillas
String html = emailService.generarPlantillaRecordatorioCita(
    "Juan Pérez",      // Nombre cliente
    "Max",             // Nombre mascota
    "25/12/2024 14:30", // Fecha y hora
    "Dr. García"       // Nombre veterinario
);
emailService.enviarCorreoHTML("cliente@example.com", "Recordatorio de Cita", html);
```

### Plantillas Disponibles

1. **Recordatorio de Cita** (24 horas antes)
   ```java
   emailService.generarPlantillaRecordatorioCita(nombre, mascota, fecha, veterinario)
   ```

2. **Confirmación de Cita** (al crear)
   ```java
   emailService.generarPlantillaConfirmacionCita(nombre, mascota, fecha, veterinario, motivo)
   ```

---

## ⏰ Sistema de Recordatorios Automáticos

### AppointmentReminderService

Se ejecuta automáticamente según estos horarios:

- **Cada hora**: Envía recordatorios a 24 horas de las citas
- **Cada 30 minutos**: Envía recordatorios a 1 hora de las citas

### Configuración

El servicio está habilitado automáticamente con `@EnableScheduling` en `DemoApplication.java`

### Métodos Disponibles

```java
@Autowired
private AppointmentReminderService reminderService;

// Forzar envío manual de recordatorios
reminderService.enviarRecordatorios24Horas();  // Recordatorios de 24 horas
reminderService.enviarRecordatorios1Hora();    // Recordatorios de 1 hora
reminderService.enviarRecordatorioCita(cita);  // Una cita específica
reminderService.enviarRecordatorioInminente(cita); // Recordatorio de 1 hora
```

---

## 🧪 Pruebas de Correos

Usa los endpoints de prueba para validar tu configuración:

### Endpoints Disponibles

#### 1. Enviar Correo Simple de Prueba
```bash
POST http://localhost:8080/api/test/email/send?to=correo@example.com&subject=Prueba&body=Contenido
```

#### 2. Enviar Correo HTML de Prueba
```bash
POST http://localhost:8080/api/test/email/send-html?to=correo@example.com
```

#### 3. Validar Email
```bash
GET http://localhost:8080/api/test/validate/email/correo@example.com
```

**Respuesta:**
```json
{
  "campo": "Email: correo@example.com",
  "mensaje": "✓ Email válido",
  "valido": true
}
```

#### 4. Validar Teléfono
```bash
GET http://localhost:8080/api/test/validate/phone/999999999
```

#### 5. Validar RUC
```bash
GET http://localhost:8080/api/test/validate/ruc/12345678901
```

#### 6. Validar DNI
```bash
GET http://localhost:8080/api/test/validate/dni/12345678
```

#### 7. Validar Fecha
```bash
GET http://localhost:8080/api/test/validate/date/25/12/2024
```

#### 8. Validar Número
```bash
GET http://localhost:8080/api/test/validate/number/123.45
```

#### 9. Validar Porcentaje
```bash
GET http://localhost:8080/api/test/validate/percentage/50
```

---

## 🔍 Solución de Problemas

### ❌ "No se envían correos"

**Posibles causas:**

1. **Variables de entorno no configuradas**
   - Verifica que `SENDLIB_API_KEY` y `SENDLIB_FROM` estén definidas
   - Reinicia la aplicación después de cambiar variables

2. **API key o remitente de Sendlib incorrectos**
   - Genera una API key nueva desde Sendlib
   - Comprueba que `SENDLIB_FROM` sea una cuenta Gmail conectada a Sendlib

3. **Endpoint de Sendlib inaccesible**
   - Comprueba `SENDLIB_ENDPOINT=https://sendlib.samueltuoyo.com/api/send`
   - Revisa la respuesta HTTP registrada por la aplicación

4. **Email no configurado en el cliente**
   - Verifica que el cliente tenga un email válido en la BD
   - Usa `/api/test/validate/email/` para validar

5. **Revisar logs**
   ```bash
   # En la consola o logs:
   # "Email no configurado. Omitiendo envío a: ..."
   # Indica que las variables no están cargadas
   ```

### ✅ Verificar Configuración

1. **Endpoint de prueba de email:**
   ```bash
   POST http://localhost:8080/api/test/email/send?to=tu-email@gmail.com&subject=Prueba&body=Funciona
   ```

2. **Ver logs en consola:**
   - Busca "Correo enviado exitosamente"
   - O busca "Error al enviar correo"

3. **Verificar credenciales:**
   ```bash
   curl -X GET "http://localhost:8080/api/test/validate/email/tu-email@gmail.com"
   ```

---

## 📦 Uso en Controladores

### Ejemplo: Crear Cita con Correo de Confirmación

```java
@PostMapping("/api/citas")
public ResponseEntity<?> crearCita(@Valid @RequestBody AppointmentDTO dto) {
    // Validar
    if (!validationService.esFechaFutura(dto.getFechaHora())) {
        return ResponseEntity.badRequest().body("La fecha debe ser futura");
    }
    
    // Crear cita
    Appointment cita = appointmentService.crearCita(dto);
    
    // Enviar correo de confirmación
    try {
        Cliente cliente = cita.getCliente();
        if (cliente != null && cliente.getEmail() != null) {
            String html = emailService.generarPlantillaConfirmacionCita(
                cliente.getRazonSocial(),
                cita.getMascota().getNombre(),
                cita.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                cita.getVeterinario().getName(),
                cita.getMotivo()
            );
            emailService.enviarCorreoHTML(cliente.getEmail(), "✓ Cita Confirmada", html);
        }
    } catch (Exception e) {
        log.error("Error al enviar correo de confirmación: {}", e.getMessage());
    }
    
    return ResponseEntity.ok(cita);
}
```

---

## 📊 Monitoreo de Recordatorios

Los recordatorios se ejecutan automáticamente. Para verlos:

1. **En logs locales:**
   ```
   Iniciando envío de recordatorios de citas (24 horas antes)
   Se encontraron 3 citas para recordatorio
   Recordatorio enviado a cliente@example.com para cita en 25/12/2024 14:30
   ```

2. **En Render logs:**
   - Ve a tu aplicación en Render
   - Abre la sección "Logs"
   - Busca "AppointmentReminderService" o "Correo enviado"

---

## 🎯 Resumen de Nuevas Funcionalidades

✅ Validación completa de campos (email, teléfono, números, fechas)  
✅ Servicio de correos SMTP con plantillas HTML  
✅ Recordatorios automáticos a 24 horas y 1 hora antes de citas  
✅ Endpoints de prueba para validar configuración  
✅ Manejo robusto de errores con logging  
✅ Compatible con Gmail, Outlook y otros SMTP  

---

## 📞 Soporte

Si tienes problemas:

1. Verifica los logs de la aplicación
2. Comprueba las variables de entorno
3. Usa los endpoints de prueba
4. Revisa que el email del cliente esté configurado
5. Asegúrate de haber generado contraseña de aplicación (Gmail)
