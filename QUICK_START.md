# 🚀 GUÍA RÁPIDA - VALIDACIÓN Y CORREOS HAPPY PAWS

## ¿QUÉ SE IMPLEMENTÓ?

✅ **Validación de campos** - Email, teléfono, números, fechas, DNI, RUC  
✅ **Envío de correos** - SMTP con Gmail  
✅ **Recordatorios automáticos** - Antes de citas (24h y 1h)  
✅ **Endpoints de prueba** - Para validar la configuración  

---

## 🎯 3 PASOS PARA QUE FUNCIONE

### PASO 1: Configurar Gmail (1 minuto)

1. Ve a [Google Account Security](https://myaccount.google.com/security)
2. Activa **2-Step Verification** (si no lo tienes)
3. Busca **App Passwords**
4. Selecciona: Mail + Windows/Linux
5. **Copia la contraseña de 16 caracteres**

### PASO 2: Agregar Variables a Render (2 minutos)

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Abre **Happy Paws**
3. Ve a **Environment**
4. Agrega:
```
MAIL_USERNAME = tu-email@gmail.com
MAIL_PASSWORD = (la contraseña de 16 caracteres que copiaste)
APP_BASE_URL = https://happy-paws.onrender.com
```
5. **Click en Save**

### PASO 3: Hacer Push (2 minutos)

```bash
git add .
git commit -m "feat: validación de campos y recordatorios de correo"
git push
```

**¡Listo!** Render se despliega automáticamente ✅

---

## 🧪 PRUEBAS (5 minutos)

### Test 1: Validar Email
```bash
curl "https://happy-paws.onrender.com/api/test/validate/email/test@gmail.com"
```

Deberías ver:
```json
{
  "campo": "Email: test@gmail.com",
  "mensaje": "✓ Email válido",
  "valido": true
}
```

### Test 2: Enviar Correo

Reemplaza `TU_EMAIL@GMAIL.COM` con tu email:

```bash
curl -X POST "https://happy-paws.onrender.com/api/test/email/send" \
  -G \
  --data-urlencode "to=TU_EMAIL@GMAIL.COM" \
  --data-urlencode "subject=Prueba Happy Paws" \
  --data-urlencode "body=Esto funciona!"
```

Deberías recibir un correo en tu buzón.

### Test 3: Otros Validadores

```bash
# Teléfono
curl "https://happy-paws.onrender.com/api/test/validate/phone/999888777"

# DNI
curl "https://happy-paws.onrender.com/api/test/validate/dni/12345678"

# RUC
curl "https://happy-paws.onrender.com/api/test/validate/ruc/12345678901"

# Fecha
curl "https://happy-paws.onrender.com/api/test/validate/date/25/12/2024"

# Número
curl "https://happy-paws.onrender.com/api/test/validate/number/100"

# Porcentaje
curl "https://happy-paws.onrender.com/api/test/validate/percentage/50"
```

---

## 📧 LOS CORREOS SE ENVÍAN AUTOMÁTICAMENTE

### 1. Al crear una cita:
```
Cliente crea cita → Sistema envía correo de CONFIRMACIÓN
```

### 2. Antes de la cita:
```
24 horas antes → Correo de RECORDATORIO
1 hora antes → Correo URGENTE
```

**No necesitas hacer nada. ¡Los recordatorios se envían solos!**

---

## ❓ SI NO FUNCIONA

### Opción 1: Los correos no se envían

**Checklist rápido:**

```bash
# 1. Verifica las variables en Render
# Ve a Render → Environment → ¿Están MAIL_USERNAME y MAIL_PASSWORD?

# 2. Reinicia la aplicación
# En Render: Settings → Manual Restart

# 3. Revisa los logs
# En Render: Logs → Busca "Correo enviado" o "Error al enviar"

# 4. Prueba una contraseña nueva
# Ve a Gmail → App Passwords → Genera una nueva
# Actualiza en Render
```

### Opción 2: El validador no funciona

```bash
# Verifica con un ejemplo válido
curl "https://happy-paws.onrender.com/api/test/validate/email/usuario@gmail.com"

# Deberías ver: "✓ Email válido"
```

---

## 📚 DOCUMENTACIÓN DETALLADA

Si necesitas más detalles:

- **[EMAIL_CONFIG_GUIDE.md](docs/EMAIL_CONFIG_GUIDE.md)** - Guía completa
- **[VALIDATION_TESTING_GUIDE.md](docs/VALIDATION_TESTING_GUIDE.md)** - Ejemplos de pruebas
- **[SETUP_CHECKLIST.md](docs/SETUP_CHECKLIST.md)** - Pasos detallados
- **[IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md)** - Resumen técnico

---

## 💡 TIPS

1. **Las validaciones ocurren automáticamente** cuando creas una cita en el formulario
2. **No necesitas hacer nada especial** para los recordatorios automáticos
3. **Puedes probar cualquier validator** en `/api/test/validate/`
4. **Los logs de Render** te mostrarán si falla algo

---

## 🎯 RESUMEN

| Lo que tienes que hacer | Tiempo |
|---|---|
| Generar contraseña de aplicación en Gmail | 1 minuto |
| Agregar variables en Render | 2 minutos |
| Hacer push al repositorio | 2 minutos |
| Probar que funciona | 5 minutos |
| **TOTAL** | **~10 minutos** ✅ |

---

## ✅ VERIFICACIÓN FINAL

Cuando todo está listo:

- ✅ Ves "Correo enviado" en los logs de Render
- ✅ Recibes correos de confirmación cuando creo citas
- ✅ Recibes recordatorios 24h y 1h antes
- ✅ Los validadores funcionan en `/api/test/validate/`
- ✅ No hay errores de email no configurado

---

## 🆘 SOPORTE RÁPIDO

**P: ¿Dónde veo si se envió el correo?**  
R: En Render → Logs → Busca "Correo enviado exitosamente"

**P: ¿Cuánto demoran los recordatorios?**  
R: Se envían cada 30 minutos a 1 hora (y cada hora para 24h)

**P: ¿Funciona con otros emails que no sean Gmail?**  
R: Sí, pero necesitas cambiar los valores de SMTP en application.properties

**P: ¿Qué pasa si me equivoco con la contraseña?**  
R: Los correos no se envían, ves un error en los logs. Genera una nueva contraseña.

**P: ¿Los clientes reciben los recordatorios automáticamente?**  
R: Sí, si tienen un email configurado en su perfil.

---

**¡Eso es todo! Ya tienes validación y recordatorios por correo funcionando** 🎉
