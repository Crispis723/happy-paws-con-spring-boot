# Scripts SQL archivados

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
