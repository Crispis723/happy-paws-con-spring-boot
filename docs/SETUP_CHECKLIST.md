# ✅ Checklist de Configuración - Validación y Correos

## 🎯 Objetivo
Habilitar validación de campos y envío de recordatorios por correo en Happy Paws

---

## ✅ PASO 1: Verificar Archivos Creados/Modificados

### Archivos Nuevos Creados:
- ✅ `src/main/java/com/Happypaws/demo/config/MailConfig.java`
- ✅ `src/main/java/com/Happypaws/demo/validation/PositiveNumber.java`
- ✅ `src/main/java/com/Happypaws/demo/validation/PositiveNumberValidator.java`
- ✅ `src/main/java/com/Happypaws/demo/service/FieldValidationService.java`
- ✅ `src/main/java/com/Happypaws/demo/controller/TestValidationController.java`
- ✅ `docs/EMAIL_CONFIG_GUIDE.md`
- ✅ `docs/VALIDATION_TESTING_GUIDE.md`

### Archivos Modificados:
- ✅ `src/main/java/com/Happypaws/demo/DemoApplication.java` (agregado @EnableScheduling)
- ✅ `src/main/java/com/Happypaws/demo/dto/AppointmentDTO.java` (validaciones mejoradas)
- ✅ `src/main/java/com/Happypaws/demo/service/EmailService.java` (ya existía, verificado)
- ✅ `src/main/java/com/Happypaws/demo/service/AppointmentReminderService.java` (ya existía, verificado)
- ✅ `src/main/resources/application.properties` (ya configurado)

---

## ✅ PASO 2: Configurar Variables de Entorno

### En Desarrollo Local (application-dev.properties)

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-password-app-gmail
```

### En Render (Variables de Entorno)

Ve a [Render Dashboard](https://dashboard.render.com/):

1. Abre tu aplicación Happy Paws
2. Ve a **Environment**
3. Agrega estas variables:

```
MAIL_USERNAME = tu-email@gmail.com
MAIL_PASSWORD = tu-password-app-gmail
APP_BASE_URL = https://happy-paws.onrender.com
```

---

## ✅ PASO 3: Generar Contraseña de Aplicación (Gmail)

**IMPORTANTE: No uses tu contraseña de Gmail normal**

1. Ve a [Google Account](https://myaccount.google.com)
2. Click en **Security** (Seguridad)
3. Activa **2-Step Verification** si no la tienes
4. Busca **App Passwords**
5. Selecciona:
   - App: `Mail`
   - Device: `Windows/Linux`
6. Google te generará una contraseña de 16 caracteres
7. Copia esa contraseña en `MAIL_PASSWORD`

**Ejemplo:**
```
Tu contraseña de app: qwer tyui asdf ghjk
(Sin espacios): qwertyuiasdfghjk
```

---

## ✅ PASO 4: Compilar y Ejecutar

### Local

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# Deberías ver en los logs:
# [main] o.s.s.s.ThreadPoolTaskScheduler : Initializing ExecutorService 'taskScheduler'
# Esto confirma que @EnableScheduling está activo
```

### En Render

1. Haz push a tu repositorio (automáticamente se despliega)
2. Ve a **Logs** en Render
3. Busca mensajes de confirmación del servicio

---

## ✅ PASO 5: Probar Validaciones

### Test 1: Validar Email

```bash
curl "http://localhost:8080/api/test/validate/email/test@example.com"
```

**Respuesta esperada:**
```json
{
  "campo": "Email: test@example.com",
  "mensaje": "✓ Email válido",
  "valido": true
}
```

### Test 2: Validar Teléfono

```bash
curl "http://localhost:8080/api/test/validate/phone/999888777"
```

### Test 3: Validar Fecha

```bash
curl "http://localhost:8080/api/test/validate/date/25/12/2024"
```

### Test 4: Validar DNI

```bash
curl "http://localhost:8080/api/test/validate/dni/12345678"
```

### Test 5: Validar RUC

```bash
curl "http://localhost:8080/api/test/validate/ruc/12345678901"
```

---

## ✅ PASO 6: Probar Envío de Correos

### Test 1: Correo Simple

```bash
curl -X POST "http://localhost:8080/api/test/email/send" \
  -G \
  --data-urlencode "to=tu-email@gmail.com" \
  --data-urlencode "subject=Prueba Happy Paws" \
  --data-urlencode "body=Este es un correo de prueba"
```

**Respuesta esperada:**
```json
"Correo enviado exitosamente a: tu-email@gmail.com"
```

### Test 2: Correo HTML

```bash
curl -X POST "http://localhost:8080/api/test/email/send-html?to=tu-email@gmail.com"
```

**Deberías recibir un correo HTML formatado en tu buzón**

---

## ✅ PASO 7: Verificar Recordatorios Automáticos

### Verificar en Logs

Busca estos mensajes en los logs de la aplicación:

```
Iniciando envío de recordatorios de citas (24 horas antes)
Se encontraron X citas para recordatorio

Iniciando envío de recordatorios de citas (1 hora antes)
Se encontraron X citas inminentes para recordatorio
```

### Probar Manualmente

1. Crea una cita con una fecha futura
2. Verifica que el cliente tiene un email válido
3. Observa los logs para confirmar envío

---

## ✅ PASO 8: Integrar Validaciones en Controladores

### Ejemplo: Controlador de Citas

```java
@PostMapping("/api/citas")
public ResponseEntity<?> crearCita(
    @Valid @RequestBody AppointmentDTO dto,
    BindingResult bindingResult) {
    
    // Validar errores de @Valid
    if (bindingResult.hasErrors()) {
        List<String> errores = bindingResult.getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errores);
    }
    
    // Validar fecha futura (adicional)
    if (!validationService.esFechaFutura(dto.getFechaHora())) {
        return ResponseEntity.badRequest()
            .body("La fecha de la cita debe ser en el futuro");
    }
    
    // Crear cita
    Appointment cita = appointmentService.crearCita(dto);
    
    // Enviar confirmación
    try {
        Cliente cliente = cita.getCliente();
        String html = emailService.generarPlantillaConfirmacionCita(
            cliente.getRazonSocial(),
            cita.getMascota().getNombre(),
            cita.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            cita.getVeterinario().getName(),
            cita.getMotivo()
        );
        emailService.enviarCorreoHTML(
            cliente.getEmail(),
            "✓ Cita Confirmada - Happy Paws",
            html
        );
    } catch (Exception e) {
        log.error("Error al enviar correo", e);
        // No fallar la operación aunque falle el email
    }
    
    return ResponseEntity.ok(cita);
}
```

---

## ✅ PASO 9: Solucionar Problemas

### ❌ "No se envían correos"

**Checklist:**

1. **Verificar variables de entorno**
   ```bash
   # En logs, deberías ver:
   # "Email no configurado" → Las variables no están cargadas
   ```

2. **Verificar credenciales Gmail**
   - ¿Usaste una contraseña de APP, no la contraseña de cuenta?
   - ¿Generaste la contraseña en Google Account?

3. **Verificar conectividad**
   ```bash
   curl -X POST "http://localhost:8080/api/test/email/send?to=tu-email@gmail.com&subject=Test&body=Test"
   ```

4. **Revisar logs**
   - Busca "Correo enviado exitosamente" (éxito)
   - Busca "Error al enviar correo" (fallo)

5. **Verificar email del cliente**
   - ¿El cliente en BD tiene email configurado?
   - ¿Es un email válido?

### ❌ "Recordatorios no se envían"

**Checklist:**

1. **Verificar @EnableScheduling**
   - Confirma que está en `DemoApplication.java`
   - Busca en logs: "Initializing ExecutorService 'taskScheduler'"

2. **Verificar citas programadas**
   - ¿Existen citas en BD?
   - ¿Las fechas están dentro de los rangos de envío (24h o 1h)?

3. **Esperar tiempo suficiente**
   - El servicio se ejecuta cada 30 minutos (1 hora)
   - Espera al menos 30 minutos después de crear la cita

4. **Forzar ejecución manual** (solo desarrollo)
   ```bash
   # Llamar directamente al endpoint de prueba
   curl -X POST "http://localhost:8080/api/test/email/send-html?to=cliente@example.com"
   ```

---

## ✅ PASO 10: Monitorear en Producción (Render)

### Verificar Logs en Render

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Abre tu servicio Happy Paws
3. Click en **Logs**
4. Filtra por:
   - "AppointmentReminderService" → Ver recordatorios
   - "Correo enviado" → Ver correos enviados
   - "Error al enviar" → Ver errores

### Configurar Alertas (Opcional)

1. Ve a **Settings** en Render
2. Agrega un webhook para notificaciones de error
3. Recibe alertas si falla el envío de correos

---

## 📊 Resumen de Funcionalidades Implementadas

| Funcionalidad | Archivo | Estado |
|---|---|---|
| **Validación de Email** | `FieldValidationService.java` | ✅ |
| **Validación de Teléfono** | `FieldValidationService.java` | ✅ |
| **Validación de Números** | `FieldValidationService.java` | ✅ |
| **Validación de Fechas** | `FieldValidationService.java` | ✅ |
| **Validación de DNI/RUC** | `FieldValidationService.java` | ✅ |
| **Envío de Correos SMTP** | `EmailService.java` | ✅ |
| **Recordatorios 24h** | `AppointmentReminderService.java` | ✅ |
| **Recordatorios 1h** | `AppointmentReminderService.java` | ✅ |
| **Plantillas HTML** | `EmailService.java` | ✅ |
| **Endpoints de Prueba** | `TestValidationController.java` | ✅ |
| **@EnableScheduling** | `DemoApplication.java` | ✅ |
| **Configuración MailConfig** | `MailConfig.java` | ✅ |

---

## 🎉 ¡Listo!

Cuando completes todos los pasos, tendrás:

✅ **Validación completa** de email, teléfono, números, fechas, DNI/RUC  
✅ **Envío de correos** SMTP funcional con Gmail  
✅ **Recordatorios automáticos** a 24 horas y 1 hora antes de citas  
✅ **Correos de confirmación** cuando se crea una cita  
✅ **Endpoints de prueba** para validar la configuración  
✅ **Plantillas HTML** profesionales para correos  
✅ **Manejo robusto de errores** con logging completo  

---

## 📞 Soporte Rápido

| Problema | Solución |
|---|---|
| No se envían correos | Ver PASO 6 y Solucionar Problemas |
| Email no válido | Verificar FieldValidationService |
| Recordatorios no se envían | Ver PASO 10 y Solucionar Problemas |
| Errores de compilación | `mvn clean compile` |
| Variables de entorno no funciona | Reiniciar la aplicación |

---

**¡Espero que esta solución completa te ayude a implementar validación y recordatorios por correo en Happy Paws!** 🚀
