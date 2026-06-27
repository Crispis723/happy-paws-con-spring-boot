package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Proveedor;
import com.Happypaws.demo.service.ProveedorService;
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
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorService.listar());
        return "views/proveedores/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "views/proveedores/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("proveedor") Proveedor proveedor, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "views/proveedores/formulario";
        }
        if (proveedor.getId() == null) {
            proveedorService.guardar(proveedor);
        } else {
            proveedorService.actualizar(proveedor);
        }
        redirectAttributes.addFlashAttribute("success", "Proveedor guardado correctamente");
        return "redirect:/proveedores";
    }

    @GetMapping("/edit/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
        model.addAttribute("proveedor", proveedor);
        return "views/proveedores/formulario";
    }

    @GetMapping("/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        proveedorService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Proveedor eliminado correctamente");
        return "redirect:/proveedores";
    }
}