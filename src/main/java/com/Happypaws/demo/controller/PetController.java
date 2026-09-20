package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.Desparasitacion;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.model.Vacuna;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.DesparasitacionService;
import com.Happypaws.demo.service.HistorialMascotaService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.VacunaService;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class PetController {

        private static final List<String> ESPECIES_MASCOTA = List.of(
                        "Perro", "Gato", "Ave", "Pez", "Conejo", "Hamster",
                        "Cobayo", "Hurón", "Reptil", "Tortuga"
        );

        private static final List<String> SEXOS_MASCOTA = List.of("Macho", "Hembra");

    private final PetService petService;
    private final ClienteService clienteService;
    private final VacunaService vacunaService;
    private final DesparasitacionService desparasitacionService;
    private final HistorialMascotaService historialMascotaService;

    public PetController(PetService petService, ClienteService clienteService,
                          VacunaService vacunaService, DesparasitacionService desparasitacionService,
                          HistorialMascotaService historialMascotaService) {
        this.petService = petService;
        this.clienteService = clienteService;
        this.vacunaService = vacunaService;
        this.desparasitacionService = desparasitacionService;
        this.historialMascotaService = historialMascotaService;
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
            @RequestParam(name = "fotoArchivo", required = false) MultipartFile fotoArchivo,
            RedirectAttributes redirectAttributes,
            Model model,
            Authentication auth) {

        boolean isClientUser = esCliente(auth);
        boolean canManageAllPets = puedeGestionarTodasLasMascotas(auth);

        /*
         * SEGURIDAD (control de acceso a nivel de recurso):
         * Si es una actualización (idMascota viene informado) y el
         * usuario es CLIENTE puro, hay que verificar que la mascota
         * YA le pertenecía ANTES de aplicar los cambios. Sin este
         * chequeo, un cliente podía enviar el id de la mascota de
         * OTRO cliente en el formulario y reasignarla a su cuenta,
         * sobrescribiendo sus datos (el resto del controlador ya
         * protege ver/editar-formulario/eliminar con
         * validarPermisoMascota, pero este POST no pasaba por ahí).
         */
        if (isClientUser && !canManageAllPets && mascota.getIdMascota() != null) {

            Pet mascotaExistente = petService.buscarPorId(mascota.getIdMascota())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Mascota no encontrada")
                    );

            validarPermisoMascota(mascotaExistente, auth);
        }

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
        // FOTO DE LA MASCOTA (mismo patrón que ProductoController)
        // =====================================================
        String fotoAnterior = null;

        if (mascota.getIdMascota() != null) {
            fotoAnterior = petService.buscarPorId(mascota.getIdMascota())
                    .map(Pet::getFoto)
                    .orElse(null);

            // Si no se sube una foto nueva, se conserva la actual.
            mascota.setFoto(fotoAnterior);
        }

        try {
            String nombreArchivo = petService.guardarFoto(fotoArchivo);

            if (nombreArchivo != null) {
                mascota.setFoto(nombreArchivo);
            }

        } catch (IllegalArgumentException | IOException ex) {

            model.addAttribute("error", ex.getMessage() != null
                    ? ex.getMessage()
                    : "No se pudo guardar la foto de la mascota.");

            aplicarContextoCliente(model, mascota, auth);

            return "views/mascotas/formulario";
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

            // Si se reemplazó la foto, se borra el archivo anterior.
            if (fotoAnterior != null && !fotoAnterior.equals(mascota.getFoto())) {
                petService.eliminarFotoSiExiste(fotoAnterior);
            }
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
        model.addAttribute("vacunas", vacunaService.listarPorMascotaId(id));
        model.addAttribute("desparasitaciones", desparasitacionService.listarPorMascotaId(id));
        model.addAttribute("historialReciente", historialMascotaService.listarPorMascotaId(id)
                .stream().limit(5).toList());
        model.addAttribute("canManageAllPets", puedeGestionarTodasLasMascotas(auth));

        return "views/mascotas/show";
    }

    // =========================================================
    // CONTEXTO DEL CLIENTE
    // =========================================================
    private void aplicarContextoCliente(
            Model model,
            Pet mascota,
            Authentication auth) {

                model.addAttribute("especiesMascota", ESPECIES_MASCOTA);
                model.addAttribute("sexosMascota", SEXOS_MASCOTA);

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

    // =========================================================
    // VACUNAS
    // =========================================================

    @PostMapping("/mascotas/{petId}/vacunas/guardar")
    public String guardarVacuna(
            @PathVariable Long petId,
            @ModelAttribute("vacuna") Vacuna vacuna,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        Pet mascota = petService.buscarPorId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        validarPermisoMascota(mascota, auth);
        validarColaborador(auth);

        vacuna.setMascota(mascota);
        vacunaService.guardar(vacuna);

        redirectAttributes.addFlashAttribute("success", "Vacuna registrada correctamente");
        return "redirect:/mascotas/" + petId;
    }

    @GetMapping("/mascotas/{petId}/vacunas/{vacunaId}/eliminar")
    public String eliminarVacuna(
            @PathVariable Long petId,
            @PathVariable Long vacunaId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        Pet mascota = petService.buscarPorId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        validarPermisoMascota(mascota, auth);
        validarColaborador(auth);

        vacunaService.eliminar(vacunaId);

        redirectAttributes.addFlashAttribute("success", "Vacuna eliminada correctamente");
        return "redirect:/mascotas/" + petId;
    }

    // =========================================================
    // DESPARASITACIÓN
    // =========================================================

    @PostMapping("/mascotas/{petId}/desparasitaciones/guardar")
    public String guardarDesparasitacion(
            @PathVariable Long petId,
            @ModelAttribute("desparasitacion") Desparasitacion desparasitacion,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        Pet mascota = petService.buscarPorId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        validarPermisoMascota(mascota, auth);
        validarColaborador(auth);

        desparasitacion.setMascota(mascota);
        desparasitacionService.guardar(desparasitacion);

        redirectAttributes.addFlashAttribute("success", "Desparasitación registrada correctamente");
        return "redirect:/mascotas/" + petId;
    }

    @GetMapping("/mascotas/{petId}/desparasitaciones/{desparasitacionId}/eliminar")
    public String eliminarDesparasitacion(
            @PathVariable Long petId,
            @PathVariable Long desparasitacionId,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        Pet mascota = petService.buscarPorId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        validarPermisoMascota(mascota, auth);
        validarColaborador(auth);

        desparasitacionService.eliminar(desparasitacionId);

        redirectAttributes.addFlashAttribute("success", "Desparasitación eliminada correctamente");
        return "redirect:/mascotas/" + petId;
    }

    /**
     * Solo el personal clínico (no un CLIENTE puro) puede registrar o
     * eliminar vacunas/desparasitaciones, igual que con el historial
     * clínico (ver HistorialMascotaController.validarColaborador).
     */
    private void validarColaborador(Authentication auth) {
        if (esCliente(auth) && !puedeGestionarTodasLasMascotas(auth)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permisos para modificar este registro"
            );
        }
    }
}
