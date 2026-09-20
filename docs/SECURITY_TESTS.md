# Pruebas de autorización

Se agregó `SecurityAuthorizationTest` como prueba de regresión para las rutas de compatibilidad.

Casos cubiertos:

- Un usuario anónimo no accede a rutas protegidas.
- `CLIENTES_VER` no permite editar ni eliminar clientes.
- `CLIENTES_EDITAR` sí permite usar la ruta legacy de edición.
- `MASCOTAS_VER` no permite editar mascotas.
- `MASCOTAS_EDITAR` sí permite usar la ruta legacy de edición.
- `VENTAS_VER` no permite editar ventas.
- `VENTAS_EDITAR` sí permite usar la ruta legacy de edición.
- `PRODUCTOS_VER` no permite eliminar productos.
- `ROLE_ADMIN` conserva acceso a rutas protegidas.

La finalidad es evitar regresiones donde una URL legacy caiga por error en un matcher amplio de tipo `*_VER`.

## Ejecutar

```bash
./mvnw test
```

En Windows:

```bat
mvnw.cmd test
```
