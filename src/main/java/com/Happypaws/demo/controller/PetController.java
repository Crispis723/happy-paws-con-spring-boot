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

    @GetMapping("/mascotas")
    public String listar(Model model, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            model.addAttribute("mascotas", petService.listarPorClienteId(cliente.getId()));
            model.addAttribute("isClientUser", true);
            model.addAttribute("clienteNombre", cliente.getRazonSocial());
        } else {
            model.addAttribute("mascotas", petService.listar());
            model.addAttribute("isClientUser", false);
        }
        return "views/mascotas/index";
    }

    @GetMapping("/mascotas/nuevo")
    public String nuevo(Model model, Authentication auth) {
        Pet mascota = new Pet();
        model.addAttribute("mascota", mascota);
        aplicarContextoCliente(model, mascota, auth);
        return "views/mascotas/formulario";
    }

    @PostMapping("/mascotas/guardar")
    public String guardar(@Valid @ModelAttribute("mascota") Pet mascota, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            mascota.setCliente(cliente);
        }

        if (bindingResult.hasErrors()) {
            aplicarContextoCliente(model, mascota, auth);
            return "views/mascotas/formulario";
        }
        if (mascota.getCliente() != null && mascota.getCliente().getId() != null) {
            Cliente cliente = clienteService.buscarPorId(mascota.getCliente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
            mascota.setCliente(cliente);
        }

        if (mascota.getId() == null) {
            petService.guardar(mascota);
        } else {
            petService.actualizar(mascota);
        }
        redirectAttributes.addFlashAttribute("success", "Mascota guardada correctamente");
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/edit/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        validarPropietario(mascota, auth);
        if (mascota.getCliente() == null) {
            mascota.setCliente(new Cliente());
        }
        model.addAttribute("mascota", mascota);
        aplicarContextoCliente(model, mascota, auth);
        return "views/mascotas/formulario";
    }

    @GetMapping("/mascotas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        validarPropietario(mascota, auth);
        petService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Mascota eliminada correctamente");
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/{id}")
    public String ver(@PathVariable Long id, Model model, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));
        if (isClientUser) {
            return "redirect:/mascotas";
        }
        Pet mascota = petService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        validarPropietario(mascota, auth);
        model.addAttribute("mascota", mascota);
        return "views/mascotas/show";
    }

    private void aplicarContextoCliente(Model model, Pet mascota, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            mascota.setCliente(cliente);
            model.addAttribute("isClientUser", true);
            model.addAttribute("clienteNombre", cliente.getRazonSocial());
            model.addAttribute("clienteId", cliente.getId());
            model.addAttribute("clientes", null);
        } else {
            model.addAttribute("isClientUser", false);
            model.addAttribute("clientes", clienteService.listar());
        }
    }

    private void validarPropietario(Pet mascota, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            if (mascota.getCliente() == null || mascota.getCliente().getId() == null || !cliente.getId().equals(mascota.getCliente().getId())) {
                throw new IllegalArgumentException("No tienes permiso para ver o modificar esta mascota");
            }
        }
    }
}