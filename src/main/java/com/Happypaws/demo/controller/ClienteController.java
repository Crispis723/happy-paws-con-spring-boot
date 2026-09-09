package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.DocumentoTipoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final DocumentoTipoService documentoTipoService;

    public ClienteController(ClienteService clienteService, DocumentoTipoService documentoTipoService) {
        this.clienteService = clienteService;
        this.documentoTipoService = documentoTipoService;
    }

    @GetMapping
    public String listar(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Page<Cliente> resultado = clienteService.listar(
                PageRequest.of(Math.max(page, 0), size, Sort.by("razonSocial").ascending())
        );

        model.addAttribute("clientes", resultado.getContent());
        model.addAttribute("currentPage", resultado.getNumber());
        model.addAttribute("totalPages", resultado.getTotalPages());
        model.addAttribute("totalItems", resultado.getTotalElements());
        model.addAttribute("pageSize", size);

        return "views/clientes/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("documentoTipos", documentoTipoService.listar());
        return "views/clientes/formulario";
    }

    /** Compatibilidad con formularios antiguos que enviaban POST a /clientes. */
    @PostMapping
    public String guardarCompatibilidad(@Valid @ModelAttribute("cliente") Cliente cliente,
                                        BindingResult bindingResult,
                                        Model model,
                                        RedirectAttributes redirectAttributes) {
        return guardar(cliente, bindingResult, model, redirectAttributes);
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("documentoTipos", documentoTipoService.listar());
            return "views/clientes/formulario";
        }
        if (cliente.getIdCliente() == null) {
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
        model.addAttribute("documentoTipos", documentoTipoService.listar());
        return "views/clientes/formulario";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente");
        return "redirect:/clientes";
    }
}