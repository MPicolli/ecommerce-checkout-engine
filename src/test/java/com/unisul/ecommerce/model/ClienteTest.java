package com.unisul.ecommerce.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    public void deveTestarConstrutorComNomeECep() {
        // Cobre as linhas vermelhas 17, 18 e 19 do construtor parcial
        Cliente cliente = new Cliente("João Silva", "12345-678");

        assertNull(cliente.getId());
        assertEquals("João Silva", cliente.getNome());
        assertEquals("12345-678", cliente.getCep());
        assertEquals(0, cliente.getSaldoPontos());
    }

    @Test
    public void deveTestarGettersESettersRestantes() {
        // Usa o construtor vazio
        Cliente cliente = new Cliente();

        // Cobre as linhas vermelhas do setNome e getNome (29 a 35)
        cliente.setNome("Maria Joaquina");
        assertEquals("Maria Joaquina", cliente.getNome());

        // Cobre as linhas vermelhas do getId (25 a 27) instanciando o construtor
        // completo
        Cliente clienteComId = new Cliente(99L, "Carlos", "00000-000", 50);
        assertEquals(99L, clienteComId.getId());
    }

    @Test
    public void deveGarantirSaldoDePontosNuncaNegativo() {
        Cliente cliente = new Cliente();

        // Tenta setar um saldo negativo, o Math.max deve cravar em 0
        cliente.setSaldoPontos(-15);
        assertEquals(0, cliente.getSaldoPontos());

        // Tenta setar um saldo positivo, deve salvar normalmente
        cliente.setSaldoPontos(100);
        assertEquals(100, cliente.getSaldoPontos());
    }
}