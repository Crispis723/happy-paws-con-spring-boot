# 🚀 Guía de Despliegue a Replit - Happy Paws

## Paso 1: Preparar la Base de Datos

Antes de subir a Replit, necesitas una base de datos MySQL en la nube:

### Opción A: MySQL PythonAnywhere (Recomendado para Replit)
1. Crea cuenta en [PythonAnywhere.com](https://www.pythonanywhere.com)
2. Ve a la sección "MySQL" 
3. Crea una base de datos nueva
4. Anota los datos de conexión:
   - **Host**: `tuuser.mysql.pythonanywhere.com`
   - **Usuario**: `tuuser$tudb`
   - **Contraseña**: (que configuraste)
   - **Base de datos**: `tuuser$veterinariadb`

### Opción B: Railway.app
1. Crea cuenta en [Railway.app](https://railway.app)
2. Crea un nuevo servicio MySQL
3. Copia los datos de conexión

### Opción C: Hostinger/GoDaddy
Usa los datos que ya tengas de tu proveedor

## Paso 2: Subir el Proyecto a Replit

1. Inicia sesión en [Replit.com](https://replit.com)
2. Click en "Create Repl" → "Import from GitHub"
3. Pega la URL de tu repositorio: `https://github.com/Crispis723/happy-paws-con-spring-boot`
4. Selecciona Java como lenguaje
5. Click en "Create Repl"

## Paso 3: Configurar las Variables de Entorno en Replit

### En la interfaz de Replit:

1. Click en el icono de **"Secrets"** (llave) en la barra lateral izquierda
2. Agrega estas variables (una por una):

```
DB_HOST = tuuser.mysql.pythonanywhere.com
DB_PORT = 3306
DB_NAME = tuuser$veterinariadb
DB_USERNAME = tuuser$veterinariadb
DB_PASSWORD = tu_contraseña_aqui
SERVER_PORT = 8080
```

### Valores de ejemplo (PythonAnywhere):
```
DB_HOST = juan.mysql.pythonanywhere.com
DB_PORT = 3306
DB_NAME = juan$clinicaveterinaria
DB_USERNAME = juan$clinicaveterinaria
DB_PASSWORD = miPassword123
SERVER_PORT = 8080
```

## Paso 4: Inicializar la Base de Datos

1. Antes de ejecutar por primera vez, necesitas crear la BD en tu host:
   - Conéctate con un cliente MySQL (MySQL Workbench, phpMyAdmin, etc.)
   - Crea la base de datos vacía

2. Alternativamente, ejecuta el archivo `database.sql`:
   ```sql
   mysql -h tu_host -u tu_usuario -p tu_password -e "source database.sql"
   ```

## Paso 5: Ejecutar en Replit

1. Haz click en el botón **"Run"** (arriba)
2. Espera a que se compile (puede tardar 2-3 minutos la primera vez)
3. Una vez que aparezca "Listening on port 8080", tu app está lista
4. Click en el enlace que aparece en la parte superior para abrir tu app

## Solución de Problemas

### Error: "Cannot connect to database"
- ✅ Verifica que los datos de BD sean correctos en Secrets
- ✅ Asegúrate de que la BD está creada
- ✅ Prueba la conexión con MySQL Workbench primero

### Error: "Java version not supported"
- Replit usa Java 21 por defecto en el archivo `.replit`
- Si falla, cambia a Java 17 en `.replit`:
  ```
  [nix]
  channel = "stable-23.11"
  [[nix.packages]]
  package = "jdk17"
  ```

### La app se cuelga en el puerto
- Spring Boot por defecto usa puerto 8080
- Replit mapea automáticamente. Deja `SERVER_PORT = 8080`

### Importar datos iniciales
1. Coloca un archivo `data.sql` en `src/main/resources/`
2. Spring Boot lo ejecutará automáticamente al iniciar

## Comandos Útiles en la Terminal de Replit

```bash
# Limpiar y compilar
mvn clean package

# Solo compilar
mvn clean install

# Ver logs
tail -f application.log

# Ver estado de la BD
mysql -h $DB_HOST -u $DB_USERNAME -p$DB_PASSWORD $DB_NAME
```

## Notas Importantes

⚠️ **Seguridad:**
- Nunca guardes contraseñas en el código
- Siempre usa Secrets/Environment Variables
- En producción, usa SSL/TLS para la BD

⚠️ **Recursos Replit:**
- Plan gratuito tiene límites de memoria y CPU
- Si la app es lenta, considera upgrade a plan pago
- La BD en nube debe estar fuera de Replit

✅ **Verificar que todo funciona:**
1. Abre `https://tu-replit-url.repl.co`
2. Intenta hacer login o crear una mascota
3. Revisa los logs si hay errores

---
¿Preguntas? Revisa el archivo `.env.example` para más detalles.
