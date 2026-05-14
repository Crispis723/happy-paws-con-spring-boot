package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.Venta;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.VentaService;
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
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final AtomicLong numeroSeed = new AtomicLong(1);

    public VentaController(VentaService ventaService, ClienteService clienteService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        return "views/ventas/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setFormaPago("efectivo");
        venta.setEstado("registrada");
        venta.setTotal(BigDecimal.ZERO);
        venta.setNumero(generarNumero());
        venta.setCliente(new Cliente());
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.listar());
        return "views/ventas/action";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("venta") Venta venta, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clientes", clienteService.listar());
            return venta.getId() == null ? "views/ventas/action" : "views/ventas/action";
        }

        if (venta.getCliente() != null && venta.getCliente().getId() != null) {
            venta.setCliente(clienteService.buscarPorId(venta.getCliente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado")));
        }

        if (venta.getId() == null) {
            if (venta.getNumero() == null || venta.getNumero().isBlank()) {
                venta.setNumero(generarNumero());
            }
            ventaService.guardar(venta);
        } else {
            ventaService.actualizar(venta);
        }

        redirectAttributes.addFlashAttribute("success", "Venta guardada correctamente");
        return "redirect:/ventas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Venta venta = ventaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        if (venta.getCliente() == null) {
            venta.setCliente(new Cliente());
        }
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.listar());
        return "views/ventas/action";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ventaService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Venta eliminada correctamente");
        return "redirect:/ventas";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Venta venta = ventaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        model.addAttribute("venta", venta);
        model.addAttribute("empresa", new EmpresaView("Happy Paws", "Av. Principal 123", "20123456789"));
        return "views/ventas/ticket";
    }

    private String generarNumero() {
        return "V" + String.format("%05d", numeroSeed.getAndIncrement());
    }

    private record EmpresaView(String razonSocial, String direccion, String ruc) {
    }
}