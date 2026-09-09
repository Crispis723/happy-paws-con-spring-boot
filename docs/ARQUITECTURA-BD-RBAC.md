# Happy Paws — Arquitectura de Base de Datos y RBAC

## Modelo de seguridad

```text
users
  │
  └──< user_roles >── roles
                         │
                         └──< role_permissions >── permissions >── modules
```

### Reglas

- Cada tabla tiene una PK `BIGINT` generada por identidad.
- `user_roles` usa PK compuesta `(user_id, role_id)` y dos FK.
- `role_permissions` usa PK compuesta `(role_id, permission_id)` y dos FK.
- Cada permiso pertenece obligatoriamente a un módulo mediante `permissions.module_id`.
- `permissions.code` es único y es la autoridad que consume Spring Security.
- `permissions.action` está restringida a `VIEW`, `CREATE`, `UPDATE`, `DELETE`, `EXPORT` o `ASSIGN`.
- Las relaciones de dominio usan FK explícitas: mascota→cliente, cita→mascota/cliente/veterinario, historial→mascota/cita, producto→unidad/afectación, compra→proveedor/comprobante, venta→cliente, etc.
- Las tablas puente eliminan sus filas dependientes mediante `ON DELETE CASCADE`.
- Los permisos no se borran al eliminar un módulo accidentalmente: `permissions.module_id` usa `ON DELETE RESTRICT`.

## Autorización

Spring Security carga las autoridades como:

- `ROLE_<ROL>` para el rol.
- `<PERMISSION_CODE>` para cada permiso activo del rol.

Las rutas sensibles se protegen por permisos (`CLIENTES_CREAR`, `MASCOTAS_EDITAR`, `ROLES_ASIGNAR`, etc.), no únicamente por el nombre del rol.

## Operación en producción

Producción usa `JPA_DDL_AUTO=validate`. La estructura se administra mediante SQL versionado:

- `docs/supabase-schema-fixed.sql`: instalación limpia.
- `docs/supabase-migration-fix.sql`: migración no destructiva de una base existente.

No se debe usar `DROP TABLE` sobre una base productiva. El script destructivo solamente debe ejecutarse sobre una base nueva/de pruebas.
