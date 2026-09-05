package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.Venta;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.ProductoService;
import com.Happypaws.demo.service.VentaService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final AtomicLong numeroSeed = new AtomicLong(1);

    public VentaController(VentaService ventaService, ClienteService clienteService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            model.addAttribute("ventas", ventaService.listarPorClienteId(cliente.getIdCliente()));
            model.addAttribute("isClientUser", true);
            model.addAttribute("clienteNombre", cliente.getRazonSocial());
        } else {
            model.addAttribute("ventas", ventaService.listar());
            model.addAttribute("isClientUser", false);
        }
        model.addAttribute("productos", productoService.listar());
        return "views/ventas/index";
    }

    @GetMapping("/create")
    public String create(Model model, Authentication auth) {
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setFormaPago("efectivo");
        venta.setEstado("registrada");
        venta.setTotal(BigDecimal.ZERO);
        venta.setNumero(generarNumero());
        model.addAttribute("venta", venta);
        aplicarContextoCliente(model, venta, auth);
        model.addAttribute("productos", productoService.listar());
        return "views/ventas/action";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("venta") Venta venta, BindingResult bindingResult,
            @RequestParam(value = "productoId", required = false) List<Long> productoIds,
            @RequestParam(value = "cantidad", required = false) List<Integer> cantidades,
            Model model, RedirectAttributes redirectAttributes, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            venta.setCliente(cliente);
        }

        if (bindingResult.hasErrors()) {
            return volverAlFormulario(venta, model, auth, null);
        }

        if (venta.getCliente() != null && venta.getCliente().getIdCliente() != null) {
            venta.setCliente(clienteService.buscarPorId(venta.getCliente().getIdCliente())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado")));
        }

        boolean esNueva = venta.getIdVenta() == null;
        if (esNueva && (venta.getNumero() == null || venta.getNumero().isBlank())) {
            venta.setNumero(generarNumero());
        }

        try {
            ventaService.guardarConDetalle(venta, productoIds, cantidades);
        } catch (IllegalArgumentException ex) {
            return volverAlFormulario(venta, model, auth, ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("success",
                esNueva ? "Venta registrada correctamente" : "Venta actualizada correctamente");
        return "redirect:/ventas";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, Authentication auth) {
        Venta venta = ventaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        validarPropietario(venta, auth);
        if (venta.getCliente() == null) {
            venta.setCliente(new Cliente());
        }
        model.addAttribute("venta", venta);
        aplicarContextoCliente(model, venta, auth);
        model.addAttribute("productos", productoService.listar());
        return "views/ventas/action";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Venta venta = ventaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        validarPropietario(venta, auth);
        ventaService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Venta eliminada correctamente");
        return "redirect:/ventas";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model, Authentication auth) {
        Venta venta = ventaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        validarPropietario(venta, auth);
        model.addAttribute("venta", venta);
        model.addAttribute("empresa", new EmpresaView("Happy Paws", "Av. Principal 123", "20123456789"));
        return "views/ventas/ticket";
    }

    private String volverAlFormulario(Venta venta, Model model, Authentication auth, String mensajeError) {
        aplicarContextoCliente(model, venta, auth);
        model.addAttribute("productos", productoService.listar());
        if (mensajeError != null) {
            model.addAttribute("error", mensajeError);
        }
        return "views/ventas/action";
    }

    private void aplicarContextoCliente(Model model, Venta venta, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            venta.setCliente(cliente);
            model.addAttribute("isClientUser", true);
            model.addAttribute("clienteNombre", cliente.getRazonSocial());
            model.addAttribute("clienteId", cliente.getIdCliente());
            model.addAttribute("clientes", null);
        } else {
            model.addAttribute("isClientUser", false);
            model.addAttribute("clientes", clienteService.listar());
        }
    }

    private void validarPropietario(Venta venta, Authentication auth) {
        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            if (venta.getCliente() == null || venta.getCliente().getIdCliente() == null || !cliente.getIdCliente().equals(venta.getCliente().getIdCliente())) {
                throw new IllegalArgumentException("No tienes permiso para ver o modificar esta venta");
            }
        }
    }

    private String generarNumero() {
        return "V" + String.format("%05d", numeroSeed.getAndIncrement());
    }

    private record EmpresaView(String razonSocial, String direccion, String ruc) {
    }
}
