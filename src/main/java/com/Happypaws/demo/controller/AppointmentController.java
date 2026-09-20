package com.Happypaws.demo.controller;

import com.Happypaws.demo.dto.AppointmentDTO;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.EstadoCita;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/citas")
public class AppointmentController {

    // ============================================================
    // CATÁLOGO DE SERVICIOS
    // ============================================================

    private static final Map<String, List<String>> TIPOS_SERVICIO = Map.of(
            "Veterinaria",
            List.of(
                    "Control",
                    "Primera vez",
                    "Vacunación",
                    "Consulta",
                    "Desparasitación"
            ),

            "Estilista",
            List.of(
                    "Baño",
                    "Corte",
                    "Baño + corte",
                    "Corte de uñas"
            )
    );

    private final AppointmentService appointmentService;
    private final PetService petService;
    private final ClienteService clienteService;
    private final UserService userService;

    public AppointmentController(
            AppointmentService appointmentService,
            PetService petService,
            ClienteService clienteService,
            UserService userService) {

        this.appointmentService = appointmentService;
        this.petService = petService;
        this.clienteService = clienteService;
        this.userService = userService;
    }

    // ============================================================
    // LISTAR CITAS
    // ============================================================

    @GetMapping
    public String listar(
            Model model,
            Authentication auth) {

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");
        boolean esVeterinario = tieneRol(auth, "ROLE_VETERINARIO");
        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");

        model.addAttribute(
                "isClientUser",
                esCliente && !esVeterinario && !esAdmin
        );

        model.addAttribute(
                "estados",
                EstadoCita.values()
        );

        /*
         * ADMIN y VETERINARIO:
         * pueden ver todas las citas.
         */
        if (esAdmin || esVeterinario) {

            model.addAttribute(
                    "citas",
                    appointmentService.listar()
            );

        /*
         * CLIENTE:
         * solamente puede ver sus propias citas.
         */
        } else if (esCliente) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            model.addAttribute(
                    "citas",
                    appointmentService.listarPorClienteId(
                            cliente.getIdCliente()
                    )
            );

        /*
         * Otros roles autorizados.
         */
        } else {

            model.addAttribute(
                    "citas",
                    appointmentService.listar()
            );
        }

        return "views/citas/index";
    }

    // ============================================================
    // CREAR CITA
    // ============================================================

    @GetMapping("/create")
    public String create(
            Model model,
            Authentication auth) {

        /*
         * Valores iniciales seguros.
         *
         * Esto evita que servicio y tipoServicio comiencen
         * como null al cargar el formulario.
         */
        AppointmentDTO dto = new AppointmentDTO();

        dto.setServicio("Veterinaria");
        dto.setTipoServicio("Consulta");

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");
        boolean esVeterinario = tieneRol(auth, "ROLE_VETERINARIO");
        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");

        /*
         * CLIENTE PURO
         */
        if (esCliente && !esVeterinario && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            dto.setClienteId(
                    cliente.getIdCliente()
            );

            model.addAttribute(
                    "clienteNombre",
                    cliente.getRazonSocial()
            );

            model.addAttribute(
                    "mascotas",
                    petService.listarPorClienteId(
                            cliente.getIdCliente()
                    )
            );

        /*
         * ADMIN / VETERINARIO / STAFF
         */
        } else {

            model.addAttribute(
                    "clienteNombre",
                    "Seleccione un cliente"
            );

            model.addAttribute(
                    "mascotas",
                    petService.listar()
            );
        }

        model.addAttribute(
                "cita",
                dto
        );

        /*
         * Cargar catálogo de servicios de forma segura.
         */
        cargarCatalogoServicios(
                model,
                dto
        );

        model.addAttribute(
                "veterinarios",
                userService.listarVeterinarios()
        );

        return "views/citas/create";
    }

    // ============================================================
    // GUARDAR / ACTUALIZAR
    // ============================================================

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("cita") AppointmentDTO dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication auth) {

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");
        boolean esVeterinario = tieneRol(auth, "ROLE_VETERINARIO");
        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");

        // ========================================================
        // SEGURIDAD - ACTUALIZACIÓN DE CITA
        // ========================================================

        /*
         * Si un CLIENTE intenta actualizar una cita existente,
         * verificamos que la cita realmente le pertenezca.
         */
        if (esCliente
                && !esVeterinario
                && !esAdmin
                && dto.getId() != null) {

            Appointment citaExistente =
                    appointmentService.buscarPorId(dto.getId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Cita no encontrada"
                                    )
                            );

            Cliente clienteActual =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (citaExistente.getCliente() == null
                    || citaExistente.getCliente().getIdCliente() == null
                    || !clienteActual.getIdCliente().equals(
                            citaExistente
                                    .getCliente()
                                    .getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para modificar esta cita"
                );
            }
        }

        Cliente cliente = null;

        // ========================================================
        // CLIENTE AUTENTICADO
        // ========================================================

        /*
         * Un CLIENTE puro no puede seleccionar otro cliente.
         * Siempre utilizamos el cliente autenticado.
         */
        if (esCliente
                && !esVeterinario
                && !esAdmin) {

            cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            dto.setClienteId(
                    cliente.getIdCliente()
            );
        }

        // ========================================================
        // VALIDACIÓN DE FECHA
        // ========================================================

        /*
         * La cita debe agendarse mínimo 15 minutos después
         * de la hora actual de Colombia.
         */
        if (dto.getFechaHora() != null) {

            LocalDateTime fechaMinima =
                    LocalDateTime
                            .now(ZoneId.of("America/Bogota"))
                            .truncatedTo(ChronoUnit.MINUTES)
                            .plusMinutes(15);

            if (dto.getFechaHora().isBefore(fechaMinima)) {

                bindingResult.rejectValue(
                        "fechaHora",
                        "fechaHora.minima",
                        "La cita debe agendarse mínimo 15 minutos después de la fecha y hora actual"
                );
            }
        }

        // ========================================================
        // VALIDACIÓN DE SERVICIO
        // ========================================================

        normalizarYValidarServicio(
                dto,
                bindingResult
        );

        // ========================================================
        // ERRORES DEL FORMULARIO
        // ========================================================

        if (bindingResult.hasErrors()) {

            if (cliente != null) {

                model.addAttribute(
                        "mascotas",
                        petService.listarPorClienteId(
                                cliente.getIdCliente()
                        )
                );

                model.addAttribute(
                        "clienteNombre",
                        cliente.getRazonSocial()
                );

            } else {

                model.addAttribute(
                        "mascotas",
                        petService.listar()
                );

                model.addAttribute(
                        "clienteNombre",
                        "Seleccione un cliente"
                );
            }

            model.addAttribute(
                    "veterinarios",
                    userService.listarVeterinarios()
            );

            /*
             * IMPORTANTE:
             * volver a cargar el catálogo utilizando los valores
             * que el usuario envió.
             */
            cargarCatalogoServicios(
                    model,
                    dto
            );

            return "views/citas/create";
        }

        // ========================================================
        // CONSTRUIR ENTIDAD APPOINTMENT
        // ========================================================

        Appointment appointment =
                new Appointment();

        if (dto.getId() != null) {

            appointment.setIdCita(
                    dto.getId()
            );
        }

        appointment.setFechaHora(
                dto.getFechaHora()
        );

        appointment.setMotivo(
                dto.getMotivo()
        );

        appointment.setServicio(
                dto.getServicio()
        );

        appointment.setTipoServicio(
                dto.getTipoServicio()
        );

        // ========================================================
        // MASCOTA
        // ========================================================

        appointment.setMascota(
                petService.buscarPorId(
                        dto.getPetId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Mascota no encontrada"
                        )
                )
        );

        // ========================================================
        // VETERINARIO
        // ========================================================

        appointment.setVeterinario(
                userService.buscarPorId(
                        dto.getVeterinarioId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Veterinario no encontrado"
                        )
                )
        );

        // ========================================================
        // CLIENTE
        // ========================================================

        /*
         * CLIENTE PURO:
         * solamente puede crear una cita para una mascota propia.
         */
        if (esCliente
                && !esVeterinario
                && !esAdmin) {

            cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (appointment.getMascota().getCliente() == null
                    || appointment.getMascota()
                            .getCliente()
                            .getIdCliente() == null
                    || !cliente.getIdCliente().equals(
                            appointment.getMascota()
                                    .getCliente()
                                    .getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No puedes agendar una cita para una mascota que no es tuya"
                );
            }

            appointment.setCliente(
                    cliente
            );

        } else {

            /*
             * ADMIN / VETERINARIO / STAFF
             * pueden seleccionar el cliente.
             */
            appointment.setCliente(
                    clienteService.buscarPorId(
                            dto.getClienteId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Cliente no encontrado"
                            )
                    )
            );
        }

        // ========================================================
        // GUARDAR O ACTUALIZAR
        // ========================================================

        if (appointment.getIdCita() == null) {

            appointmentService.guardar(
                    appointment
            );

        } else {

            appointmentService.actualizar(
                    appointment
            );
        }

        // ========================================================
        // MENSAJE DE ÉXITO
        // ========================================================

        redirectAttributes.addFlashAttribute(
                "success",
                "Cita guardada correctamente"
        );

        return "redirect:/citas";
    }

    // ============================================================
    // EDITAR
    // ============================================================

    @GetMapping("/edit/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            Authentication auth) {

        Appointment appointment =
                appointmentService.buscarPorId(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cita no encontrada"
                                )
                        );

        boolean esCliente = tieneRol(
                auth,
                "ROLE_CLIENTE"
        );

        boolean esVeterinario = tieneRol(
                auth,
                "ROLE_VETERINARIO"
        );

        boolean esAdmin = tieneRol(
                auth,
                "ROLE_ADMIN"
        );

        // ========================================================
        // SEGURIDAD DEL CLIENTE
        // ========================================================

        if (esCliente
                && !esVeterinario
                && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (appointment.getCliente() == null
                    || appointment.getCliente().getIdCliente() == null
                    || !cliente.getIdCliente().equals(
                            appointment
                                    .getCliente()
                                    .getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para editar esta cita"
                );
            }
        }

        // ========================================================
        // CREAR DTO
        // ========================================================

        AppointmentDTO dto =
                new AppointmentDTO();

        dto.setId(
                appointment.getIdCita()
        );

        dto.setFechaHora(
                appointment.getFechaHora()
        );

        dto.setMotivo(
                appointment.getMotivo()
        );

        dto.setServicio(
                appointment.getServicio()
        );

        dto.setTipoServicio(
                appointment.getTipoServicio()
        );

        /*
         * Compatibilidad con citas antiguas que no tenían
         * servicio/tipoServicio.
         */
        if (dto.getServicio() == null
                || dto.getServicio().isBlank()) {

            dto.setServicio(
                    "Veterinaria"
            );
        }

        if (dto.getTipoServicio() == null
                || dto.getTipoServicio().isBlank()) {

            dto.setTipoServicio(
                    "Consulta"
            );
        }

        // ========================================================
        // MASCOTA
        // ========================================================

        if (appointment.getMascota() != null) {

            dto.setPetId(
                    appointment
                            .getMascota()
                            .getIdMascota()
            );
        }

        // ========================================================
        // CLIENTE
        // ========================================================

        if (appointment.getCliente() != null) {

            dto.setClienteId(
                    appointment
                            .getCliente()
                            .getIdCliente()
            );
        }

        // ========================================================
        // VETERINARIO
        // ========================================================

        if (appointment.getVeterinario() != null) {

            dto.setVeterinarioId(
                    appointment
                            .getVeterinario()
                            .getIdUsuario()
            );
        }

        model.addAttribute(
                "cita",
                dto
        );

        // ========================================================
        // DATOS PARA CLIENTE
        // ========================================================

        if (esCliente
                && !esVeterinario
                && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            model.addAttribute(
                    "mascotas",
                    petService.listarPorClienteId(
                            cliente.getIdCliente()
                    )
            );

            model.addAttribute(
                    "clienteNombre",
                    cliente.getRazonSocial()
            );

        } else {

            // ====================================================
            // ADMIN / VETERINARIO / STAFF
            // ====================================================

            model.addAttribute(
                    "mascotas",
                    petService.listar()
            );

            model.addAttribute(
                    "clienteNombre",
                    appointment.getCliente() != null
                            ? appointment
                                    .getCliente()
                                    .getRazonSocial()
                            : "Cliente"
            );
        }

        // ========================================================
        // VETERINARIOS
        // ========================================================

        model.addAttribute(
                "veterinarios",
                userService.listarVeterinarios()
        );

        // ========================================================
        // CATÁLOGO DE SERVICIOS
        // ========================================================

        cargarCatalogoServicios(
                model,
                dto
        );

        return "views/citas/create";
    }

    // ============================================================
    // CATÁLOGO DE SERVICIOS
    // ============================================================

    /**
     * Carga el catálogo de servicios para el formulario.
     *
     * IMPORTANTE:
     * TIPOS_SERVICIO es un Map.of(), que no admite búsquedas
     * con claves null.
     *
     * Por eso nunca llamamos:
     *
     * TIPOS_SERVICIO.getOrDefault(null, ...)
     *
     * directamente.
     */
    private void cargarCatalogoServicios(
            Model model,
            AppointmentDTO dto) {

        String servicio =
                dto != null
                        ? dto.getServicio()
                        : null;

        List<String> tiposServicio;

        if (servicio == null
                || servicio.isBlank()) {

            tiposServicio = List.of();

        } else {

            tiposServicio =
                    TIPOS_SERVICIO.getOrDefault(
                            servicio,
                            List.of()
                    );
        }

        model.addAttribute(
                "servicios",
                TIPOS_SERVICIO.keySet()
        );

        model.addAttribute(
                "tiposServicio",
                tiposServicio
        );

        model.addAttribute(
                "tiposServicioPorServicio",
                TIPOS_SERVICIO
        );
    }

    // ============================================================
    // NORMALIZAR Y VALIDAR SERVICIO
    // ============================================================

    private void normalizarYValidarServicio(
            AppointmentDTO dto,
            BindingResult bindingResult) {

        if (dto == null) {
            return;
        }

        // ========================================================
        // LIMPIEZA DE DATOS
        // ========================================================

        if (dto.getServicio() != null) {

            dto.setServicio(
                    dto.getServicio().trim()
            );
        }

        if (dto.getTipoServicio() != null) {

            dto.setTipoServicio(
                    dto.getTipoServicio().trim()
            );
        }

        // ========================================================
        // SERVICIO OBLIGATORIO
        // ========================================================

        if (dto.getServicio() == null
                || dto.getServicio().isBlank()) {

            bindingResult.rejectValue(
                    "servicio",
                    "servicio.requerido",
                    "Selecciona un servicio"
            );

            return;
        }

        // ========================================================
        // VALIDAR SERVICIO CONTRA EL CATÁLOGO
        // ========================================================

        List<String> tiposPermitidos =
                TIPOS_SERVICIO.get(
                        dto.getServicio()
                );

        if (tiposPermitidos == null) {

            bindingResult.rejectValue(
                    "servicio",
                    "servicio.invalido",
                    "Selecciona un servicio válido"
            );

            return;
        }

        // ========================================================
        // TIPO DE SERVICIO OBLIGATORIO
        // ========================================================

        if (dto.getTipoServicio() == null
                || dto.getTipoServicio().isBlank()) {

            bindingResult.rejectValue(
                    "tipoServicio",
                    "tipoServicio.requerido",
                    "Selecciona un tipo de servicio"
            );

            return;
        }

        // ========================================================
        // VALIDAR TIPO DE SERVICIO
        // ========================================================

        if (!tiposPermitidos.contains(
                dto.getTipoServicio()
        )) {

            bindingResult.rejectValue(
                    "tipoServicio",
                    "tipoServicio.invalido",
                    "Selecciona un tipo de servicio válido"
            );
        }
    }

    // ============================================================
    // VER CITA
    // ============================================================

    @GetMapping("/show/{id}")
    public String ver(
            @PathVariable Long id,
            Model model,
            Authentication auth) {

        Appointment cita =
                appointmentService.buscarPorId(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cita no encontrada"
                                )
                        );

        boolean esCliente = tieneRol(
                auth,
                "ROLE_CLIENTE"
        );

        boolean esVeterinario = tieneRol(
                auth,
                "ROLE_VETERINARIO"
        );

        boolean esAdmin = tieneRol(
                auth,
                "ROLE_ADMIN"
        );

        // ========================================================
        // SEGURIDAD DEL CLIENTE
        // ========================================================

        if (esCliente
                && !esVeterinario
                && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (cita.getCliente() == null
                    || cita.getCliente().getIdCliente() == null
                    || !cliente.getIdCliente().equals(
                            cita.getCliente().getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para ver esta cita"
                );
            }
        }

        model.addAttribute(
                "cita",
                cita
        );

        return "views/citas/show";
    }

    // ============================================================
    // ELIMINAR
    // ============================================================

    @GetMapping("/delete/{id}")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Authentication auth) {

        Appointment cita =
                appointmentService.buscarPorId(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cita no encontrada"
                                )
                        );

        boolean esCliente = tieneRol(
                auth,
                "ROLE_CLIENTE"
        );

        boolean esVeterinario = tieneRol(
                auth,
                "ROLE_VETERINARIO"
        );

        boolean esAdmin = tieneRol(
                auth,
                "ROLE_ADMIN"
        );

        // ========================================================
        // SEGURIDAD DEL CLIENTE
        // ========================================================

        if (esCliente
                && !esVeterinario
                && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (cita.getCliente() == null
                    || cita.getCliente().getIdCliente() == null
                    || !cliente.getIdCliente().equals(
                            cita.getCliente().getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para eliminar esta cita"
                );
            }
        }

        appointmentService.eliminar(
                id
        );

        redirectAttributes.addFlashAttribute(
                "success",
                "Cita eliminada correctamente"
        );

        return "redirect:/citas";
    }

    // ============================================================
    // CAMBIAR ESTADO
    // ============================================================

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam("estado") EstadoCita nuevoEstado,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        Appointment cita =
                appointmentService.buscarPorId(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Cita no encontrada"
                                )
                        );

        boolean esCliente = tieneRol(
                auth,
                "ROLE_CLIENTE"
        );

        boolean esVeterinario = tieneRol(
                auth,
                "ROLE_VETERINARIO"
        );

        boolean esAdmin = tieneRol(
                auth,
                "ROLE_ADMIN"
        );

        // ========================================================
        // SEGURIDAD DEL CLIENTE
        // ========================================================

        if (esCliente
                && !esVeterinario
                && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            boolean esSuCita =
                    cita.getCliente() != null
                            && cita.getCliente().getIdCliente() != null
                            && cliente.getIdCliente().equals(
                                    cita.getCliente()
                                            .getIdCliente()
                            );

            if (!esSuCita) {

                throw new IllegalArgumentException(
                        "No tienes permiso para modificar esta cita"
                );
            }

            /*
             * El cliente únicamente puede cancelar.
             */
            if (nuevoEstado != EstadoCita.CANCELADA) {

                throw new AccessDeniedException(
                        "Solo puedes cancelar tu cita"
                );
            }
        }

        // ========================================================
        // CAMBIAR ESTADO
        // ========================================================

        try {

            appointmentService.cambiarEstado(
                    id,
                    nuevoEstado
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Estado de la cita actualizado"
            );

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }

        return "redirect:/citas";
    }

    // ============================================================
    // MÉTODO AUXILIAR - ROLES
    // ============================================================

    private boolean tieneRol(
            Authentication auth,
            String rol) {

        if (auth == null
                || auth.getAuthorities() == null) {

            return false;
        }

        return auth.getAuthorities()
                .stream()
                .anyMatch(
                        authority ->
                                authority != null
                                        && rol.equals(
                                                authority
                                                        .getAuthority()
                                        )
                );
    }
}

