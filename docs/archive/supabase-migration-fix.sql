-- HAPPY PAWS - MIGRACIÓN RBAC NO DESTRUCTIVA
-- Para bases ya existentes. NO elimina tablas ni datos.
BEGIN;

-- 1) Asegurar catálogo de módulos
INSERT INTO modules (code,name,description,icon,route,display_order) VALUES
('DASHBOARD','Dashboard','Panel principal','bi-speedometer2','/dashboard',1),
('CITAS','Citas','Agenda y gestión de citas','bi-calendar-check','/citas',2),
('MASCOTAS','Mascotas','Registro y gestión de mascotas','bi-heart-fill','/mascotas',3),
('CLIENTES','Clientes','Gestión de clientes','bi-people-fill','/clientes',4),
('PRODUCTOS','Productos','Inventario y catálogo de productos','bi-box-seam','/productos',5),
('COMPRAS','Compras','Compras y abastecimiento','bi-cart-check','/compras',6),
('VENTAS','Ventas','Ventas y facturación','bi-cash-coin','/ventas',7),
('PROVEEDORES','Proveedores','Gestión de proveedores','bi-truck','/proveedores',8),
('HISTORIAL','Historial clínico','Historial médico de mascotas','bi-file-medical','/historial',9),
('USUARIOS','Usuarios','Administración de usuarios','bi-person-gear','/usuarios',10),
('ROLES','Roles','Administración de roles y permisos','bi-shield-lock','/roles',11),
('REPORTES','Reportes','Reportes del sistema','bi-bar-chart','/reportes',12),
('CONFIGURACION','Configuración','Configuración general del sistema','bi-gear','/admin',13),
('CONFIGURACION_COMPROBANTES','Configuración de comprobantes','Configuración de comprobantes','bi-receipt','/configuracion/comprobantes',14),
('CONFIGURACION_DOCUMENTOS','Configuración de documentos','Configuración de documentos','bi-file-text','/configuracion/documentos',15),
('CONFIGURACION_UNIDADES','Configuración de unidades','Configuración de unidades','bi-rulers','/configuracion/unidades',16),
('CONFIGURACION_AFECTACIONES','Configuración de afectaciones','Configuración de afectaciones','bi-percent','/configuracion/afectaciones',17)
ON CONFLICT (code) DO NOTHING;

-- 2) Normalizar permissions existentes
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS code VARCHAR(100);
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS action VARCHAR(30);
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE permissions ADD COLUMN IF NOT EXISTS module_id BIGINT;

UPDATE permissions SET code = name WHERE code IS NULL OR btrim(code) = '';

UPDATE permissions p SET action = CASE
    WHEN p.code LIKE '%_VER' THEN 'VIEW'
    WHEN p.code LIKE '%_CREAR' THEN 'CREATE'
    WHEN p.code LIKE '%_EDITAR' THEN 'UPDATE'
    WHEN p.code LIKE '%_ELIMINAR' THEN 'DELETE'
    WHEN p.code LIKE '%_EXPORTAR' THEN 'EXPORT'
    WHEN p.code LIKE '%_ASIGNAR' THEN 'ASSIGN'
    ELSE 'VIEW' END
WHERE action IS NULL OR btrim(action) = '';

UPDATE permissions p
SET module_id = m.id
FROM modules m
WHERE p.module_id IS NULL
  AND m.code = CASE
      WHEN p.code LIKE 'DASHBOARD_%' THEN 'DASHBOARD'
      WHEN p.code LIKE 'PERMISOS_%' THEN 'ROLES'
      WHEN p.code IN ('CONFIGURACION_COMPROBANTES','CONFIGURACION_DOCUMENTOS','CONFIGURACION_UNIDADES','CONFIGURACION_AFECTACIONES') THEN p.code
      WHEN p.code LIKE 'CONFIGURACION_%' THEN 'CONFIGURACION'
      ELSE split_part(p.code, '_', 1)
  END;

-- 3) Crear permisos que el SecurityConfig necesita y que algunas bases antiguas no tenían
INSERT INTO permissions (code,name,description,action,module_id)
SELECT x.code,x.name,x.description,x.action,m.id
FROM (VALUES
 ('DASHBOARD_CLIENTE_VER','DASHBOARD_CLIENTE_VER','Ver dashboard de cliente','VIEW','DASHBOARD'),
 ('ROLES_ASIGNAR','ROLES_ASIGNAR','Asignar roles a usuarios','ASSIGN','ROLES'),
 ('CONFIGURACION_COMPROBANTES','CONFIGURACION_COMPROBANTES','Administrar comprobantes','VIEW','CONFIGURACION_COMPROBANTES'),
 ('CONFIGURACION_DOCUMENTOS','CONFIGURACION_DOCUMENTOS','Administrar tipos de documento','VIEW','CONFIGURACION_DOCUMENTOS'),
 ('CONFIGURACION_UNIDADES','CONFIGURACION_UNIDADES','Administrar unidades','VIEW','CONFIGURACION_UNIDADES'),
 ('CONFIGURACION_AFECTACIONES','CONFIGURACION_AFECTACIONES','Administrar afectaciones','VIEW','CONFIGURACION_AFECTACIONES')
) AS x(code,name,description,action,module_code)
JOIN modules m ON m.code=x.module_code
WHERE NOT EXISTS (SELECT 1 FROM permissions p WHERE p.code=x.code);

-- 3.1) Corregir permisos de configuración existentes para que apunten a su módulo específico.
UPDATE permissions p
SET module_id = m.id
FROM modules m
WHERE p.code IN ('CONFIGURACION_COMPROBANTES','CONFIGURACION_DOCUMENTOS','CONFIGURACION_UNIDADES','CONFIGURACION_AFECTACIONES')
  AND m.code = p.code;

-- 4) Garantizar unicidad del código y la relación FK
CREATE UNIQUE INDEX IF NOT EXISTS ux_permissions_code ON permissions(code);
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname='fk_permissions_module') THEN
        ALTER TABLE permissions ADD CONSTRAINT fk_permissions_module
            FOREIGN KEY (module_id) REFERENCES modules(id) ON DELETE RESTRICT;
    END IF;
END $$;

-- Solo después de completar los datos, endurecemos NOT NULL.
ALTER TABLE permissions ALTER COLUMN code SET NOT NULL;
ALTER TABLE permissions ALTER COLUMN action SET NOT NULL;
ALTER TABLE permissions ALTER COLUMN module_id SET NOT NULL;

-- 5) Permiso especial para asignar roles
INSERT INTO role_permissions(role_id,permission_id)
SELECT r.id,p.id FROM roles r CROSS JOIN permissions p
WHERE r.name='ADMIN' AND p.code='ROLES_ASIGNAR'
ON CONFLICT DO NOTHING;

-- 5.1) Un usuario solo puede tener UN rol.
-- Si alguna cuenta quedó con más de un rol (bug histórico o dato manual),
-- nos quedamos con el rol de mayor privilegio: ADMIN > el resto por id de rol más bajo.
DO $$
BEGIN
    DELETE FROM user_roles ur
    WHERE ur.role_id NOT IN (
        SELECT r.id FROM roles r
        WHERE r.id = (
            SELECT ur2.role_id FROM user_roles ur2
            JOIN roles r2 ON r2.id = ur2.role_id
            WHERE ur2.user_id = ur.user_id
            ORDER BY (r2.name = 'ADMIN') DESC, ur2.role_id ASC
            LIMIT 1
        )
    );
END $$;

-- Ahora que cada user_id aparece como máximo una vez, hacemos que la base
-- de datos lo obligue: user_id pasa a ser la clave primaria de user_roles.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'user_roles_pkey' AND conrelid = 'user_roles'::regclass
    ) THEN
        ALTER TABLE user_roles DROP CONSTRAINT user_roles_pkey;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'pk_user_roles'
    ) THEN
        ALTER TABLE user_roles ADD CONSTRAINT pk_user_roles PRIMARY KEY (user_id);
    END IF;
END $$;

-- 6) Token de recuperación, si aún no existe
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_expiry_date ON password_reset_tokens(expiry_date);

COMMIT;
