# Archivo histórico

## Scripts SQL archivados

Estos scripts son versiones anteriores del esquema de base de datos que
**ya no coinciden** con las entidades JPA actuales del proyecto (usan
columnas genéricas como `id`, `unidad_id`, `producto_id`, etc. en vez de
`id_producto`, `id_unidad`, etc.). Se conservan solo como referencia
histórica de la evolución del esquema.

**No los ejecutes contra una base de datos en uso.**

La única fuente de verdad del esquema es:

```
docs/supabase-schema-corregido.sql
```

Ese script sí está verificado columna por columna contra cada
`@Column`/`@JoinColumn`/`@JoinTable` de `src/main/java/com/Happypaws/demo/model`.

## Notas de correcciones archivadas

Los siguientes `.md` documentan correcciones puntuales ya aplicadas al
código (bugs de roles, sesiones, rutas 404/405, convención de IDs, la
implementación inicial de validaciones y correo). Se conservan como
bitácora histórica, pero **no reflejan necesariamente el estado actual**
del proyecto — para eso está el `README.md` de la raíz y `docs/PROJECT_FIXES.md`.

- `CORRECCION_RBAC_2026-08-15.md`
- `IMPLEMENTATION_SUMMARY.md`
- `NEW_ID_CONVENTION_FIX.md`
- `QUICK_START.md`
- `RENDER_DEPLOYMENT_FIX.md`
- `REPARACION_404_405_2026-08-15.md`
- `ROLES_AND_TAB_SESSION_FIX_2026-08-15.md`
- `ROLES_FIX_NOTES.md`
- `SESSION_FIX.md`
