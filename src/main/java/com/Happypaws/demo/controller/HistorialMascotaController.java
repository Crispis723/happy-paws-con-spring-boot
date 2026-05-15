package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.HistorialMascota;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.HistorialMascotaService;
import com.Happypaws.demo.service.PetService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/historial")
public class HistorialMascotaController {

    private final Path historialUploadDir;

    private final HistorialMascotaService historialMascotaService;
    private final PetService petService;
    private final ClienteService clienteService;
    private final AppointmentService appointmentService;

    public HistorialMascotaController(HistorialMascotaService historialMascotaService,
                                      PetService petService,
                                      ClienteService clienteService,
                                      AppointmentService appointmentService,
                                      @Value("${app.storage.path:uploads}") String storagePath) {
        this.historialMascotaService = historialMascotaService;
        this.petService = petService;
        this.clienteService = clienteService;
        this.appointmentService = appointmentService;
        this.historialUploadDir = Paths.get(storagePath, "historial");
    }

    @GetMapping("/mascota/{petId}")
    public String listar(@PathVariable Long petId, Model model, Authentication auth) {
        Pet mascota = obtenerMascotaAutorizada(petId, auth);
        List<HistorialMascota> historial = historialMascotaService.listarPorMascotaId(petId);
        model.addAttribute("mascota", mascota);
        model.addAttribute("historiales", historial);
        model.addAttribute("isClientUser", esCliente(auth));
        return "views/historial/index";
    }

    @GetMapping("/mascota/{petId}/nuevo")
    public String nuevo(@PathVariable Long petId, Model model, Authentication auth) {
        validarColaborador(auth);
        Pet mascota = obtenerMascotaAutorizada(petId, auth);
        HistorialMascota historial = new HistorialMascota();
        historial.setMascota(mascota);
        historial.setFechaRegistro(LocalDate.now());
        cargarFormulario(model, historial, petId, auth);
        return "views/historial/formulario";
    }

    @PostMapping("/mascota/{petId}/guardar")
    public String guardar(@PathVariable Long petId,
                          @Valid @ModelAttribute("historial") HistorialMascota historial,
                          BindingResult bindingResult,
                          @RequestParam(name = "archivo", required = false) MultipartFile archivo,
                          Model model,
                          Authentication auth,
                          RedirectAttributes redirectAttributes) {
        validarColaborador(auth);
        Pet mascota = obtenerMascotaAutorizada(petId, auth);
        historial.setMascota(mascota);

        if (historial.getCita() != null && historial.getCita().getId() != null) {
            Appointment cita = appointmentService.buscarPorId(historial.getCita().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
            if (!cita.getMascota().getId().equals(mascota.getId())) {
                throw new IllegalArgumentException("La cita seleccionada no pertenece a esta mascota");
            }
            historial.setCita(cita);
        } else {
            historial.setCita(null);
        }

        if (bindingResult.hasErrors()) {
            cargarFormulario(model, historial, petId, auth);
            return "views/historial/formulario";
        }

        try {
            if (archivo != null && !archivo.isEmpty()) {
                guardarArchivo(historial, archivo);
            }
        } catch (IOException ex) {
            bindingResult.reject("archivo", "No se pudo guardar el archivo adjunto");
            cargarFormulario(model, historial, petId, auth);
            return "views/historial/formulario";
        }

        if (historial.getId() == null) {
            historialMascotaService.guardar(historial);
        } else {
            HistorialMascota existente = historialMascotaService.buscarPorId(historial.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Registro de historial no encontrado"));
            if (historial.getArchivoGuardado() == null || historial.getArchivoGuardado().isBlank()) {
                historial.setArchivoGuardado(existente.getArchivoGuardado());
                historial.setArchivoOriginal(existente.getArchivoOriginal());
                historial.setArchivoTipo(existente.getArchivoTipo());
                historial.setArchivoTamanio(existente.getArchivoTamanio());
            }
            historialMascotaService.actualizar(historial);
        }

        redirectAttributes.addFlashAttribute("success", "Historial guardado correctamente. Vigencia de archivo: 1 año.");
        return "redirect:/historial/mascota/" + petId;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        validarColaborador(auth);
        HistorialMascota historial = historialMascotaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de historial no encontrado"));
        Long petId = historial.getMascota().getId();
        obtenerMascotaAutorizada(petId, auth);
        cargarFormulario(model, historial, petId, auth);
        return "views/historial/formulario";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        validarColaborador(auth);
        HistorialMascota historial = historialMascotaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de historial no encontrado"));
        Long petId = historial.getMascota().getId();
        obtenerMascotaAutorizada(petId, auth);
        eliminarArchivoSiExiste(historial.getArchivoGuardado());
        historialMascotaService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Registro de historial eliminado correctamente");
        return "redirect:/historial/mascota/" + petId;
    }

    @GetMapping("/archivo/{id}")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long id, Authentication auth) throws IOException {
        HistorialMascota historial = historialMascotaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de historial no encontrado"));
        obtenerMascotaAutorizada(historial.getMascota().getId(), auth);

        if (historial.getArchivoGuardado() == null || historial.getArchivoGuardado().isBlank()) {
            throw new IllegalArgumentException("Este registro no tiene archivo adjunto");
        }

        Path archivoPath = historialUploadDir.resolve(historial.getArchivoGuardado()).normalize();
        Resource recurso = new UrlResource(archivoPath.toUri());
        if (!recurso.exists() || !recurso.isReadable()) {
            throw new IllegalArgumentException("No se pudo leer el archivo adjunto");
        }

        String nombreDescarga = historial.getArchivoOriginal() != null ? historial.getArchivoOriginal() : "adjunto";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreDescarga + "\"")
                .header(HttpHeaders.CONTENT_TYPE, historial.getArchivoTipo() != null ? historial.getArchivoTipo() : "application/octet-stream")
                .body(recurso);
    }

    private void cargarFormulario(Model model, HistorialMascota historial, Long petId, Authentication auth) {
        Pet mascota = obtenerMascotaAutorizada(petId, auth);
        model.addAttribute("historial", historial);
        model.addAttribute("mascota", mascota);
        model.addAttribute("citasMascota", appointmentService.listarPorMascotaId(petId));
    }

    private Pet obtenerMascotaAutorizada(Long petId, Authentication auth) {
        Pet mascota = petService.buscarPorId(petId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));

        if (esCliente(auth)) {
            Cliente cliente = clienteService.resolverOCrearClienteAutenticado(auth.getName(), auth.getName());
            if (mascota.getCliente() == null || mascota.getCliente().getId() == null || !cliente.getId().equals(mascota.getCliente().getId())) {
                throw new AccessDeniedException("No tienes permiso para consultar el historial de esta mascota");
            }
        }

        return mascota;
    }

    private void validarColaborador(Authentication auth) {
        if (esCliente(auth)) {
            throw new AccessDeniedException("No tienes permisos para modificar el historial clínico");
        }
    }

    private boolean esCliente(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));
    }

    private void guardarArchivo(HistorialMascota historial, MultipartFile archivo) throws IOException {
        Files.createDirectories(historialUploadDir);
        String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "archivo";
        String extension = "";
        int index = nombreOriginal.lastIndexOf('.');
        if (index >= 0) {
            extension = nombreOriginal.substring(index);
        }
        String nombreGuardado = UUID.randomUUID() + extension;
        Path destino = historialUploadDir.resolve(nombreGuardado).normalize();
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        historial.setArchivoOriginal(nombreOriginal);
        historial.setArchivoGuardado(nombreGuardado);
        historial.setArchivoTipo(archivo.getContentType());
        historial.setArchivoTamanio(archivo.getSize());
    }

    private void eliminarArchivoSiExiste(String archivoGuardado) {
        if (archivoGuardado == null || archivoGuardado.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(historialUploadDir.resolve(archivoGuardado).normalize());
        } catch (IOException ignored) {
            // Si no se puede eliminar físicamente, no bloqueamos la operación de negocio.
        }
    }
}
