package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.HistorialMascota;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.CompraService;
import com.Happypaws.demo.service.HistorialMascotaService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.ProductoService;
import com.Happypaws.demo.service.VentaService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ClienteService clienteService;
    private final PetService petService;
    private final AppointmentService appointmentService;
    private final HistorialMascotaService historialMascotaService;
    private final ProductoService productoService;
    private final CompraService compraService;
    private final VentaService ventaService;

    public DashboardController(ClienteService clienteService,
                               PetService petService,
                               AppointmentService appointmentService,
                               HistorialMascotaService historialMascotaService,
                               ProductoService productoService,
                               CompraService compraService,
                               VentaService ventaService) {
        this.clienteService = clienteService;
        this.petService = petService;
        this.appointmentService = appointmentService;
        this.historialMascotaService = historialMascotaService;
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
                LocalDate hoy = LocalDate.now();
                LocalDate limiteCitas = hoy.plusDays(7);
                LocalDate limiteVencimiento = hoy.plusDays(30);

                List<Appointment> proximasCitas = appointmentService.listarPorClienteId(cliente.getId()).stream()
                        .filter(cita -> cita.getFecha() != null && !cita.getFecha().isBefore(hoy) && !cita.getFecha().isAfter(limiteCitas))
                        .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                        .toList();

                List<HistorialMascota> vencimientos = historialMascotaService
                        .listarVencimientosPorCliente(cliente.getId(), hoy, limiteVencimiento)
                        .stream()
                        .filter(item -> item.getArchivoGuardado() != null && !item.getArchivoGuardado().isBlank())
                        .toList();

                model.addAttribute("proximasCitas", proximasCitas);
                model.addAttribute("vencimientosHistorial", vencimientos);
                model.addAttribute("cantidadRecordatorios", proximasCitas.size() + vencimientos.size());
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

    /**
     * Exporta los datos principales del sistema (clientes, mascotas, citas,
     * productos, compras y ventas) como varios CSV dentro de un .zip.
     * Solo el personal del negocio puede descargar esto (no CLIENTE), ya que
     * incluye datos de contacto y ventas de todos los clientes.
     */
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO','RECEPCIONISTA')")
    @GetMapping("/dashboard/exportar")
    public ResponseEntity<byte[]> exportarDatos() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (ZipOutputStream zip = new ZipOutputStream(buffer, StandardCharsets.UTF_8)) {

            agregarCsv(zip, "clientes.csv",
                    List.of("ID", "Tipo Documento", "Numero Documento", "Razon Social", "Direccion", "Telefono", "Email"),
                    clienteService.listar(),
                    c -> List.of(
                            String.valueOf(c.getId()),
                            valorOVacio(c.getDocumentoTipoCodigo()),
                            valorOVacio(c.getNumeroDocumento()),
                            valorOVacio(c.getRazonSocial()),
                            valorOVacio(c.getDireccion()),
                            valorOVacio(c.getTelefono()),
                            valorOVacio(c.getEmail())
                    ));

            agregarCsv(zip, "mascotas.csv",
                    List.of("ID", "Nombre", "Especie", "Raza", "Edad", "Cliente"),
                    petService.listar(),
                    p -> List.of(
                            String.valueOf(p.getId()),
                            valorOVacio(p.getNombre()),
                            valorOVacio(p.getEspecie()),
                            valorOVacio(p.getRaza()),
                            p.getEdad() != null ? String.valueOf(p.getEdad()) : "",
                            valorOVacio(p.getClienteNombre())
                    ));

            agregarCsv(zip, "citas.csv",
                    List.of("ID", "Mascota", "Cliente", "Veterinario", "Fecha", "Motivo"),
                    appointmentService.listar(),
                    a -> List.of(
                            String.valueOf(a.getId()),
                            a.getMascota() != null ? valorOVacio(a.getMascota().getNombre()) : "",
                            a.getCliente() != null ? valorOVacio(a.getCliente().getRazonSocial()) : "",
                            valorOVacio(a.getVeterinarioNombre()),
                            a.getFecha() != null ? a.getFecha().toString() : "",
                            valorOVacio(a.getMotivo())
                    ));

            agregarCsv(zip, "productos.csv",
                    List.of("ID", "Codigo", "Nombre", "Precio Unitario", "Stock"),
                    productoService.listar(),
                    p -> List.of(
                            String.valueOf(p.getId()),
                            valorOVacio(p.getCodigo()),
                            valorOVacio(p.getNombre()),
                            p.getPrecioUnitario() != null ? p.getPrecioUnitario().toString() : "",
                            p.getStock() != null ? String.valueOf(p.getStock()) : ""
                    ));

            agregarCsv(zip, "compras.csv",
                    List.of("ID", "Numero", "Fecha", "Proveedor", "Estado", "Total"),
                    compraService.listar(),
                    c -> List.of(
                            String.valueOf(c.getId()),
                            valorOVacio(c.getNumero()),
                            c.getFecha() != null ? c.getFecha().toString() : "",
                            valorOVacio(c.getProveedorNombre()),
                            valorOVacio(c.getEstado()),
                            c.getTotal() != null ? c.getTotal().toString() : ""
                    ));

            agregarCsv(zip, "ventas.csv",
                    List.of("ID", "Numero", "Fecha", "Cliente", "Estado", "Total"),
                    ventaService.listar(),
                    v -> List.of(
                            String.valueOf(v.getId()),
                            valorOVacio(v.getNumero()),
                            v.getFecha() != null ? v.getFecha().toString() : "",
                            valorOVacio(v.getClienteNombre()),
                            valorOVacio(v.getEstado()),
                            v.getTotal() != null ? v.getTotal().toString() : ""
                    ));
        }

        String nombreArchivo = "happy-paws-datos-" + LocalDate.now() + ".zip";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(buffer.toByteArray());
    }

    private <T> void agregarCsv(ZipOutputStream zip, String nombreArchivo, List<String> encabezados,
                                List<T> items, Function<T, List<String>> mapeador) throws IOException {
        zip.putNextEntry(new ZipEntry(nombreArchivo));

        // BOM UTF-8 para que Excel muestre tildes/eñes correctamente al abrir el CSV.
        zip.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});

        Writer writer = new OutputStreamWriter(zip, StandardCharsets.UTF_8);
        writer.write(filaCsv(encabezados));

        for (T item : items) {
            writer.write(filaCsv(mapeador.apply(item)));
        }

        writer.flush();
        zip.closeEntry();
    }

    private String filaCsv(List<String> valores) {
        StringBuilder fila = new StringBuilder();
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) {
                fila.append(',');
            }
            fila.append(csvEscape(valores.get(i)));
        }
        fila.append("\r\n");
        return fila.toString();
    }

    private String csvEscape(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    private String valorOVacio(String valor) {
        return valor != null ? valor : "";
    }
}
