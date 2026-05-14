package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

	private final ClienteRepository repository;

	public ClienteService(ClienteRepository repository) {
		this.repository = repository;
	}

	public List<Cliente> listar() {
		return repository.findAll();
	}

	public Optional<Cliente> buscarPorId(Long id) {
		return repository.findById(id);
	}

	public Cliente guardar(Cliente cliente) {
		return repository.save(cliente);
	}

	public Cliente actualizar(Cliente cliente) {
		return repository.save(cliente);
	}

	public void eliminar(Long id) {
		repository.deleteById(id);
	}
}
