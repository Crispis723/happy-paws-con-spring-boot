package com.Happypaws.demo.controller;

import com.Happypaws.demo.service.AppointmentService;
import com.Happypaws.demo.service.VentaService;
import com.Happypaws.demo.model.Venta;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    private final VentaService ventaService;
    private final AppointmentService appointmentService;

    public ReportesController(VentaService ventaService, AppointmentService appointmentService) {
        this.ventaService = ventaService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/ventas")
    public String ventas(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                         Model model) {
        List<Venta> ventas = ventaService.listar().stream()
            .filter(venta -> fechaInicio == null || !venta.getFecha().isBefore(fechaInicio))
            .filter(venta -> fechaFin == null || !venta.getFecha().isAfter(fechaFin))
            .toList();

        model.addAttribute("ventas", ventas);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("totalVentas", ventas.size());
        model.addAttribute("totalImporte", ventas.stream()
                .map(venta -> venta.getTotal() == null ? java.math.BigDecimal.ZERO : venta.getTotal())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        return "views/reportes/ventas";
    }

    @GetMapping("/medicos")
    public String medicos(Model model) {
        model.addAttribute("totalCitas", appointmentService.listar().size());
        return "views/reportes/medicos";
    }

    @GetMapping("/financieros")
    public String financieros(Model model) {
        model.addAttribute("ventas", ventaService.listar());
        model.addAttribute("totalVentas", ventaService.listar().size());
        model.addAttribute("totalImporte", ventaService.listar().stream()
                .map(venta -> venta.getTotal() == null ? java.math.BigDecimal.ZERO : venta.getTotal())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        return "views/reportes/financieros";
    }
}