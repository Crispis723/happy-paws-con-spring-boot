package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Compra;
import com.Happypaws.demo.model.Proveedor;
import com.Happypaws.demo.service.CompraService;
import com.Happypaws.demo.service.ComprobanteTipoService;
import com.Happypaws.demo.service.ProveedorService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
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
@RequestMapping("/compras")
public class CompraController {

    private final CompraService compraService;
    private final ProveedorService proveedorService;
    private final ComprobanteTipoService comprobanteTipoService;
    private final AtomicLong numeroSeed = new AtomicLong(1);

    public CompraController(CompraService compraService, ProveedorService proveedorService, ComprobanteTipoService comprobanteTipoService) {
        this.compraService = compraService;
        this.proveedorService = proveedorService;
        this.comprobanteTipoService = comprobanteTipoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("compras", compraService.listar());
        return "views/compras/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Compra compra = new Compra();
        compra.setFecha(LocalDate.now());
        compra.setFormaPago("efectivo");
        compra.setEstado("registrada");
        compra.setTotal(BigDecimal.ZERO);
        compra.setNumero(generarNumero());
        compra.setProveedor(new Proveedor());
        model.addAttribute("compra", compra);
        model.addAttribute("proveedores", proveedorService.listar());
        model.addAttribute("comprobanteTipos", comprobanteTipoService.listar());
        return "views/compras/create";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("compra") Compra compra, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("proveedores", proveedorService.listar());
            model.addAttribute("comprobanteTipos", comprobanteTipoService.listar());
            return "views/compras/create";
        }

        if (compra.getProveedor() != null && compra.getProveedor().getId() != null) {
            compra.setProveedor(proveedorService.buscarPorId(compra.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado")));
        }

        if (compra.getId() == null) {
            if (compra.getNumero() == null || compra.getNumero().isBlank()) {
                compra.setNumero(generarNumero());
            }
            compraService.guardar(compra);
        } else {
            compraService.actualizar(compra);
        }

        redirectAttributes.addFlashAttribute("success", "Compra guardada correctamente");
        return "redirect:/compras";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Compra compra = compraService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));
        if (compra.getProveedor() == null) {
            compra.setProveedor(new Proveedor());
        }
        model.addAttribute("compra", compra);
        model.addAttribute("proveedores", proveedorService.listar());
        model.addAttribute("comprobanteTipos", comprobanteTipoService.listar());
        return "views/compras/edit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        compraService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Compra eliminada correctamente");
        return "redirect:/compras";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Compra compra = compraService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra no encontrada"));
        model.addAttribute("compra", compra);
        model.addAttribute("empresa", new EmpresaView("Happy Paws", "Av. Principal 123", "20123456789"));
        return "views/compras/show";
    }

    private String generarNumero() {
        return "C" + String.format("%05d", numeroSeed.getAndIncrement());
    }

    private record EmpresaView(String razonSocial, String direccion, String ruc) {
    }
}