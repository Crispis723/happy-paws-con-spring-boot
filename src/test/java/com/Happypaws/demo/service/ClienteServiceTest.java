package com.Happypaws.demo.service;

import com.Happypaws.demo.model.Cliente;
import com.Happypaws.demo.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Cobertura básica de ClienteService: resolución/creación de cliente a
 * partir del usuario autenticado, y las versiones paginada/conteo del
 * listado usadas por el panel de clientes y el dashboard.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void resolverOCrearClienteAutenticado_siYaExisteLoDevuelve() {
        Cliente existente = new Cliente();
        existente.setIdCliente(5L);
        existente.setEmail("ana@example.com");

        when(clienteRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(existente));

        Cliente resultado = clienteService.resolverOCrearClienteAutenticado("ana@example.com", "Ana");

        assertThat(resultado).isSameAs(existente);
    }

    @Test
    void resolverOCrearClienteAutenticado_siNoExisteCreaUnoBasico() {
        when(clienteRepository.findByEmail("nuevo@example.com")).thenReturn(Optional.empty());
        when(clienteRepository.findByNumeroDocumento(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());
        when(clienteRepository.save(org.mockito.ArgumentMatchers.any(Cliente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = clienteService.resolverOCrearClienteAutenticado("nuevo@example.com", "Nuevo Cliente");

        assertThat(resultado.getEmail()).isEqualTo("nuevo@example.com");
        assertThat(resultado.getRazonSocial()).isEqualTo("Nuevo Cliente");
    }

    @Test
    void listarPaginado_delegaEnElRepositorioYDevuelveLaPagina() {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        Pageable pageable = PageRequest.of(0, 20);
        Page<Cliente> pagina = new PageImpl<>(List.of(cliente), pageable, 1);

        when(clienteRepository.findAll(pageable)).thenReturn(pagina);

        Page<Cliente> resultado = clienteService.listar(pageable);

        assertThat(resultado.getContent()).containsExactly(cliente);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    @Test
    void contar_delegaEnElRepositorio() {
        when(clienteRepository.count()).thenReturn(7L);

        assertThat(clienteService.contar()).isEqualTo(7L);
    }
}
