package com.Happypaws.demo.config;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Happypaws.demo.model.AfectacionTipo;
import com.Happypaws.demo.model.DocumentoTipo;
import com.Happypaws.demo.model.Permission;
import com.Happypaws.demo.model.Role;
import com.Happypaws.demo.model.SystemSetting;
import com.Happypaws.demo.model.Unidad;
import com.Happypaws.demo.model.User;

import com.Happypaws.demo.repository.AfectacionTipoRepository;
import com.Happypaws.demo.repository.DocumentoTipoRepository;
import com.Happypaws.demo.repository.PermissionRepository;
import com.Happypaws.demo.repository.RoleRepository;
import com.Happypaws.demo.repository.SystemSettingRepository;
import com.Happypaws.demo.repository.UnidadRepository;
import com.Happypaws.demo.repository.UserRepository;

@Configuration
public class DataInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedData(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            com.Happypaws.demo.repository.ModuleRepository moduleRepository,
            UserRepository userRepository,
            UnidadRepository unidadRepository,
            AfectacionTipoRepository afectacionTipoRepository,
            DocumentoTipoRepository documentoTipoRepository,
            SystemSettingRepository systemSettingRepository,
            PasswordEncoder passwordEncoder,

            @Value("${ADMIN_INITIAL_EMAIL:admin@happypaws.com}")
            String adminInitialEmail,

            @Value("${ADMIN_INITIAL_PASSWORD:}")
            String adminInitialPassword,

            @Value("${SEED_DEMO_USERS:false}")
            boolean seedDemoUsers,

            @Value("${DEMO_USERS_PASSWORD:}")
            String demoUsersPassword) {

        return args -> {

            // =====================================================
            // PERMISOS
            // =====================================================

            Map<String, String> permissions = Map.ofEntries(

                    Map.entry("DASHBOARD_VER",
                            "Ver el dashboard"),
                    Map.entry("DASHBOARD_CLIENTE_VER",
                            "Ver dashboard de cliente"),

                    // Citas
                    Map.entry("CITAS_VER",
                            "Ver citas"),
                    Map.entry("CITAS_CREAR",
                            "Crear citas"),
                    Map.entry("CITAS_EDITAR",
                            "Editar citas"),
                    Map.entry("CITAS_ELIMINAR",
                            "Eliminar citas"),

                    // Mascotas
                    Map.entry("MASCOTAS_VER",
                            "Ver mascotas"),
                    Map.entry("MASCOTAS_CREAR",
                            "Crear mascotas"),
                    Map.entry("MASCOTAS_EDITAR",
                            "Editar mascotas"),
                    Map.entry("MASCOTAS_ELIMINAR",
                            "Eliminar mascotas"),

                    // Clientes
                    Map.entry("CLIENTES_VER",
                            "Ver clientes"),
                    Map.entry("CLIENTES_CREAR",
                            "Crear clientes"),
                    Map.entry("CLIENTES_EDITAR",
                            "Editar clientes"),
                    Map.entry("CLIENTES_ELIMINAR",
                            "Eliminar clientes"),

                    // Productos
                    Map.entry("PRODUCTOS_VER",
                            "Ver productos"),
                    Map.entry("PRODUCTOS_CREAR",
                            "Crear productos"),
                    Map.entry("PRODUCTOS_EDITAR",
                            "Editar productos"),
                    Map.entry("PRODUCTOS_ELIMINAR",
                            "Eliminar productos"),

                    // Compras
                    Map.entry("COMPRAS_VER",
                            "Ver compras"),
                    Map.entry("COMPRAS_CREAR",
                            "Crear compras"),
                    Map.entry("COMPRAS_EDITAR",
                            "Editar compras"),
                    Map.entry("COMPRAS_ELIMINAR",
                            "Eliminar compras"),

                    // Ventas
                    Map.entry("VENTAS_VER",
                            "Ver ventas"),
                    Map.entry("VENTAS_CREAR",
                            "Crear ventas"),
                    Map.entry("VENTAS_EDITAR",
                            "Editar ventas"),
                    Map.entry("VENTAS_ELIMINAR",
                            "Eliminar ventas"),

                    // Proveedores
                    Map.entry("PROVEEDORES_VER",
                            "Ver proveedores"),
                    Map.entry("PROVEEDORES_CREAR",
                            "Crear proveedores"),
                    Map.entry("PROVEEDORES_EDITAR",
                            "Editar proveedores"),
                    Map.entry("PROVEEDORES_ELIMINAR",
                            "Eliminar proveedores"),

                    // Historial
                    Map.entry("HISTORIAL_VER",
                            "Ver historial clínico"),
                    Map.entry("HISTORIAL_CREAR",
                            "Crear historial clínico"),
                    Map.entry("HISTORIAL_EDITAR",
                            "Editar historial clínico"),
                    Map.entry("HISTORIAL_ELIMINAR",
                            "Eliminar historial clínico"),

                    // Reportes
                    Map.entry("REPORTES_VER",
                            "Ver reportes"),
                    Map.entry("REPORTES_EXPORTAR",
                            "Exportar reportes"),

                    // Usuarios
                    Map.entry("USUARIOS_VER",
                            "Ver usuarios"),
                    Map.entry("USUARIOS_CREAR",
                            "Crear usuarios"),
                    Map.entry("USUARIOS_EDITAR",
                            "Editar usuarios"),
                    Map.entry("USUARIOS_ELIMINAR",
                            "Eliminar usuarios"),

                    // Roles
                    Map.entry("ROLES_VER",
                            "Ver roles"),
                    Map.entry("ROLES_CREAR",
                            "Crear roles"),
                    Map.entry("ROLES_EDITAR",
                            "Editar roles"),
                    Map.entry("ROLES_ELIMINAR",
                            "Eliminar roles"),

                    // Permisos
                    Map.entry("PERMISOS_VER",
                            "Ver permisos"),
                    Map.entry("PERMISOS_ASIGNAR",
                            "Asignar permisos"),
                    Map.entry("ROLES_ASIGNAR",
                            "Asignar roles a usuarios"),

                    Map.entry("CONFIGURACION_COMPROBANTES",
                            "Administrar comprobantes"),
                    Map.entry("CONFIGURACION_DOCUMENTOS",
                            "Administrar tipos de documento"),
                    Map.entry("CONFIGURACION_UNIDADES",
                            "Administrar unidades"),
                    Map.entry("CONFIGURACION_AFECTACIONES",
                            "Administrar afectaciones")
            );

            // =====================================================
            // MÓDULOS + PERMISOS (RBAC normalizado)
            // =====================================================
            Map<String, String[]> moduleCatalog = Map.ofEntries(
                Map.entry("DASHBOARD", new String[]{"Dashboard","/dashboard","bi-speedometer2"}),
                Map.entry("CITAS", new String[]{"Citas","/citas","bi-calendar-check"}),
                Map.entry("MASCOTAS", new String[]{"Mascotas","/mascotas","bi-heart-fill"}),
                Map.entry("CLIENTES", new String[]{"Clientes","/clientes","bi-people-fill"}),
                Map.entry("PRODUCTOS", new String[]{"Productos","/productos","bi-box-seam"}),
                Map.entry("COMPRAS", new String[]{"Compras","/compras","bi-cart-check"}),
                Map.entry("VENTAS", new String[]{"Ventas","/ventas","bi-cash-coin"}),
                Map.entry("PROVEEDORES", new String[]{"Proveedores","/proveedores","bi-truck"}),
                Map.entry("HISTORIAL", new String[]{"Historial clínico","/historial","bi-file-medical"}),
                Map.entry("REPORTES", new String[]{"Reportes","/reportes","bi-bar-chart"}),
                Map.entry("USUARIOS", new String[]{"Usuarios","/usuarios","bi-person-gear"}),
                Map.entry("ROLES", new String[]{"Roles","/roles","bi-shield-lock"}),
                Map.entry("PERMISOS", new String[]{"Permisos","/roles","bi-key"}),
                Map.entry("CONFIGURACION_COMPROBANTES", new String[]{"Configuración de comprobantes","/configuracion/comprobantes","bi-receipt"}),
                Map.entry("CONFIGURACION_DOCUMENTOS", new String[]{"Configuración de documentos","/configuracion/documentos","bi-file-text"}),
                Map.entry("CONFIGURACION_UNIDADES", new String[]{"Configuración de unidades","/configuracion/unidades","bi-rulers"}),
                Map.entry("CONFIGURACION_AFECTACIONES", new String[]{"Configuración de afectaciones","/configuracion/afectaciones","bi-percent"})
            );
            Map<String, com.Happypaws.demo.model.Module> modules = new java.util.LinkedHashMap<>();
            int order=1;
            for (var e : moduleCatalog.entrySet()) {
                final int currentOrder = order;
                var m=moduleRepository.findByCode(e.getKey()).orElseGet(() -> moduleRepository.save(
                    new com.Happypaws.demo.model.Module(e.getKey(), e.getValue()[0], "Módulo "+e.getValue()[0], e.getValue()[2], e.getValue()[1], currentOrder)));
                modules.put(e.getKey(), m); order++;
            }
            for (var entry : permissions.entrySet()) {
                String code = entry.getKey();

                // Por defecto el prefijo del permiso identifica el módulo.
                String moduleCode = code.contains("_")
                        ? code.substring(0, code.indexOf('_'))
                        : "DASHBOARD";

                // PERMISOS y ROLES_ASIGNAR pertenecen al módulo ROLES.
                if (code.equals("PERMISOS_VER") || code.equals("PERMISOS_ASIGNAR")
                        || code.equals("ROLES_ASIGNAR")) {
                    moduleCode = "ROLES";
                }

                // Los permisos de configuración tienen nombres completos y no
                // deben resolverse por el primer fragmento ("CONFIGURACION").
                if (code.equals("CONFIGURACION_COMPROBANTES")
                        || code.equals("CONFIGURACION_DOCUMENTOS")
                        || code.equals("CONFIGURACION_UNIDADES")
                        || code.equals("CONFIGURACION_AFECTACIONES")) {
                    moduleCode = code;
                }

                String action = code.equals("DASHBOARD_VER") || code.endsWith("_VER")
                        ? "VIEW"
                        : code.endsWith("_CREAR")
                            ? "CREATE"
                            : code.endsWith("_EDITAR")
                                ? "UPDATE"
                                : code.endsWith("_ELIMINAR")
                                    ? "DELETE"
                                    : code.endsWith("_EXPORTAR")
                                        ? "EXPORT"
                                        : code.endsWith("_ASIGNAR")
                                            ? "ASSIGN"
                                            : "VIEW";

                var module = modules.get(moduleCode);
                if (module == null) {
                    throw new IllegalStateException(
                            "No existe módulo RBAC para el permiso: " + code
                                    + " (moduleCode=" + moduleCode + ")");
                }

                Permission p = permissionRepository.findByCode(code)
                        .orElseGet(Permission::new);

                // MUY IMPORTANTE: siempre reasignamos la relación obligatoria.
                // Esto corrige registros antiguos que quedaron con id_modulo NULL.
                p.setCode(code);
                p.setName(code);
                p.setDescription(entry.getValue());
                p.setAction(action);
                p.setModule(module);
                p.setEnabled(true);

                // Persistir siempre con un módulo válido. Esto también repara
                // permisos existentes que quedaron con id_modulo = NULL en PostgreSQL.
                if (module.getIdModulo() == null) {
                    module = moduleRepository.saveAndFlush(module);
                    modules.put(moduleCode, module);
                }

                permissionRepository.saveAndFlush(p);
            }

            // =====================================================
            // ROLES
            // =====================================================

            List<String> roleNames = List.of(
                    "ADMIN",
                    "VETERINARIO",
                    "RECEPCIONISTA",
                    "CLIENTE"
            );

            for (String roleName : roleNames) {

                roleRepository.findByName(roleName)
                        .orElseGet(() ->
                                roleRepository.save(
                                        new Role(null, roleName)
                                )
                        );
            }

            // =====================================================
            // ASIGNAR PERMISOS A ROLES
            // =====================================================

            Role adminRole =
                    roleRepository.findByName("ADMIN").orElseThrow();

            Role veterinarioRole =
                    roleRepository.findByName("VETERINARIO").orElseThrow();

            Role recepcionistaRole =
                    roleRepository.findByName("RECEPCIONISTA").orElseThrow();

            Role clienteRole =
                    roleRepository.findByName("CLIENTE").orElseThrow();

            /*
             * ADMIN
             *
             * Tiene todos los permisos.
             */

            adminRole.getPermissions().clear();

            adminRole.getPermissions().addAll(
                    permissionRepository.findAll()
            );

            roleRepository.save(adminRole);


            /*
             * VETERINARIO
             */

            veterinarioRole.getPermissions().clear();

            addPermissions(
                    veterinarioRole,
                    permissionRepository,

                    "DASHBOARD_VER",

                    "CITAS_VER",
                    "CITAS_CREAR",
                    "CITAS_EDITAR",

                    "MASCOTAS_VER",
                    "MASCOTAS_CREAR",
                    "MASCOTAS_EDITAR",

                    "CLIENTES_VER",

                    "VENTAS_VER",
                    "VENTAS_CREAR",

                    "HISTORIAL_VER",
                    "HISTORIAL_CREAR",
                    "HISTORIAL_EDITAR",

                    "REPORTES_VER",
                    "REPORTES_EXPORTAR"
            );

            roleRepository.save(veterinarioRole);


            /*
             * RECEPCIONISTA
             */

            recepcionistaRole.getPermissions().clear();

            addPermissions(
                    recepcionistaRole,
                    permissionRepository,

                    "DASHBOARD_VER",

                    "CITAS_VER",
                    "CITAS_CREAR",
                    "CITAS_EDITAR",
                    "CITAS_ELIMINAR",

                    "MASCOTAS_VER",
                    "MASCOTAS_CREAR",
                    "MASCOTAS_EDITAR",

                    "CLIENTES_VER",
                    "CLIENTES_CREAR",
                    "CLIENTES_EDITAR",

                    "PRODUCTOS_VER",
                    "PRODUCTOS_CREAR",
                    "PRODUCTOS_EDITAR",

                    "COMPRAS_VER",
                    "COMPRAS_CREAR",
                    "COMPRAS_EDITAR",

                    "PROVEEDORES_VER",
                    "PROVEEDORES_CREAR",
                    "PROVEEDORES_EDITAR",

                    "VENTAS_VER",
                    "VENTAS_CREAR",
                    "VENTAS_EDITAR"
            );

            roleRepository.save(recepcionistaRole);


            /*
             * CLIENTE
             */

            clienteRole.getPermissions().clear();

            addPermissions(
                    clienteRole,
                    permissionRepository,

                    "DASHBOARD_CLIENTE_VER",

                    "CITAS_VER",
                    "CITAS_CREAR",

                    "MASCOTAS_VER",
                    "MASCOTAS_CREAR",

                    "CLIENTES_VER",

                    "HISTORIAL_VER",

                    "VENTAS_VER",
                    "VENTAS_CREAR"
            );

            roleRepository.save(clienteRole);


            // =====================================================
            // RESTO DE DATOS INICIALES
            // =====================================================

            if (unidadRepository.count() == 0) {

                unidadRepository.save(
                        new Unidad(null, "NIU", "Unidad")
                );

                unidadRepository.save(
                        new Unidad(null, "PZA", "Pieza")
                );
            }

            if (afectacionTipoRepository.count() == 0) {

                afectacionTipoRepository.save(
                        new AfectacionTipo(
                                null,
                                "10",
                                "Gravado",
                                "Gravado - Operacion onerosa",
                                "G",
                                new BigDecimal("18.00")
                        )
                );

                afectacionTipoRepository.save(
                        new AfectacionTipo(
                                null,
                                "20",
                                "Exonerado",
                                "Exonerado - Operacion no onerosa",
                                "E",
                                new BigDecimal("0.00")
                        )
                );
            }

            if (documentoTipoRepository.count() == 0) {

                documentoTipoRepository.save(
                        new DocumentoTipo(
                                null,
                                "CC",
                                "Cedula de ciudadania"
                        )
                );

                documentoTipoRepository.save(
                        new DocumentoTipo(
                                null,
                                "NIT",
                                "Numero de identificacion tributaria"
                        )
                );
            }

            if (systemSettingRepository
                    .findBySettingKey("cita.precio")
                    .isEmpty()) {

                systemSettingRepository.save(
                        new SystemSetting(
                                null,
                                "cita.precio",
                                "50.00"
                        )
                );
            }

            // =====================================================
            // USUARIOS INICIALES
            // =====================================================

            createInitialUsers(
                    roleRepository,
                    userRepository,
                    passwordEncoder,
                    adminInitialEmail,
                    adminInitialPassword,
                    seedDemoUsers,
                    demoUsersPassword
            );
        };
    }


    // =============================================================
    // MÉTODO PARA ASIGNAR PERMISOS
    // =============================================================

    private void addPermissions(
            Role role,
            PermissionRepository permissionRepository,
            String... permissionNames) {

        for (String permissionName : permissionNames) {

            permissionRepository
                    .findByName(permissionName)
                    .ifPresent(role.getPermissions()::add);
        }
    }


    // =============================================================
    // USUARIOS INICIALES
    // =============================================================

    private void createInitialUsers(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String adminInitialEmail,
            String adminInitialPassword,
            boolean seedDemoUsers,
            String demoUsersPassword) {

        Role adminRole =
                roleRepository.findByName("ADMIN").orElseThrow();


        if (adminInitialPassword != null
                && !adminInitialPassword.isBlank()) {

            userRepository.findByEmail(adminInitialEmail)
                    .ifPresentOrElse(existing -> {

                        if (existing.getRoles() == null) {
                            existing.setRoles(
                                    new LinkedHashSet<>()
                            );
                        }

                        if (existing.getRoles()
                                .stream()
                                .noneMatch(
                                        role -> "ADMIN"
                                                .equals(role.getName())
                                )) {

                            existing.getRoles().add(adminRole);

                            userRepository.save(existing);

                            log.info(
                                    "Se asigno el rol ADMIN al usuario existente: {}",
                                    adminInitialEmail
                            );
                        }

                    }, () -> {

                        userRepository.save(
                                createUser(
                                        passwordEncoder,
                                        "Administrador",
                                        adminInitialEmail,
                                        adminInitialPassword,
                                        adminRole
                                )
                        );

                        log.info(
                                "Usuario administrador inicial creado: {}",
                                adminInitialEmail
                        );
                    });
        }


        if (seedDemoUsers
                && demoUsersPassword != null
                && !demoUsersPassword.isBlank()) {

            Role recepcionistaRole =
                    roleRepository.findByName("RECEPCIONISTA")
                            .orElseThrow();

            Role veterinarioRole =
                    roleRepository.findByName("VETERINARIO")
                            .orElseThrow();

            Role clienteRole =
                    roleRepository.findByName("CLIENTE")
                            .orElseThrow();


            if (userRepository
                    .findByEmail("admin@happypaws.com")
                    .isEmpty()) {

                userRepository.save(
                        createUser(
                                passwordEncoder,
                                "Administrador",
                                "admin@happypaws.com",
                                demoUsersPassword,
                                adminRole
                        )
                );
            }


            if (userRepository
                    .findByEmail("recepcion@happypaws.com")
                    .isEmpty()) {

                userRepository.save(
                        createUser(
                                passwordEncoder,
                                "Recepcion",
                                "recepcion@happypaws.com",
                                demoUsersPassword,
                                recepcionistaRole
                        )
                );
            }


            if (userRepository
                    .findByEmail("vet@happypaws.com")
                    .isEmpty()) {

                userRepository.save(
                        createUser(
                                passwordEncoder,
                                "Veterinario",
                                "vet@happypaws.com",
                                demoUsersPassword,
                                veterinarioRole
                        )
                );
            }


            if (userRepository
                    .findByEmail("cliente@happypaws.com")
                    .isEmpty()) {

                userRepository.save(
                        createUser(
                                passwordEncoder,
                                "Cliente Demo",
                                "cliente@happypaws.com",
                                demoUsersPassword,
                                clienteRole
                        )
                );
            }

            log.warn(
                    "Usuarios demo creados o actualizados en modo local. No uses esto en produccion."
            );

            return;
        }


        if (userRepository
                .findByEmail(adminInitialEmail)
                .isEmpty()) {

            log.warn(
                    "No se crearon usuarios iniciales. Define ADMIN_INITIAL_PASSWORD para crear el primer administrador."
            );
        }
    }


    // =============================================================
    // CREAR USUARIO
    // =============================================================

    private User createUser(
            PasswordEncoder passwordEncoder,
            String name,
            String email,
            String password,
            Role role) {

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setEnabled(true);

        user.setRoles(
                new LinkedHashSet<>(
                        List.of(role)
                )
        );

        return user;
    }
}
