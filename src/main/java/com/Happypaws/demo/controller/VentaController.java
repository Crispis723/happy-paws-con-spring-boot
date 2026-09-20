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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public String listar(
            Model model,
            Authentication auth,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        boolean isClientUser = auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClientUser) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            List<Venta> ventasCliente = ventaService.listarPorClienteId(cliente.getIdCliente());
            model.addAttribute("ventas", ventasCliente);
            model.addAttribute("isClientUser", true);
            model.addAttribute("clienteNombre", cliente.getRazonSocial());
            // El historial de compras de un cliente individual no crece lo
            // suficiente como para justificar paginación; se muestra
            // completo, pero se informan estos atributos igual para que
            // el fragmento de paginación (que espera que existan) se
            // oculte automáticamente (totalPages <= 1).
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            model.addAttribute("totalItems", ventasCliente.size());
            model.addAttribute("pageSize", size);
        } else {
            Page<Venta> resultado = ventaService.listar(
                    PageRequest.of(Math.max(page, 0), size, Sort.by("fecha").descending())
            );
            model.addAttribute("ventas", resultado.getContent());
            model.addAttribute("isClientUser", false);
            model.addAttribute("currentPage", resultado.getNumber());
            model.addAttribute("totalPages", resultado.getTotalPages());
            model.addAttribute("totalItems", resultado.getTotalElements());
            model.addAttribute("pageSize", size);
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

        /*
         * SEGURIDAD (control de acceso a nivel de recurso):
         * Si es una actualización (venta.getIdVenta() viene informado) y
         * el usuario es CLIENTE, hay que confirmar que esa venta YA era
         * suya antes de tocarla. edit()/delete()/show() ya llaman a
         * validarPropietario, pero guardar() (este método) no lo hacía,
         * así que un cliente podía enviar el id de la venta de OTRO
         * cliente y sobrescribir sus productos/cantidades.
         */
        if (isClientUser && venta.getIdVenta() != null) {

            Venta ventaExistente = ventaService.buscarPorId(venta.getIdVenta())
                    .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

            validarPropietario(ventaExistente, auth);
        }

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
