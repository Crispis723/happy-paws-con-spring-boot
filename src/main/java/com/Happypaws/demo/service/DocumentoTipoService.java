package com.Happypaws.demo.service;

import com.Happypaws.demo.exception.ResourceNotFoundException;
import com.Happypaws.demo.model.DocumentoTipo;
import com.Happypaws.demo.repository.DocumentoTipoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class DocumentoTipoService {

    private final DocumentoTipoRepository repository;

    public DocumentoTipoService(DocumentoTipoRepository repository) {
        this.repository = repository;
    }

    public List<DocumentoTipo> listar() {
        return repository.findAll();
    }

    public Optional<DocumentoTipo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public DocumentoTipo guardar(DocumentoTipo documentoTipo) {
        return repository.save(documentoTipo);
    }

    public DocumentoTipo actualizar(DocumentoTipo documentoTipo) {
        if (documentoTipo.getIdDocumentoTipo() == null || !repository.existsById(documentoTipo.getIdDocumentoTipo())) {
            throw new ResourceNotFoundException("Tipo de documento no encontrado");
        }
        return repository.save(documentoTipo);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}