package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.service.ClienteService;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "views/clientes/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "views/clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "views/clientes/formulario";
        }
        if (cliente.getId() == null) {
            clienteService.guardar(cliente);
        } else {
            clienteService.actualizar(cliente);
        }
        redirectAttributes.addFlashAttribute("success", "Cliente guardado correctamente");
        return "redirect:/clientes";
    }

    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        model.addAttribute("cliente", cliente);
        return "views/clientes/formulario";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente");
        return "redirect:/clientes";
    }
}