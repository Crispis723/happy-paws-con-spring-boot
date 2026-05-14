package com.Happypaws.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/citas"})
    public String citas() {
        return "views/citas/index";
    }

    @GetMapping({"/citas/create"})
    public String crearCita() {
        return "views/citas/create";
    }

    @GetMapping({"/productos"})
    public String productos() {
        return "views/productos/index";
    }

    @GetMapping({"/compras"})
    public String compras() {
        return "views/compras/index";
    }

    @GetMapping({"/ventas"})
    public String ventas() {
        return "views/ventas/index";
    }

    @GetMapping({"/proveedores"})
    public String proveedores() {
        return "views/proveedores/index";
    }

    @GetMapping({"/roles"})
    public String roles() {
        return "views/roles/index";
    }

    @GetMapping({"/unidades"})
    public String unidades() {
        return "views/unidades/index";
    }

    @GetMapping({"/afectacion-tipos"})
    public String afectacionTipos() {
        return "views/afectacion-tipos/index";
    }

    @GetMapping({"/documento-tipos"})
    public String documentoTipos() {
        return "views/documento-tipos/index";
    }

    @GetMapping({"/comprobante-series"})
    public String comprobanteSeries() {
        return "views/comprobante-series/index";
    }

    @GetMapping({"/comprobante-tipos"})
    public String comprobanteTipos() {
        return "views/comprobante-tipos/index";
    }
}
