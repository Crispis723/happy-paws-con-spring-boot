package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.PetService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PetController {

    private final PetService petService;
    private final ClienteService clienteService;

    public PetController(PetService petService, ClienteService clienteService) {
        this.petService = petService;
        this.clienteService = clienteService;
    }

    // =========================================================
    // LISTAR MASCOTAS
    // =========================================================
    @GetMapping("/mascotas")
    public String listar(Model model, Authentication auth) {

        boolean isClientUser = esCliente(auth);
        boolean canManageAllPets = puedeGestionarTodasLasMascotas(auth);

        /*
         * Si es veterinario, aunque también tenga ROLE_CLIENTE,
         * puede ver TODAS las mascotas.
         *
         * Si solamente es CLIENTE, ve únicamente sus mascotas.
         */
        if (canManageAllPets) {

            model.addAttribute("mascotas", petService.listar());
            model.addAttribute("isClientUser", false);
            model.addAttribute("canManageAllPets", true);

        } else if (isClientUser) {

            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(
                    auth.getName(),
                    auth.getName()
            );

            model.addAttribute(
                    "mascotas",
                    petService.listarPorClienteId(cliente.getIdCliente())
            );

            model.addAttribute("isClientUser", true);
            model.addAttribute("canManageAllPets", false);
            model.addAttribute(
                    "clienteNombre",
                    cliente.getRazonSocial()
            );
            model.addAttribute(
                    "clienteId",
                    cliente.getIdCliente()
            );

        } else {

            /*
             * Si no es cliente, pero tiene un rol administrativo,
             * puede ver todas las mascotas.
             */
            model.addAttribute("mascotas", petService.listar());
            model.addAttribute("isClientUser", false);
            model.addAttribute("canManageAllPets", true);
        }

        return "views/mascotas/index";
    }

    // =========================================================
    // NUEVA MASCOTA
    // =========================================================
    @GetMapping("/mascotas/nuevo")
    public String nuevo(Model model, Authentication auth) {

        Pet mascota = new Pet();

        model.addAttribute("mascota", mascota);

        aplicarContextoCliente(model, mascota, auth);

        return "views/mascotas/formulario";
    }

    // =========================================================
    // GUARDAR / ACTUALIZAR MASCOTA
    // =========================================================
    @PostMapping("/mascotas/guardar")
    public String guardar(
            @Valid @ModelAttribute("mascota") Pet mascota,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            Authentication auth) {

        boolean isClientUser = esCliente(auth);
        boolean canManageAllPets = puedeGestionarTodasLasMascotas(auth);

        /*
         * Un usuario que solamente es CLIENTE no puede elegir
         * otro cliente. La mascota se asigna automáticamente
         * a su cuenta.
         *
         * IMPORTANTE:
         * Si es CLIENTE + VETERINARIO, no entra aquí porque
         * canManageAllPets será true.
         */
        if (isClientUser && !canManageAllPets) {

            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(
                    auth.getName(),
                    auth.getName()
            );

            mascota.setCliente(cliente);
        }

        // =====================================================
        // VALIDACIONES
        // =====================================================
        if (bindingResult.hasErrors()) {

            aplicarContextoCliente(model, mascota, auth);

            return "views/mascotas/formulario";
        }

        // =====================================================
        // VALIDAR CLIENTE
        // =====================================================
        if (mascota.getCliente() != null
                && mascota.getCliente().getIdCliente() != null) {

            Cliente cliente = clienteService
                    .buscarPorId(mascota.getCliente().getIdCliente())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Cliente no encontrado"
                            )
                    );

            mascota.setCliente(cliente);
        }

        // =====================================================
        // GUARDAR O ACTUALIZAR
        // =====================================================
        if (mascota.getIdMascota() == null) {

            petService.guardar(mascota);

        } else {

            /*
             * Veterinarios y administradores pueden actualizar
             * cualquier mascota para modificar su información
             * e historial.
             */
            petService.actualizar(mascota);
        }

        redirectAttributes.addFlashAttribute(
                "success",
                "Mascota guardada correctamente"
        );

        return "redirect:/mascotas";
    }

    // =========================================================
    // EDITAR MASCOTA
    // =========================================================
    @GetMapping("/mascotas/edit/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            Authentication auth) {

        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Mascota no encontrada"
                        )
                );

        /*
         * CLIENTE solamente:
         * solo puede modificar sus mascotas.
         *
         * VETERINARIO:
         * puede modificar cualquier mascota.
         *
         * CLIENTE + VETERINARIO:
         * puede modificar cualquier mascota.
         */
        validarPermisoMascota(mascota, auth);

        if (mascota.getCliente() == null) {
            mascota.setCliente(new Cliente());
        }

        model.addAttribute("mascota", mascota);

        aplicarContextoCliente(model, mascota, auth);

        return "views/mascotas/formulario";
    }

    // =========================================================
    // ELIMINAR MASCOTA
    // =========================================================
    @GetMapping("/mascotas/delete/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Authentication auth) {

        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Mascota no encontrada"
                        )
                );

        validarPermisoMascota(mascota, auth);

        petService.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Mascota eliminada correctamente"
        );

        return "redirect:/mascotas";
    }

    // =========================================================
    // VER MASCOTA / HISTORIAL
    // =========================================================
    @GetMapping("/mascotas/{id}")
    public String ver(
            @PathVariable Long id,
            Model model,
            Authentication auth) {

        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Mascota no encontrada"
                        )
                );

        /*
         * El permiso se valida aquí.
         *
         * CLIENTE:
         * solamente puede ver su propia mascota.
         *
         * VETERINARIO:
         * puede ver cualquier mascota.
         *
         * CLIENTE + VETERINARIO:
         * puede ver cualquier mascota.
         */
        validarPermisoMascota(mascota, auth);

        model.addAttribute("mascota", mascota);

        return "views/mascotas/show";
    }

    // =========================================================
    // CONTEXTO DEL CLIENTE
    // =========================================================
    private void aplicarContextoCliente(
            Model model,
            Pet mascota,
            Authentication auth) {

        boolean isClientUser = esCliente(auth);
        boolean canManageAllPets = puedeGestionarTodasLasMascotas(auth);

        /*
         * CLIENTE PURO
         *
         * No puede seleccionar otro cliente.
         */
        if (isClientUser && !canManageAllPets) {

            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(
                    auth.getName(),
                    auth.getName()
            );

            mascota.setCliente(cliente);

            model.addAttribute("isClientUser", true);
            model.addAttribute("canManageAllPets", false);

            model.addAttribute(
                    "clienteNombre",
                    cliente.getRazonSocial()
            );

            model.addAttribute(
                    "clienteId",
                    cliente.getIdCliente()
            );

            model.addAttribute("clientes", null);

        } else {

            /*
             * VETERINARIO
             * ADMIN
             * u otros roles administrativos
             *
             * Pueden seleccionar el cliente de la mascota.
             */
            model.addAttribute("isClientUser", false);
            model.addAttribute("canManageAllPets", true);

            model.addAttribute(
                    "clientes",
                    clienteService.listar()
            );
        }
    }

    // =========================================================
    // VALIDAR PERMISO SOBRE UNA MASCOTA
    // =========================================================
    private void validarPermisoMascota(
            Pet mascota,
            Authentication auth) {

        /*
         * VETERINARIO / ADMIN / PERSONAL AUTORIZADO
         *
         * Pueden trabajar con cualquier mascota.
         */
        if (puedeGestionarTodasLasMascotas(auth)) {
            return;
        }

        /*
         * CLIENTE
         *
         * Solamente puede trabajar con sus propias mascotas.
         */
        if (esCliente(auth)) {

            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(
                    auth.getName(),
                    auth.getName()
            );

            if (mascota.getCliente() == null
                    || mascota.getCliente().getIdCliente() == null
                    || cliente.getIdCliente() == null
                    || !cliente.getIdCliente().equals(
                            mascota.getCliente().getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para ver o modificar esta mascota"
                );
            }

            return;
        }

        /*
         * Si no tiene ninguno de los roles esperados,
         * no puede acceder.
         */
        throw new IllegalArgumentException(
                "No tienes permiso para acceder a esta mascota"
        );
    }

    // =========================================================
    // ¿ES CLIENTE?
    // =========================================================
    private boolean esCliente(Authentication auth) {

        if (auth == null) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(authority ->
                        "ROLE_CLIENTE".equals(
                                authority.getAuthority()
                        )
                );
    }

    // =========================================================
    // ¿PUEDE GESTIONAR TODAS LAS MASCOTAS?
    // =========================================================
    private boolean puedeGestionarTodasLasMascotas(
            Authentication auth) {

        if (auth == null) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(authority -> {

                    String role = authority.getAuthority();

                    return "ROLE_VETERINARIO".equals(role)
                            || "ROLE_ADMIN".equals(role)
                            || "ROLE_GERENTE".equals(role)
                            || "ROLE_RECEPCIONISTA".equals(role);
                });
    }
}
