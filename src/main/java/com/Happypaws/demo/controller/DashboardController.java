package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.CompraService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.ProductoService;
import com.Happypaws.demo.service.VentaService;
import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class DashboardController {

    private final ClienteService clienteService;
    private final PetService petService;
    private final AppointmentService appointmentService;
    private final ProductoService productoService;
    private final CompraService compraService;
    private final VentaService ventaService;

    public DashboardController(ClienteService clienteService,
                               PetService petService,
                               AppointmentService appointmentService,
                               ProductoService productoService,
                               CompraService compraService,
                               VentaService ventaService) {
        this.clienteService = clienteService;
        this.petService = petService;
        this.appointmentService = appointmentService;
        this.productoService = productoService;
        this.compraService = compraService;
        this.ventaService = ventaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        boolean isClient = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));
        return isClient ? "redirect:/dashboard/cliente" : "redirect:/dashboard/colaboradores";
    }

    @GetMapping("/dashboard/cliente")
    public String clientDashboard(Authentication authentication, Model model, Principal principal) {
        boolean isClient = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClient) {
            String email = principal != null ? principal.getName() : null;
            model.addAttribute("nombreUsuario", email != null ? email : "Cliente");
            if (email != null) {
                var cliente = clienteService.resolverOCrearClienteAutenticado(email, email);
                model.addAttribute("mascotas", petService.listarPorClienteId(cliente.getId()));
            }
            return "views/dashboard/public";
        }

        return "redirect:/dashboard/colaboradores";
    }

    @GetMapping("/dashboard/colaboradores")
    public String collaboratorDashboard(Authentication authentication, Model model, Principal principal) {
        boolean isClient = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        if (isClient) {
            return "redirect:/dashboard/cliente";
        }

        model.addAttribute("nombreUsuario", principal != null ? principal.getName() : "Usuario");
        model.addAttribute("totalClientes", clienteService.listar().size());
        model.addAttribute("totalMascotas", petService.listar().size());
        model.addAttribute("totalCitas", appointmentService.listar().size());
        model.addAttribute("totalProductos", productoService.listar().size());
        model.addAttribute("totalCompras", compraService.listar().size());
        model.addAttribute("totalVentas", ventaService.listar().size());
        return "views/dashboard/staff";
    }

}
