package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.SystemSettingService;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {

    private final SystemSettingService systemSettingService;

    public AdminSettingsController(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("precio", systemSettingService.obtenerPrecioCita());
        return "views/admin/settings/index";
    }

    @GetMapping("/cita_precio")
    public String precioCita(Model model) {
        model.addAttribute("precio", systemSettingService.obtenerPrecioCita());
        return "views/admin/settings/cita_precio";
    }

    @PostMapping("/cita_precio")
    public String guardarPrecioCita(@RequestParam("cita_precio") @DecimalMin(value = "0.00") BigDecimal precio,
                                    RedirectAttributes redirectAttributes) {
        BigDecimal guardado = systemSettingService.guardarPrecioCita(precio);
        redirectAttributes.addFlashAttribute("success", "Precio de cita actualizado a S/ " + guardado.toPlainString());
        return "redirect:/admin/settings/cita_precio";
    }
}