package com.unisul.ecommerce.repository;

import com.unisul.ecommerce.model.Cliente;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClienteRepositoryTest {

    @Test
    void deveBuscarTodosOsClientesDoBancoSimulado() {
        ClienteRepository repo = new ClienteRepository();
        List<Cliente> clientes = repo.buscarTodos();
        
        assertNotNull(clientes);
        assertEquals(1, clientes.size());
        assertEquals("Cliente Demonstração", clientes.get(0).getNome());
    }
}