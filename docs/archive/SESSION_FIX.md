# Corrección de sesiones Happy Paws

## Qué se corrigió
- Las sesiones HTTP ahora se almacenan en PostgreSQL/Supabase mediante Spring Session JDBC.
- Un reinicio/redeploy de Render no debería cerrar las sesiones por perder la memoria local del proceso.
- Se configura cookie JSESSIONID HttpOnly, SameSite=Lax y Secure en Render.
- Se eliminó la dependencia lógica de `sessionStorage`/`window.name` para decidir si una sesión es válida.
- El `loginRole` se infiere si el formulario no lo envía.
- Se eliminó `?tabLogin=1` de las redirecciones de login.

## Base de datos
Al arrancar, Spring Session crea las tablas necesarias para las sesiones (`SPRING_SESSION` y `SPRING_SESSION_ATTRIBUTES`) mediante el esquema JDBC de PostgreSQL.

No borres las tablas de Happy Paws para aplicar esta corrección.
