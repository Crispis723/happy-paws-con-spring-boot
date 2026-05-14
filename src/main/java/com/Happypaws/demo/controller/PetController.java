package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.repository.ClienteRepository;
import com.Happypaws.demo.service.PetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PetController {

    private final PetService petService;
    private final ClienteRepository clienteRepository;

    public PetController(PetService petService, ClienteRepository clienteRepository) {
        this.petService = petService;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/mascotas")
    public String listar(Model model) {
        model.addAttribute("mascotas", petService.listar());
        return "views/mascotas/index";
    }

    @GetMapping("/mascotas/nuevo")
    public String nuevo(Model model) {
        Pet mascota = new Pet();
        mascota.setCliente(new Cliente());
        model.addAttribute("mascota", mascota);
        model.addAttribute("clientes", clienteRepository.findAll());
        return "views/mascotas/formulario";
    }

    @PostMapping("/mascotas/guardar")
    public String guardar(@ModelAttribute("mascota") Pet mascota, RedirectAttributes redirectAttributes, Model model) {
        if (mascota.getCliente() != null && mascota.getCliente().getId() != null) {
            Cliente cliente = clienteRepository.findById(mascota.getCliente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            mascota.setCliente(cliente);
        }

        petService.guardar(mascota);
        redirectAttributes.addFlashAttribute("success", "Mascota guardada correctamente");
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        if (mascota.getCliente() == null) {
            mascota.setCliente(new Cliente());
        }
        model.addAttribute("mascota", mascota);
        model.addAttribute("clientes", clienteRepository.findAll());
        return "views/mascotas/formulario";
    }

    @GetMapping("/mascotas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        petService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Mascota eliminada correctamente");
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        model.addAttribute("mascota", mascota);
        return "views/mascotas/show";
    }
}