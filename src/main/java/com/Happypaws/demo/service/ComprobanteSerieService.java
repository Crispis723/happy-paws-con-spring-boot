package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.ComprobanteSerie;
import com.Happypaws.demo.repository.ComprobanteSerieRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ComprobanteSerieService {

    private final ComprobanteSerieRepository repository;

    public ComprobanteSerieService(ComprobanteSerieRepository repository) {
        this.repository = repository;
    }

    public List<ComprobanteSerie> listar() {
        return repository.findAll();
    }

    public Optional<ComprobanteSerie> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public ComprobanteSerie guardar(ComprobanteSerie comprobanteSerie) {
        return repository.save(comprobanteSerie);
    }

    public ComprobanteSerie actualizar(ComprobanteSerie comprobanteSerie) {
        if (comprobanteSerie.getIdSerie() == null || !repository.existsById(comprobanteSerie.getIdSerie())) {
            throw new ResourceNotFoundException("Serie de comprobante no encontrada");
        }
        return repository.save(comprobanteSerie);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}