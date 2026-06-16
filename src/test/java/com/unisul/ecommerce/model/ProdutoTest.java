package com.unisul.ecommerce.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest {

    @Test
    public void deveTestarConstrutoresEGetters() {
        // Testa construtor com 3 argumentos (cobre a linha 20)
        Produto p1 = new Produto("Mouse Gamer", new BigDecimal("150.00"), new BigDecimal("0.3"));
        assertNull(p1.getId());
        assertEquals("Mouse Gamer", p1.getNome());

        // Testa construtor completo e o getId
        Produto p2 = new Produto(1L, "Teclado Mecânico", new BigDecimal("350.00"), new BigDecimal("0.8"));
        assertEquals(1L, p2.getId());
    }

    @Test
    public void deveTestarSettersSimples() {
        Produto p = new Produto();
        p.setNome("Monitor 24 Pol");
        assertEquals("Monitor 24 Pol", p.getNome());
    }

    @Test
    public void deveGarantirPrecoNuncaNegativoOuNulo() {
        Produto p = new Produto();

        // Cenário 1: Nulo -> vira ZERO
        p.setPreco(null);
        assertEquals(0, BigDecimal.ZERO.compareTo(p.getPreco()));

        // Cenário 2: Negativo -> vira ZERO
        p.setPreco(new BigDecimal("-50.00"));
        assertEquals(0, BigDecimal.ZERO.compareTo(p.getPreco()));

        // Cenário 3: Válido -> mantém o valor
        p.setPreco(new BigDecimal("199.99"));
        assertEquals(0, new BigDecimal("199.99").compareTo(p.getPreco()));
    }

    @Test
    public void deveGarantirPesoNuncaNegativoOuNulo() {
        Produto p = new Produto();

        // Cenário 1: Nulo -> vira ZERO
        p.setPesoKg(null);
        assertEquals(0, BigDecimal.ZERO.compareTo(p.getPesoKg()));

        // Cenário 2: Negativo -> vira ZERO
        p.setPesoKg(new BigDecimal("-1.5"));
        assertEquals(0, BigDecimal.ZERO.compareTo(p.getPesoKg()));

        // Cenário 3: Válido -> mantém o valor
        p.setPesoKg(new BigDecimal("2.5"));
        assertEquals(0, new BigDecimal("2.5").compareTo(p.getPesoKg()));
    }
}