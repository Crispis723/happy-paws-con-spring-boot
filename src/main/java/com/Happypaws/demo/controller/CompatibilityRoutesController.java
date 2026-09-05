package com.Happypaws.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Compatibility redirects for legacy/template URLs.
 * Keeps old links working while the canonical controllers use their current paths.
 */
@Controller
public class CompatibilityRoutesController {

    @GetMapping("/historial")
    public String historialRoot() {
        return "redirect:/mascotas";
    }

    @GetMapping("/historial/")
    public String historialRootSlash() {
        return "redirect:/mascotas";
    }

    @GetMapping("/reportes")
    public String reportesRoot() {
        return "redirect:/reportes/financieros";
    }

    @GetMapping("/reportes/")
    public String reportesRootSlash() {
        return "redirect:/reportes/financieros";
    }

    @GetMapping("/clientes/editar/{id}")
    public String clienteEditar(@PathVariable Long id) {
        return "redirect:/clientes/edit/" + id;
    }

    @GetMapping("/clientes/eliminar/{id}")
    public String clienteEliminar(@PathVariable Long id) {
        return "redirect:/clientes/delete/" + id;
    }

    @GetMapping("/mascotas/editar/{id}")
    public String mascotaEditar(@PathVariable Long id) {
        return "redirect:/mascotas/edit/" + id;
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String mascotaEliminar(@PathVariable Long id) {
        return "redirect:/mascotas/delete/" + id;
    }

    @GetMapping("/citas/nuevo")
    public String citaNuevo() {
        return "redirect:/citas/create";
    }

    @GetMapping("/citas/crear")
    public String citaCrear() {
        return "redirect:/citas/create";
    }

    @GetMapping("/citas/editar/{id}")
    public String citaEditar(@PathVariable Long id) {
        return "redirect:/citas/edit/" + id;
    }

    @GetMapping("/citas/eliminar/{id}")
    public String citaEliminar(@PathVariable Long id) {
        return "redirect:/citas/delete/" + id;
    }

    @GetMapping("/productos/nuevo")
    public String productoNuevo() {
        return "redirect:/productos/create";
    }

    @GetMapping("/productos/crear")
    public String productoCrear() {
        return "redirect:/productos/create";
    }

    @GetMapping("/productos/editar/{id}")
    public String productoEditar(@PathVariable Long id) {
        return "redirect:/productos/edit/" + id;
    }

    @GetMapping("/productos/eliminar/{id}")
    public String productoEliminar(@PathVariable Long id) {
        return "redirect:/productos/delete/" + id;
    }

    @GetMapping("/compras/nueva")
    public String compraNueva() {
        return "redirect:/compras/create";
    }

    @GetMapping("/compras/crear")
    public String compraCrear() {
        return "redirect:/compras/create";
    }

    @GetMapping("/compras/editar/{id}")
    public String compraEditar(@PathVariable Long id) {
        return "redirect:/compras/edit/" + id;
    }

    @GetMapping("/compras/eliminar/{id}")
    public String compraEliminar(@PathVariable Long id) {
        return "redirect:/compras/delete/" + id;
    }

    @GetMapping("/proveedores/editar/{id}")
    public String proveedorEditar(@PathVariable Long id) {
        return "redirect:/proveedores/edit/" + id;
    }

    @GetMapping("/proveedores/eliminar/{id}")
    public String proveedorEliminar(@PathVariable Long id) {
        return "redirect:/proveedores/delete/" + id;
    }

    @GetMapping("/ventas/nueva")
    public String ventaNueva() {
        return "redirect:/ventas/create";
    }

    @GetMapping("/ventas/crear")
    public String ventaCrear() {
        return "redirect:/ventas/create";
    }

    @GetMapping("/ventas/editar/{id}")
    public String ventaEditar(@PathVariable Long id) {
        return "redirect:/ventas/edit/" + id;
    }

    @GetMapping("/ventas/eliminar/{id}")
    public String ventaEliminar(@PathVariable Long id) {
        return "redirect:/ventas/delete/" + id;
    }
}
