# Migración de Happy Paws al nuevo convenio de IDs

Se adaptó el modelo JPA para la estructura PostgreSQL/Supabase donde las PK y FK usan `id_*`.

## PK Java -> PostgreSQL

| Entidad | Java | PostgreSQL |
|---|---|---|
| User | `idUsuario` | `id_usuario` |
| Role | `idRol` | `id_rol` |
| DocumentoTipo | `idDocumentoTipo` | `id_documento_tipo` |
| ComprobanteTipo | `idComprobanteTipo` | `id_comprobante_tipo` |
| Unidad | `idUnidad` | `id_unidad` |
| AfectacionTipo | `idAfectacionTipo` | `id_afectacion_tipo` |
| Module | `idModulo` | `id_modulo` |
| Permission | `idPermiso` | `id_permiso` |
| Cliente | `idCliente` | `id_cliente` |
| Proveedor | `idProveedor` | `id_proveedor` |
| Pet | `idMascota` | `id_mascota` |
| Appointment | `idCita` | `id_cita` |
| HistorialMascota | `idHistorial` | `id_historial` |
| Producto | `idProducto` | `id_producto` |
| ComprobanteSerie | `idSerie` | `id_serie` |
| Compra | `idCompra` | `id_compra` |
| Venta | `idVenta` | `id_venta` |
| PasswordResetToken | `idToken` | `id_token` |
| SystemSetting | `idConfiguracion` | `id_configuracion` |

## Relaciones adaptadas

Se cambiaron los `@JoinColumn` y `@JoinTable` a `id_usuario`, `id_rol`, `id_permiso`, `id_modulo`, `id_cliente`, `id_proveedor`, `id_mascota`, `id_cita`, `id_veterinario`, `id_unidad` e `id_afectacion_tipo` según corresponda.

Los repositories que navegaban IDs de relaciones fueron ajustados a las nuevas propiedades Java, por ejemplo `findByClienteIdCliente` y `findByMascotaIdMascotaOrderByFechaHoraDesc`.

## Recuperación de contraseña

`PasswordResetToken` ahora usa `idToken` mapeado a `id_token`, y la consulta de invalidación usa `t.user.idUsuario`. Esto elimina la dependencia de `password_reset_tokens.id`.

## Compatibilidad del frontend

Se conservaron métodos `getId()/setId()` de compatibilidad en las entidades para no romper de forma innecesaria formularios Thymeleaf y consumidores que todavía trabajan con `id`. El campo persistente de JPA, sin embargo, es el nuevo `idX` y su `@Column(name = "id_*")`.

## Hibernate

Se mantiene `spring.jpa.hibernate.ddl-auto=validate`. No se cambió a `create`, `create-drop` ni `update`.

También se eliminó la configuración explícita del dialecto PostgreSQL porque Hibernate puede detectarlo automáticamente.

## Migración SQL

Se agregó `docs/supabase-migration-new-ids.sql` para convertir una base existente con las columnas antiguas a los nuevos nombres. No debe ejecutarse si la base ya fue creada con los nombres nuevos.

## Verificación

Se realizaron comprobaciones estáticas sobre entidades, relaciones y repositories. No fue posible ejecutar Maven en este entorno porque el wrapper intentó descargar Maven desde Maven Central y la red del entorno no permitió la descarga. Por tanto, el build completo contra dependencias externas queda pendiente de ejecutarse en un entorno con acceso a Maven Central.
