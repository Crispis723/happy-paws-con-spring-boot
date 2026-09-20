# Corrección Roles + sesión por pestaña

## Roles
- ADMIN puede acceder al módulo de Roles aunque no tenga cada permiso explícito.
- Se corrigió `RoleView` para incluir `color`, que la vista `roles/index.html` utilizaba pero el record no exponía.
- Las operaciones de crear/editar/eliminar también aceptan `ROLE_ADMIN`.
- La asignación de roles mantiene su autorización.
- La carga ordenada de permisos tiene fallback a `findAll()` si una consulta ordenada falla por datos antiguos.

## Sesión
- La cookie JSESSIONID se mantiene como cookie de sesión (`max-age=-1`).
- Después de enviar el formulario de login se marca la pestaña actual mediante `sessionStorage`.
- Las páginas autenticadas verifican esa marca inmediatamente.
- Si se copia una URL autenticada y se abre en una pestaña nueva, esa pestaña no tiene la marca; se invalida la sesión HTTP heredada y se redirige al login.
- Recargar y navegar dentro de la misma pestaña conserva la marca y no cierra la sesión.
- No se depende de `beforeunload`/`pagehide`, porque esos eventos no son fiables al cerrar pestañas.

Limitación técnica: HTTP/JSESSIONID es compartido por las pestañas del mismo navegador; JavaScript no puede borrar una cookie de sesión únicamente para una pestaña. La protección por `sessionStorage` impide reutilizar la sesión desde una pestaña nueva y fuerza el login nuevamente.
