package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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

	public Optional<Cliente> buscarPorEmail(String email) {
		return repository.findByEmail(email);
	}

	public Cliente resolverOCrearClienteAutenticado(String email, String nombreSugerido) {
		return buscarPorEmail(email)
				.orElseGet(() -> crearClienteBasico(email, nombreSugerido));
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

	private Cliente crearClienteBasico(String email, String nombreSugerido) {
		Cliente cliente = new Cliente();
		cliente.setDocumentoTipoCodigo("CC");
		cliente.setNumeroDocumento(generarNumeroDocumentoUnico());
		cliente.setRazonSocial((nombreSugerido != null && !nombreSugerido.isBlank()) ? nombreSugerido : email);
		cliente.setDireccion("Pendiente de actualizar");
		cliente.setTelefono("000000000");
		cliente.setEmail(email);
		return repository.save(cliente);
	}

	private String generarNumeroDocumentoUnico() {
		String numero;
		do {
			numero = String.valueOf(ThreadLocalRandom.current().nextInt(10000000, 99999999));
		} while (repository.findByNumeroDocumento(numero).isPresent());
		return numero;
	}
}
