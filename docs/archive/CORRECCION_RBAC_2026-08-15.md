# Corrección RBAC / Render

## Problemas corregidos

1. `permissions.module_id` podía quedar `NULL` durante la sincronización de permisos del `DataInitializer`.
2. La asignación de roles acepta `/roles/asignar` y `/roles/asignar/` para evitar problemas de trailing slash que podían terminar en HTTP 405.
3. Se mantienen los permisos de configuración asociados explícitamente a sus módulos:
   - CONFIGURACION_COMPROBANTES
   - CONFIGURACION_DOCUMENTOS
   - CONFIGURACION_UNIDADES
   - CONFIGURACION_AFECTACIONES
4. La sincronización usa `saveAndFlush` para que el vínculo obligatorio `Permission -> Module` se escriba inmediatamente y permita detectar el problema en el arranque.
5. Se corrigió la sintaxis de fragmentos Thymeleaf de la vista 403.

## Render

El proyecto continúa usando el puerto proporcionado por Render mediante `PORT` y el endpoint de despliegue existente.

## Importante

La base de datos debe conservar `permissions.module_id` como NOT NULL. No se recomienda eliminar esa restricción para ocultar el problema; el inicializador ahora repara los permisos existentes que tengan `module_id` nulo.
