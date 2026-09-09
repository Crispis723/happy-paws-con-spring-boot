# Validación de Campos - Pruebas y Ejemplos

## 📋 Resumen Rápido de Endpoints

Todos los endpoints de validación están en: `http://localhost:8080/api/test/validate/`

---

## 🧪 Ejemplos de Pruebas

### 1. Validar Email

**Válido:**
```bash
curl "http://localhost:8080/api/test/validate/email/cliente@happypaws.com"
```

**Respuesta:**
```json
{
  "campo": "Email: cliente@happypaws.com",
  "mensaje": "✓ Email válido",
  "valido": true
}
```

**Inválido:**
```bash
curl "http://localhost:8080/api/test/validate/email/email-invalido"
```

```json
{
  "campo": "Email: email-invalido",
  "mensaje": "✗ Email inválido",
  "valido": false
}
```

---

### 2. Validar Teléfono

**Formatos aceptados:**
- `999999999` (9 dígitos)
- `+51999999999` (con código de país)
- `+1-555-555-5555` (con guiones)

**Válido:**
```bash
curl "http://localhost:8080/api/test/validate/phone/999999999"
```

**Respuesta:**
```json
{
  "campo": "Teléfono: 999999999",
  "mensaje": "✓ Teléfono válido",
  "valido": true
}
```

---

### 3. Validar RUC

**Formato:** Exactamente 11 dígitos

**Válido:**
```bash
curl "http://localhost:8080/api/test/validate/ruc/12345678901"
```

**Respuesta:**
```json
{
  "campo": "RUC: 12345678901",
  "mensaje": "✓ RUC válido (11 dígitos)",
  "valido": true
}
```

**Inválido:**
```bash
curl "http://localhost:8080/api/test/validate/ruc/1234567890"
# Falta un dígito
```

---

### 4. Validar DNI

**Formato:** Exactamente 8 dígitos

**Válido:**
```bash
curl "http://localhost:8080/api/test/validate/dni/12345678"
```

**Respuesta:**
```json
{
  "campo": "DNI: 12345678",
  "mensaje": "✓ DNI válido (8 dígitos)",
  "valido": true
}
```

---

### 5. Validar Fecha

**Formato:** DD/MM/YYYY (día/mes/año)

**Válido:**
```bash
curl "http://localhost:8080/api/test/validate/date/25/12/2024"
```

**Respuesta:**
```json
{
  "campo": "Fecha: 25/12/2024",
  "mensaje": "✓ Fecha válida (DD/MM/YYYY)",
  "valido": true
}
```

**Inválido (fecha inexistente):**
```bash
curl "http://localhost:8080/api/test/validate/date/31/02/2024"
# Febrero no tiene 31 días
```

```json
{
  "campo": "Fecha: 31/02/2024",
  "mensaje": "✗ Fecha inválida",
  "valido": false
}
```

---

### 6. Validar Número

**Requisito:** Debe ser positivo (> 0)

**Válido:**
```bash
curl "http://localhost:8080/api/test/validate/number/123.45"
```

**Respuesta:**
```json
{
  "campo": "Número: 123.45",
  "mensaje": "✓ Número positivo válido",
  "valido": true
}
```

**Inválido (negativo o cero):**
```bash
curl "http://localhost:8080/api/test/validate/number/0"
```

```json
{
  "campo": "Número: 0",
  "mensaje": "✗ Número no es positivo",
  "valido": false
}
```

---

### 7. Validar Porcentaje

**Rango:** 0 a 100

**Válido:**
```bash
curl "http://localhost:8080/api/test/validate/percentage/50"
```

**Respuesta:**
```json
{
  "campo": "Porcentaje: 50",
  "mensaje": "✓ Porcentaje válido (0-100)",
  "valido": true
}
```

**Inválido (fuera de rango):**
```bash
curl "http://localhost:8080/api/test/validate/percentage/150"
```

```json
{
  "campo": "Porcentaje: 150",
  "mensaje": "✗ Porcentaje fuera de rango",
  "valido": false
}
```

---

## 📧 Pruebas de Envío de Correos

### 1. Enviar Correo Simple

```bash
curl -X POST "http://localhost:8080/api/test/email/send?to=cliente@example.com&subject=Hola&body=Este%20es%20un%20correo%20de%20prueba"
```

**Respuesta exitosa:**
```json
"Correo enviado exitosamente a: cliente@example.com"
```

**Respuesta con error:**
```json
"El email 'cliente@invalido' no es válido"
```

---

### 2. Enviar Correo HTML

```bash
curl -X POST "http://localhost:8080/api/test/email/send-html?to=cliente@example.com"
```

**Respuesta:**
```json
"Correo HTML enviado exitosamente a: cliente@example.com"
```

---

## 🔒 Validaciones de DTOs

### AppointmentDTO

Las validaciones se aplican automáticamente al crear/actualizar citas:

```json
{
  "petId": 1,                          // @Positive - debe ser > 0
  "clienteId": 2,                      // @Positive - debe ser > 0
  "veterinarioId": 3,                  // @Positive - debe ser > 0
  "fechaHora": "2024-12-25T14:30:00",  // @Future - debe ser en el futuro
  "motivo": "Revisión general"          // @Size(5-255) - debe tener 5-255 caracteres
}
```

**Errores de validación:**

- `petId: null` → "La mascota es requerida"
- `petId: -1` → "El ID de la mascota debe ser un número positivo"
- `fechaHora: 2024-01-01T14:30:00` (pasado) → "La fecha y hora debe ser en el futuro"
- `motivo: "Revisión"` (4 caracteres) → "El motivo debe tener entre 5 y 255 caracteres"

---

## 📝 Tabla de Referencia Rápida

| Campo | Validación | Ejemplo Válido | Ejemplo Inválido |
|-------|-----------|-----------------|------------------|
| Email | RFC 5322 | user@example.com | user@.com |
| Teléfono | 7-15 dígitos | 999999999 | 12345 |
| RUC | Exacto 11 | 12345678901 | 1234567890 |
| DNI | Exacto 8 | 12345678 | 1234567 |
| Fecha | DD/MM/YYYY | 25/12/2024 | 25-12-2024 |
| Número | > 0 | 100, 99.99 | 0, -50 |
| Porcentaje | 0-100 | 50, 0, 100 | 101, -5 |

---

## 🛠️ Uso Programático

### En un Controlador

```java
@Autowired
private FieldValidationService validationService;

@PostMapping("/clientes")
public ResponseEntity<?> crearCliente(@RequestBody ClienteDTO dto) {
    // Validar email
    if (!validationService.esEmailValido(dto.getEmail())) {
        return ResponseEntity.badRequest().body("Email inválido");
    }
    
    // Validar teléfono
    if (!validationService.esTelefonoValido(dto.getTelefono())) {
        return ResponseEntity.badRequest().body("Teléfono inválido");
    }
    
    // Validar documento
    if (dto.getTipoDocumento().equals("RUC")) {
        if (!validationService.esRucValido(dto.getNumeroDocumento())) {
            return ResponseEntity.badRequest().body("RUC inválido");
        }
    }
    
    // Proceder
    return ResponseEntity.ok(clienteService.crear(dto));
}
```

### En un Servicio

```java
@Service
public class CitaService {
    @Autowired
    private FieldValidationService validationService;
    
    public void crearCita(AppointmentDTO dto) {
        // Validar fecha
        if (!validationService.esFechaFutura(dto.getFechaHora())) {
            throw new IllegalArgumentException("La cita debe ser en el futuro");
        }
        
        // Validar rango horario (8 AM a 6 PM)
        int hora = dto.getFechaHora().getHour();
        if (hora < 8 || hora > 18) {
            throw new IllegalArgumentException("Hora de cita no permitida");
        }
    }
}
```

---

## 🔄 Ciclo Completo de Ejemplo

### 1. Usuario crea una cita

```bash
POST /api/citas
{
  "petId": 5,
  "clienteId": 3,
  "veterinarioId": 7,
  "fechaHora": "2024-12-25T14:30:00",
  "motivo": "Vacunación anual"
}
```

### 2. Sistema valida todos los campos

- ✅ petId: 5 > 0
- ✅ clienteId: 3 > 0
- ✅ veterinarioId: 7 > 0
- ✅ fechaHora: 2024-12-25 > hoy
- ✅ motivo: 19 caracteres (entre 5-255)

### 3. Sistema envía correo de confirmación

```
Para: cliente@happypaws.com
Asunto: ✓ Cita Confirmada - Happy Paws
Contenido: Plantilla HTML con detalles de la cita
```

### 4. Sistema agenda recordatorios automáticos

- 24 horas antes: Envía recordatorio
- 1 hora antes: Envía recordatorio urgente

---

## 📞 Recursos

- **Configuración SMTP:** `application.properties`
- **Servicio de validación:** `FieldValidationService.java`
- **Servicio de correos:** `EmailService.java`
- **Servicio de recordatorios:** `AppointmentReminderService.java`
- **Controlador de pruebas:** `TestValidationController.java`
