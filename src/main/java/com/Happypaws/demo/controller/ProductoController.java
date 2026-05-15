package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Producto;
import com.Happypaws.demo.service.AfectacionTipoService;
import com.Happypaws.demo.service.ProductoService;
import com.Happypaws.demo.service.UnidadService;
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
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final UnidadService unidadService;
    private final AfectacionTipoService afectacionTipoService;

    public ProductoController(ProductoService productoService, UnidadService unidadService, AfectacionTipoService afectacionTipoService) {
        this.productoService = productoService;
        this.unidadService = unidadService;
        this.afectacionTipoService = afectacionTipoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "views/productos/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("unidades", unidadService.listar());
        model.addAttribute("afectacionTipos", afectacionTipoService.listar());
        return "views/productos/create";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("producto") Producto producto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("unidades", unidadService.listar());
            model.addAttribute("afectacionTipos", afectacionTipoService.listar());
            return producto.getId() == null ? "views/productos/create" : "views/productos/edit";
        }
        if (producto.getId() == null) {
            productoService.guardar(producto);
        } else {
            productoService.actualizar(producto);
        }
        redirectAttributes.addFlashAttribute("success", "Producto guardado correctamente");
        return "redirect:/productos";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        model.addAttribute("unidades", unidadService.listar());
        model.addAttribute("afectacionTipos", afectacionTipoService.listar());
        return "views/productos/edit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productoService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente");
        return "redirect:/productos";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        model.addAttribute("producto", producto);
        return "views/productos/show";
    }
}