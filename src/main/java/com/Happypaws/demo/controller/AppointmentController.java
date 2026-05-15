package com.Happypaws.demo.controller;

import com.Happypaws.demo.dto.AppointmentDTO;
import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.PetService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    public AppointmentController(AppointmentService appointmentService, PetService petService, ClienteService clienteService) {
        this.appointmentService = appointmentService;
        this.petService = petService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", appointmentService.listar());
        return "views/citas/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("cita", new AppointmentDTO());
        model.addAttribute("mascotas", petService.listar());
        model.addAttribute("clientes", clienteService.listar());
        return "views/citas/create";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cita") AppointmentDTO dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mascotas", petService.listar());
            model.addAttribute("clientes", clienteService.listar());
            return "views/citas/create";
        }

        Appointment appointment = new Appointment();
        if (dto.getId() != null) {
            appointment.setId(dto.getId());
        }
        appointment.setFecha(dto.getFecha());
        appointment.setMotivo(dto.getMotivo());
        appointment.setMascota(petService.buscarPorId(dto.getPetId()).orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada")));
        appointment.setCliente(clienteService.buscarPorId(dto.getClienteId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado")));

        if (appointment.getId() == null) {
            appointmentService.guardar(appointment);
        } else {
            appointmentService.actualizar(appointment);
        }

        redirectAttributes.addFlashAttribute("success", "Cita guardada correctamente");
        return "redirect:/citas";
    }

    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setFecha(appointment.getFecha());
        dto.setMotivo(appointment.getMotivo());
        dto.setPetId(appointment.getMascota().getId());
        dto.setClienteId(appointment.getCliente().getId());
        model.addAttribute("cita", dto);
        model.addAttribute("mascotas", petService.listar());
        model.addAttribute("clientes", clienteService.listar());
        return "views/citas/create";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Cita eliminada correctamente");
        return "redirect:/citas";
    }
}