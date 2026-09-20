# Happy Paws - checklist de despliegue

1. Ejecutar el SQL de Supabase incluido en `docs/supabase-schema-fixed.sql` únicamente si se parte de una base limpia.
2. Configurar en Render las variables de entorno indicadas en `ENV_VARIABLES.md`.
3. El servicio debe escuchar en `${PORT:10000}`.
4. Probar `/`, `/login` y después el dashboard según el rol.
5. Las rutas antiguas de edición/creación redirigen a las rutas canónicas para evitar 404.
6. `/historial` redirige a `/mascotas` porque el historial requiere el ID de una mascota.
7. `/reportes` redirige a `/reportes/financieros`.
