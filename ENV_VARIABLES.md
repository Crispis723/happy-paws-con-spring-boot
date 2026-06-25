# Tabla Rápida de Environment Variables

| Variable | Descripción | Ejemplo | Obligatoria |
|----------|------------|---------|------------|
| `DB_HOST` | Host/servidor de MySQL | `juan.mysql.pythonanywhere.com` | ✅ Sí |
| `DB_PORT` | Puerto MySQL | `3306` | ✅ Sí |
| `DB_NAME` | Nombre de la BD | `juan$veterinariadb` | ✅ Sí |
| `DB_USERNAME` | Usuario de BD | `juan$veterinariadb` | ✅ Sí |
| `DB_PASSWORD` | Contraseña de BD | `miPassword123` | ✅ Sí |
| `SERVER_PORT` | Puerto del servidor Spring Boot | `8080` | ⚠️ Opcional (default: 8080) |
| `ENVIRONMENT` | Entorno (development/production) | `production` | ⚠️ Opcional |

## ¿Dónde obtener estas variables?

### Para PythonAnywhere (Recomendado):
- Crea una BD MySQL en [PythonAnywhere.com](https://www.pythonanywhere.com/web_app_setup/)
- Tus datos serán como:
  - **Host**: `tuusuario.mysql.pythonanywhere.com`
  - **Usuario/BD**: `tuusuario$midb`
  - **Puerto**: Siempre `3306`

### Para Railway:
- Crea un proyecto con MySQL
- Copia las variables del panel de Railway

### Para otros proveedores:
- Contacta a tu proveedor de hosting para los datos de conexión

## Pasos en Replit:

1. **Import project** desde tu GitHub
2. **Click en Secrets** (icono de llave)
3. **Agrega cada variable** de la tabla arriba
4. **Click en Run** para iniciar
5. **Espera 2-3 minutos** la primera compilación
6. ¡Listo! Tu app está en vivo

---
📝 **Archivo de configuración**: [DESPLIEGUE_REPLIT.md](./DESPLIEGUE_REPLIT.md)
