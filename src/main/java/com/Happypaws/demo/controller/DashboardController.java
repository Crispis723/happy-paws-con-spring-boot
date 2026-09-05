package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.CompraService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.ProductoService;
import com.Happypaws.demo.service.ProveedorService;
import com.Happypaws.demo.service.UserService;
import com.Happypaws.demo.service.VentaService;
import java.nio.charset.StandardCharsets;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ClienteService clienteService;
    private final PetService petService;
    private final AppointmentService appointmentService;
    private final ProductoService productoService;
    private final CompraService compraService;
    private final VentaService ventaService;
    private final ProveedorService proveedorService;
    private final UserService userService;

    public DashboardController(
            ClienteService clienteService,
            PetService petService,
            AppointmentService appointmentService,
            ProductoService productoService,
            CompraService compraService,
            VentaService ventaService,
            ProveedorService proveedorService,
            UserService userService) {
        this.clienteService = clienteService;
        this.petService = petService;
        this.appointmentService = appointmentService;
        this.productoService = productoService;
        this.compraService = compraService;
        this.ventaService = ventaService;
        this.proveedorService = proveedorService;
        this.userService = userService;
    }

    @GetMapping
    public String index(Model model, Authentication authentication) {
        addDashboardData(model, authentication);
        return "views/dashboard/index";
    }

    @GetMapping("/colaboradores")
    public String colaboradores(Model model, Authentication authentication) {
        addDashboardData(model, authentication);
        return "views/dashboard/staff";
    }

    @GetMapping("/cliente")
    public String cliente(Model model, Authentication authentication) {
        model.addAttribute("nombreUsuario", authentication != null ? authentication.getName() : "cliente");
        return "views/dashboard/public";
    }

    /** Exportación sencilla y estable para el botón del dashboard. */
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar() {
        String csv = "modulo,total\n"
                + "clientes," + clienteService.listar().size() + "\n"
                + "mascotas," + petService.listar().size() + "\n"
                + "citas," + appointmentService.listar().size() + "\n"
                + "productos," + productoService.listar().size() + "\n"
                + "compras," + compraService.listar().size() + "\n"
                + "ventas," + ventaService.listar().size() + "\n"
                + "proveedores," + proveedorService.listar().size() + "\n"
                + "usuarios," + userService.listar().size() + "\n";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename("happy-paws-resumen.csv", StandardCharsets.UTF_8).build());

        return ResponseEntity.ok().headers(headers).body(csv.getBytes(StandardCharsets.UTF_8));
    }

    private void addDashboardData(Model model, Authentication authentication) {
        model.addAttribute("nombreUsuario", authentication != null ? authentication.getName() : "usuario");
        model.addAttribute("totalClientes", clienteService.listar().size());
        model.addAttribute("totalMascotas", petService.listar().size());
        model.addAttribute("totalCitas", appointmentService.listar().size());
        model.addAttribute("totalProductos", productoService.listar().size());
        model.addAttribute("totalCompras", compraService.listar().size());
        model.addAttribute("totalVentas", ventaService.listar().size());
        model.addAttribute("totalProveedores", proveedorService.listar().size());
        model.addAttribute("totalUsuarios", userService.listar().size());
    }
}
