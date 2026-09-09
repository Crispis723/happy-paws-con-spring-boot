package com.Happypaws.demo.controller;

import com.Happypaws.demo.dto.AppointmentDTO;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.UserService;
import jakarta.validation.Valid;
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

        private static final Map<String, List<String>> TIPOS_SERVICIO = Map.of(
                        "Veterinaria", List.of("Control", "Primera vez", "Vacunación", "Consulta", "Desparasitación"),
                        "Estilista", List.of("Baño", "Corte", "Baño + corte", "Corte de uñas")
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
    public String listar(Model model, Authentication auth) {

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");
        boolean esVeterinario = tieneRol(auth, "ROLE_VETERINARIO");
        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");
        model.addAttribute("isClientUser", esCliente && !esVeterinario && !esAdmin);

        /*
         * ADMIN y VETERINARIO:
         * pueden ver TODAS las citas.
         *
         * CLIENTE:
         * solamente sus propias citas.
         */

        if (esAdmin || esVeterinario) {

            model.addAttribute(
                    "citas",
                    appointmentService.listar()
            );

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

        } else {

            // Otros roles autorizados
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
    public String create(Model model, Authentication auth) {

        AppointmentDTO dto = new AppointmentDTO();

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");

        if (esCliente && !tieneRol(auth, "ROLE_VETERINARIO")
                && !tieneRol(auth, "ROLE_ADMIN")) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            dto.setClienteId(cliente.getIdCliente());

            model.addAttribute(
                    "clienteNombre",
                    cliente.getRazonSocial()
            );

            model.addAttribute(
                    "mascotas",
                    petService.listarPorClienteId(cliente.getIdCliente())
            );

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

        model.addAttribute("cita", dto);
        cargarCatalogoServicios(model, dto);

        model.addAttribute(
                "veterinarios",
                userService.listarVeterinarios()
        );

        return "views/citas/create";
    }

    // ============================================================
    // GUARDAR
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

        /*
         * SEGURIDAD (control de acceso a nivel de recurso):
         * Si es una actualización (dto.getId() viene informado) y el
         * usuario es CLIENTE puro, hay que verificar que la CITA que
         * se va a sobrescribir ya le pertenecía. Más abajo solo se
         * valida que la mascota elegida sea suya, pero eso no impide
         * que un cliente envíe el id de la cita de OTRO cliente junto
         * con una de sus propias mascotas y termine reasignando/
         * modificando una cita ajena.
         */
        if (esCliente && !esVeterinario && !esAdmin && dto.getId() != null) {

            Appointment citaExistente = appointmentService.buscarPorId(dto.getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Cita no encontrada")
                    );

            Cliente clienteActual = clienteService.resolverOCrearClienteAutenticado(
                    auth.getName(),
                    auth.getName()
            );

            if (citaExistente.getCliente() == null
                    || citaExistente.getCliente().getIdCliente() == null
                    || !clienteActual.getIdCliente().equals(
                            citaExistente.getCliente().getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para modificar esta cita"
                );
            }
        }

        Cliente cliente = null;

        /*
         * Si es CLIENTE puro, obligatoriamente usamos
         * el cliente autenticado.
         */
        if (esCliente && !esVeterinario && !esAdmin) {

            cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            dto.setClienteId(cliente.getIdCliente());
        }

        // Una cita solo puede agendarse desde 15 minutos después de la hora actual.
        // Se usa explícitamente la zona horaria de Colombia para que Render/servidores UTC
        // no cambien la referencia de tiempo de la aplicación.
        if (dto.getFechaHora() != null) {
            LocalDateTime fechaMinima = LocalDateTime
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

        normalizarYValidarServicio(dto, bindingResult);

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
            cargarCatalogoServicios(model, dto);

            return "views/citas/create";
        }

        Appointment appointment = new Appointment();

        if (dto.getId() != null) {
            appointment.setIdCita(dto.getId());
        }

        appointment.setFechaHora(dto.getFechaHora());
        appointment.setMotivo(dto.getMotivo());
        appointment.setServicio(dto.getServicio());
        appointment.setTipoServicio(dto.getTipoServicio());

        // Mascota
        Appointment finalAppointment = appointment;

        appointment.setMascota(
                petService.buscarPorId(dto.getPetId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Mascota no encontrada"
                                )
                        )
        );

        // Veterinario
        appointment.setVeterinario(
                userService.buscarPorId(dto.getVeterinarioId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Veterinario no encontrado"
                                )
                        )
        );

        /*
         * CLIENTE puro:
         * solamente puede crear cita para sus mascotas.
         */
        if (esCliente && !esVeterinario && !esAdmin) {

            cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (appointment.getMascota().getCliente() == null ||
                    !cliente.getIdCliente().equals(
                            appointment.getMascota()
                                    .getCliente()
                                    .getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No puedes agendar una cita para una mascota que no es tuya"
                );
            }

            appointment.setCliente(cliente);

        } else {

            /*
             * ADMIN / VETERINARIO / STAFF
             * pueden seleccionar el cliente.
             */
            appointment.setCliente(
                    clienteService.buscarPorId(dto.getClienteId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Cliente no encontrado"
                                    )
                            )
            );
        }

        if (appointment.getIdCita() == null) {

            appointmentService.guardar(appointment);

        } else {

            appointmentService.actualizar(appointment);
        }

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

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");
        boolean esVeterinario = tieneRol(auth, "ROLE_VETERINARIO");
        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");

        /*
         * CLIENTE puro solamente puede editar
         * sus propias citas.
         */
        if (esCliente && !esVeterinario && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (appointment.getCliente() == null ||
                    !cliente.getIdCliente().equals(
                            appointment.getCliente().getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para editar esta cita"
                );
            }
        }

        AppointmentDTO dto = new AppointmentDTO();

        dto.setId(appointment.getIdCita());
        dto.setFechaHora(appointment.getFechaHora());
        dto.setMotivo(appointment.getMotivo());
        dto.setServicio(appointment.getServicio());
        dto.setTipoServicio(appointment.getTipoServicio());
                if (dto.getServicio() == null || dto.getServicio().isBlank()) {
                        dto.setServicio("Veterinaria");
                }
                if (dto.getTipoServicio() == null || dto.getTipoServicio().isBlank()) {
                        dto.setTipoServicio("Consulta");
                }
        dto.setPetId(appointment.getMascota().getIdMascota());

        if (appointment.getCliente() != null) {
            dto.setClienteId(
                    appointment.getCliente().getIdCliente()
            );
        }

        if (appointment.getVeterinario() != null) {
            dto.setVeterinarioId(
                    appointment.getVeterinario().getIdUsuario()
            );
        }

        model.addAttribute("cita", dto);

        /*
         * CLIENTE puro
         */
        if (esCliente && !esVeterinario && !esAdmin) {

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

            /*
             * ADMIN / VETERINARIO
             */
            model.addAttribute(
                    "mascotas",
                    petService.listar()
            );

            model.addAttribute(
                    "clienteNombre",
                    appointment.getCliente() != null
                            ? appointment.getCliente().getRazonSocial()
                            : "Cliente"
            );
        }

        model.addAttribute(
                "veterinarios",
                userService.listarVeterinarios()
        );
        cargarCatalogoServicios(model, dto);

        return "views/citas/create";
    }

        private void cargarCatalogoServicios(Model model, AppointmentDTO dto) {
                model.addAttribute("servicios", TIPOS_SERVICIO.keySet());
                model.addAttribute("tiposServicio", TIPOS_SERVICIO.getOrDefault(
                                dto.getServicio(), List.of()));
                model.addAttribute("tiposServicioPorServicio", TIPOS_SERVICIO);
        }

        private void normalizarYValidarServicio(AppointmentDTO dto, BindingResult bindingResult) {
                if (dto.getId() != null && (dto.getServicio() == null || dto.getServicio().isBlank())) {
                        dto.setServicio("Veterinaria");
                }
                if (dto.getId() != null && (dto.getTipoServicio() == null || dto.getTipoServicio().isBlank())) {
                        dto.setTipoServicio("Consulta");
                }
                if (dto.getServicio() == null || dto.getServicio().isBlank()) {
                        bindingResult.rejectValue("servicio", "servicio.requerido", "Selecciona un servicio");
                        return;
                }
                List<String> tiposPermitidos = TIPOS_SERVICIO.get(dto.getServicio());
                if (tiposPermitidos == null) {
                        bindingResult.rejectValue("servicio", "servicio.invalido", "Selecciona un servicio válido");
                } else if (dto.getTipoServicio() == null || !tiposPermitidos.contains(dto.getTipoServicio())) {
                        bindingResult.rejectValue("tipoServicio", "tipoServicio.invalido", "Selecciona un tipo de servicio válido");
                }
        }

    // ============================================================
    // VER
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

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");
        boolean esVeterinario = tieneRol(auth, "ROLE_VETERINARIO");
        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");

        /*
         * CLIENTE puro solamente ve sus citas.
         */
        if (esCliente && !esVeterinario && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (cita.getCliente() == null ||
                    !cliente.getIdCliente().equals(
                            cita.getCliente().getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para ver esta cita"
                );
            }
        }

        model.addAttribute("cita", cita);

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

        boolean esCliente = tieneRol(auth, "ROLE_CLIENTE");
        boolean esVeterinario = tieneRol(auth, "ROLE_VETERINARIO");
        boolean esAdmin = tieneRol(auth, "ROLE_ADMIN");

        /*
         * CLIENTE puro solamente puede eliminar
         * sus propias citas.
         */
        if (esCliente && !esVeterinario && !esAdmin) {

            Cliente cliente =
                    clienteService.resolverOCrearClienteAutenticado(
                            auth.getName(),
                            auth.getName()
                    );

            if (cita.getCliente() == null ||
                    !cliente.getIdCliente().equals(
                            cita.getCliente().getIdCliente()
                    )) {

                throw new IllegalArgumentException(
                        "No tienes permiso para eliminar esta cita"
                );
            }
        }

        appointmentService.eliminar(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Cita eliminada correctamente"
        );

        return "redirect:/citas";
    }

    // ============================================================
    // MÉTODO AUXILIAR
    // ============================================================

    private boolean tieneRol(
            Authentication auth,
            String rol) {

        if (auth == null) {
            return false;
        }

        return auth.getAuthorities()
                .stream()
                .anyMatch(
                        authority ->
                                rol.equals(
                                        authority.getAuthority()
                                )
                );
    }
}
