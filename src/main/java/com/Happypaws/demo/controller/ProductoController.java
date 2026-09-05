package com.Happypaws.demo.controller;

import com.Happypaws.demo.model.AfectacionTipo;
import com.Happypaws.demo.model.Producto;
import com.Happypaws.demo.model.Unidad;
import com.Happypaws.demo.service.AfectacionTipoService;
import com.Happypaws.demo.service.ProductoService;
import com.Happypaws.demo.service.UnidadService;

import jakarta.validation.Valid;

import java.io.IOException;

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
@RequestMapping("/productos")
public class ProductoController {

private final ProductoService productoService;
private final UnidadService unidadService;
private final AfectacionTipoService afectacionTipoService;

public ProductoController(
        ProductoService productoService,
        UnidadService unidadService,
        AfectacionTipoService afectacionTipoService) {

    this.productoService = productoService;
    this.unidadService = unidadService;
    this.afectacionTipoService = afectacionTipoService;
}

@GetMapping
public String listar(Model model) {

    model.addAttribute(
            "productos",
            productoService.listar()
    );

    return "views/productos/index";
}

@GetMapping("/create")
public String create(Model model) {

    model.addAttribute(
            "producto",
            new Producto()
    );

    cargarDatosFormulario(model);

    return "views/productos/create";
}

@PostMapping
public String guardar(
        @Valid @ModelAttribute("producto") Producto producto,
        BindingResult bindingResult,
        @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagenArchivo,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) {

        cargarDatosFormulario(model);

        return producto.getIdProducto() == null
                ? "views/productos/create"
                : "views/productos/edit";
    }

    if (producto.getUnidad() == null
            || producto.getUnidad().getIdUnidad() == null) {

        model.addAttribute(
                "errorUnidad",
                "Debe seleccionar una unidad."
        );

        cargarDatosFormulario(model);

        return producto.getIdProducto() == null
                ? "views/productos/create"
                : "views/productos/edit";
    }

    if (producto.getAfectacionTipo() == null
            || producto.getAfectacionTipo().getIdAfectacionTipo() == null) {

        model.addAttribute(
                "errorAfectacion",
                "Debe seleccionar un tipo de afectación."
        );

        cargarDatosFormulario(model);

        return producto.getIdProducto() == null
                ? "views/productos/create"
                : "views/productos/edit";
    }

    Long idUnidad = producto.getUnidad().getIdUnidad();

    Unidad unidad = unidadService.buscarPorId(idUnidad)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "La unidad seleccionada no existe."
                    )
            );

    Long idAfectacionTipo =
            producto.getAfectacionTipo().getIdAfectacionTipo();

    AfectacionTipo afectacionTipo =
            afectacionTipoService.buscarPorId(idAfectacionTipo)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El tipo de afectación seleccionado no existe."
                            )
                    );

    producto.setUnidad(unidad);
    producto.setAfectacionTipo(afectacionTipo);

    String imagenAnterior = null;

    if (producto.getIdProducto() != null) {

        imagenAnterior = productoService.buscarPorId(producto.getIdProducto())
                .map(Producto::getImagen)
                .orElse(null);

        // Si no se sube una imagen nueva, se conserva la actual.
        producto.setImagen(imagenAnterior);
    }

    try {

        String nombreArchivo = productoService.guardarImagen(imagenArchivo);

        if (nombreArchivo != null) {
            producto.setImagen(nombreArchivo);
        }

    } catch (IllegalArgumentException | IOException ex) {

        model.addAttribute("error", ex.getMessage() != null
                ? ex.getMessage()
                : "No se pudo guardar la imagen del producto.");

        cargarDatosFormulario(model);

        return producto.getIdProducto() == null
                ? "views/productos/create"
                : "views/productos/edit";
    }

    if (producto.getIdProducto() == null) {

        productoService.guardar(producto);

    } else {

        productoService.actualizar(producto);

        // Si se reemplazó la imagen, se borra el archivo anterior.
        if (imagenAnterior != null
                && !imagenAnterior.equals(producto.getImagen())) {

            productoService.eliminarImagenSiExiste(imagenAnterior);
        }
    }

    redirectAttributes.addFlashAttribute(
            "success",
            "Producto guardado correctamente."
    );

    return "redirect:/productos";
}

@GetMapping("/edit/{id}")
public String edit(
        @PathVariable Long id,
        Model model) {

    Producto producto = productoService.buscarPorId(id)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Producto no encontrado."
                    )
            );

    model.addAttribute(
            "producto",
            producto
    );

    cargarDatosFormulario(model);

    return "views/productos/edit";
}

@GetMapping("/delete/{id}")
public String delete(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes) {

    productoService.eliminar(id);

    redirectAttributes.addFlashAttribute(
            "success",
            "Producto eliminado correctamente."
    );

    return "redirect:/productos";
}

@GetMapping("/{id}")
public String show(
        @PathVariable Long id,
        Model model) {

    Producto producto = productoService.buscarPorId(id)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "Producto no encontrado."
                    )
            );

    model.addAttribute(
            "producto",
            producto
    );

    return "views/productos/show";
}

private void cargarDatosFormulario(Model model) {

    model.addAttribute(
            "unidades",
            unidadService.listar()
    );

    model.addAttribute(
            "afectacionTipos",
            afectacionTipoService.listar()
    );
}

}
