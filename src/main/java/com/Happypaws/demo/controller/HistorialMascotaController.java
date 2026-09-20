package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.Appointment;
import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.model.HistorialMascota;
import com.Happypaws.demo.model.Pet;
import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.ClienteService;
import com.Happypaws.demo.service.HistorialMascotaService;
import com.Happypaws.demo.service.PetService;
import com.Happypaws.demo.service.SupabaseStorageService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // Carpeta logica dentro del bucket de Supabase Storage (no es una ruta en disco).
    private static final String CARPETA_STORAGE = "historial";

    private final HistorialMascotaService historialMascotaService;
    private final PetService petService;
    private final ClienteService clienteService;
    private final AppointmentService appointmentService;
    private final SupabaseStorageService supabaseStorageService;

    public HistorialMascotaController(HistorialMascotaService historialMascotaService,
                                      PetService petService,
                                      ClienteService clienteService,
                                      AppointmentService appointmentService,
                                      SupabaseStorageService supabaseStorageService) {
        this.historialMascotaService = historialMascotaService;
        this.petService = petService;
        this.clienteService = clienteService;
        this.appointmentService = appointmentService;
        this.supabaseStorageService = supabaseStorageService;
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

    
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
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

   @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
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

        if (historial.getCita() != null && historial.getCita().getIdCita() != null) {
            Appointment cita = appointmentService.buscarPorId(historial.getCita().getIdCita())
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
            if (!cita.getMascota().getIdMascota().equals(mascota.getIdMascota())) {
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
            bindingResult.reject("archivo", "No se pudo guardar el archivo adjunto en Supabase Storage");
            cargarFormulario(model, historial, petId, auth);
            return "views/historial/formulario";
        }

        if (historial.getIdHistorial() == null) {
            historialMascotaService.guardar(historial);
        } else {
            HistorialMascota existente = historialMascotaService.buscarPorId(historial.getIdHistorial())
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

    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, Authentication auth) {
        validarColaborador(auth);
        HistorialMascota historial = historialMascotaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de historial no encontrado"));
        Long petId = historial.getMascota().getIdMascota();
        obtenerMascotaAutorizada(petId, auth);
        cargarFormulario(model, historial, petId, auth);
        return "views/historial/formulario";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        validarColaborador(auth);
        HistorialMascota historial = historialMascotaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de historial no encontrado"));
        Long petId = historial.getMascota().getIdMascota();
        obtenerMascotaAutorizada(petId, auth);
        supabaseStorageService.eliminar(historial.getArchivoGuardado());
        historialMascotaService.eliminar(id);
        redirectAttributes.addFlashAttribute("success", "Registro de historial eliminado correctamente");
        return "redirect:/historial/mascota/" + petId;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/archivo/{id}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id, Authentication auth) throws IOException {
        HistorialMascota historial = historialMascotaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro de historial no encontrado"));
        obtenerMascotaAutorizada(historial.getMascota().getIdMascota(), auth);

        if (historial.getArchivoGuardado() == null || historial.getArchivoGuardado().isBlank()) {
            throw new IllegalArgumentException("Este registro no tiene archivo adjunto");
        }

        byte[] contenido = supabaseStorageService.descargar(historial.getArchivoGuardado());

        String nombreDescarga = historial.getArchivoOriginal() != null ? historial.getArchivoOriginal() : "adjunto";
        MediaType tipo = historial.getArchivoTipo() != null
                ? MediaType.parseMediaType(historial.getArchivoTipo())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreDescarga + "\"")
                .contentType(tipo)
                .body(contenido);
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
            if (mascota.getCliente() == null || mascota.getCliente().getIdCliente() == null || !cliente.getIdCliente().equals(mascota.getCliente().getIdCliente())) {
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
        String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "archivo";
        String extension = "";
        int index = nombreOriginal.lastIndexOf('.');
        if (index >= 0) {
            extension = nombreOriginal.substring(index);
        }
        String nombreGuardado = CARPETA_STORAGE + "/" + UUID.randomUUID() + extension;

        supabaseStorageService.subir(nombreGuardado, archivo.getBytes(), archivo.getContentType());

        historial.setArchivoOriginal(nombreOriginal);
        historial.setArchivoGuardado(nombreGuardado);
        historial.setArchivoTipo(archivo.getContentType());
        historial.setArchivoTamanio(archivo.getSize());
    }
}
