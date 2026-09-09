Guía de reorganización propuesta

Objetivo
- Mejorar la jerarquía de plantillas y assets para facilitar mantenimiento y presentación.

Propuesta de cambios (no destructivos, confirmar antes de mover archivos)
1. Vistas
   - Mantener `templates/views` por módulo.
   - Crear `templates/partials` o `templates/fragments` para header/footer/modals comunes.
   - Consolidar componentes reutilizables en `templates/views/components` (actualmente existen `button-*` y `module-card`).

2. Assets estáticos
   - Crear subcarpetas en `static/assets/img/` por módulo: `clientes/`, `mascotas/`, `productos/`, `usuarios/`, `shared/`.
   - Mover imágenes correspondientes a cada carpeta y dejar un `shared` para logos/avatares.
   - Mantener `static/css`, `static/js`, `static/fonts` como están.

3. Documentación
   - `docs/presentation.html` ya añadido.
   - Añadir `docs/CHANGELOG.md` y `docs/DEPLOY.md` si se desea.

4. Automatización (opcional)
   - Puedo ejecutar un script que:
     - Mueva imágenes a subcarpetas basadas en nombres (ej. `prod-*` → `productos/`).
     - Actualice rutas en templates buscando ocurrencias de `/assets/img/...` y reemplazándolas según el nuevo destino.
   - Nota: reemplazos automáticos pueden necesitar revisión manual si las rutas son construidas dinámicamente.

Siguiente paso sugerido
- Si das OK, aplico la reorganización automática para imágenes y genero un diff con los cambios propuestos para que revises antes de confirmar.
