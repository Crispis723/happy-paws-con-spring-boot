package com.Happypaws.demo.security;

import com.Happypaws.demo.config.SecurityConfig;
import com.Happypaws.demo.controller.CompatibilityRoutesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regression tests for route-level authorization.
 *
 * These tests focus on compatibility URLs because they previously had a risk
 * of falling through to broad *_VER matchers. The canonical and legacy routes
 * must require the same effective permission.
 */
@WebMvcTest(controllers = CompatibilityRoutesController.class)
@Import(SecurityConfig.class)
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousUserIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/clientes/editar/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(authorities = "CLIENTES_VER")
    void clientViewPermissionCannotEditAnotherClientViaLegacyRoute() throws Exception {
        mockMvc.perform(get("/clientes/editar/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CLIENTES_EDITAR")
    void clientEditPermissionCanUseLegacyEditRoute() throws Exception {
        mockMvc.perform(get("/clientes/editar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/edit/1"));
    }

    @Test
    @WithMockUser(authorities = "CLIENTES_VER")
    void clientViewPermissionCannotDeleteViaLegacyRoute() throws Exception {
        mockMvc.perform(get("/clientes/eliminar/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "MASCOTAS_VER")
    void petViewPermissionCannotEditViaLegacyRoute() throws Exception {
        mockMvc.perform(get("/mascotas/editar/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "MASCOTAS_EDITAR")
    void petEditPermissionCanUseLegacyEditRoute() throws Exception {
        mockMvc.perform(get("/mascotas/editar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mascotas/edit/1"));
    }

    @Test
    @WithMockUser(authorities = "VENTAS_VER")
    void salesViewPermissionCannotEditViaLegacyRoute() throws Exception {
        mockMvc.perform(get("/ventas/editar/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "VENTAS_EDITAR")
    void salesEditPermissionCanUseLegacyEditRoute() throws Exception {
        mockMvc.perform(get("/ventas/editar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ventas/edit/1"));
    }

    @Test
    @WithMockUser(authorities = "PRODUCTOS_VER")
    void productViewPermissionCannotDeleteViaLegacyRoute() throws Exception {
        mockMvc.perform(get("/productos/eliminar/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void adminCanUseProtectedLegacyRoutes() throws Exception {
        mockMvc.perform(get("/clientes/editar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/edit/1"));
    }
}
