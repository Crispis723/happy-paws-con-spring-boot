package com.Happypaws.demo.controller;

import com.Happypaws.demo.dto.AppointmentDTO;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.UserService;
import com.Happypaws.demo.model.Cliente;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/citas")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PetService petService;
    private final ClienteService clienteService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService, PetService petService, ClienteService clienteService, UserService userService) {
        this.appointmentService = appointmentService;
        this.petService = petService;
        this.clienteService = clienteService;
        this.userService = userService;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            model.addAttribute("citas", appointmentService.listarPorClienteId(cliente.getId()));
        } else {
            model.addAttribute("citas", appointmentService.listar());
        }
        return "views/citas/index";
    }

    @GetMapping("/create")
    public String create(Model model, Authentication auth) {
        AppointmentDTO dto = new AppointmentDTO();
        if (auth != null) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            dto.setClienteId(cliente.getId());
            model.addAttribute("clienteNombre", cliente.getRazonSocial());
            model.addAttribute("mascotas", petService.listarPorClienteId(cliente.getId()));
        } else {
            model.addAttribute("clienteNombre", "Cliente no autenticado");
            model.addAttribute("mascotas", petService.listar());
        }
        model.addAttribute("cita", dto);
        model.addAttribute("veterinarios", userService.listarVeterinarios());
        return "views/citas/create";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cita") AppointmentDTO dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, Authentication auth) {
        if (auth != null) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            dto.setClienteId(cliente.getId());
        }

        if (bindingResult.hasErrors()) {
            if (auth != null) {
                Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
                model.addAttribute("mascotas", petService.listarPorClienteId(cliente.getId()));
                model.addAttribute("clienteNombre", cliente.getRazonSocial());
            } else {
                model.addAttribute("mascotas", petService.listar());
                model.addAttribute("clienteNombre", "Cliente no autenticado");
            }
            model.addAttribute("veterinarios", userService.listarVeterinarios());
            return "views/citas/create";
        }

        Appointment appointment = new Appointment();
        if (dto.getId() != null) {
            appointment.setId(dto.getId());
        }
        appointment.setFecha(dto.getFecha());
        appointment.setMotivo(dto.getMotivo());
        appointment.setMascota(petService.buscarPorId(dto.getPetId()).orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada")));
        appointment.setVeterinario(userService.buscarPorId(dto.getVeterinarioId())
            .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado")));

        if (auth != null) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            // Ensure pet belongs to client
            if (!appointment.getMascota().getCliente().getId().equals(cliente.getId())) {
                throw new IllegalArgumentException("No puedes agendar una cita para una mascota que no es tuya");
            }
            appointment.setCliente(cliente);
        } else {
            appointment.setCliente(clienteService.buscarPorId(dto.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado")));
        }

        boolean isNew = appointment.getId() == null;
        if (isNew) {
            appointmentService.guardar(appointment);
        } else {
            appointmentService.actualizar(appointment);
        }

        redirectAttributes.addFlashAttribute("success", "Cita guardada correctamente");
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Appointment appointment = appointmentService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setFecha(appointment.getFecha());
        dto.setMotivo(appointment.getMotivo());
        dto.setPetId(appointment.getMascota().getId());
        dto.setClienteId(appointment.getCliente().getId());
        dto.setVeterinarioId(appointment.getVeterinario() != null ? appointment.getVeterinario().getId() : null);
        model.addAttribute("cita", dto);
        if (auth != null) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            if (!appointment.getCliente().getId().equals(cliente.getId())) {
                throw new IllegalArgumentException("No tienes permiso para editar esta cita");
            }
            model.addAttribute("mascotas", petService.listarPorClienteId(cliente.getId()));
            model.addAttribute("clienteNombre", cliente.getRazonSocial());
            model.addAttribute("veterinarios", userService.listarVeterinarios());
        } else {
            model.addAttribute("mascotas", petService.listar());
            model.addAttribute("clienteNombre", appointment.getCliente().getRazonSocial());
            model.addAttribute("veterinarios", userService.listarVeterinarios());
        }
        return "views/citas/create";
    }

    @GetMapping("/show/{id}")
    public String ver(@PathVariable Long id, Model model, Authentication auth) {
        Appointment cita = appointmentService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (auth != null) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            if (!cita.getCliente().getId().equals(cliente.getId())) {
                throw new IllegalArgumentException("No tienes permiso para ver esta cita");
            }
        }

        model.addAttribute("cita", cita);
        return "views/citas/show";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            Appointment cita = appointmentService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            if (!cita.getCliente().getId().equals(cliente.getId())) {
                throw new IllegalArgumentException("No tienes permiso para eliminar esta cita");
            }
        }
        appointmentService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Cita eliminada correctamente");
        return "redirect:/citas";
    }
}