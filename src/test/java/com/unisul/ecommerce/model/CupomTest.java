package com.unisul.ecommerce.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class CupomTest {

    @Test
    public void deveValidarCupom_SatisfeitasAsCondicoes() {
        // Usa o construtor completo
        Cupom cupom = new Cupom(1L, "DESC10", new BigDecimal("10.00"), new BigDecimal("50.00"), TipoCupom.FIXO, true);

        assertTrue(cupom.estaValido(new BigDecimal("60.00")));
        assertFalse(cupom.estaValido(new BigDecimal("40.00"))); // Abaixo do mínimo
        assertFalse(cupom.estaValido(null)); // Valor nulo
    }

    @Test
    public void deveRetornarInvalido_QuandoCupomInativo() {
        Cupom cupom = new Cupom(1L, "X", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.FIXO, false);
        assertFalse(cupom.estaValido(new BigDecimal("100.00")));
    }

    @Test
    public void deveGarantirRegrasDeLimiteAoSetarValorDesconto() {
        Cupom cupomPercentual = new Cupom();
        cupomPercentual.setTipo(TipoCupom.PERCENTUAL);

        // Caso 1: Forçar limite máximo de 100%
        cupomPercentual.setValorDesconto(new BigDecimal("150.00"));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(cupomPercentual.getValorDesconto()));

        // Caso 2: Forçar valor negativo a virar ZERO
        cupomPercentual.setValorDesconto(new BigDecimal("-5.00"));
        assertEquals(0, BigDecimal.ZERO.compareTo(cupomPercentual.getValorDesconto()));

        // Caso 3: Valor normal
        cupomPercentual.setValorDesconto(new BigDecimal("15.00"));
        assertEquals(0, new BigDecimal("15.00").compareTo(cupomPercentual.getValorDesconto()));
    }

    @Test
    public void deveTestarConstrutorAlternativo() {
        // Testa o segundo construtor da classe
        Cupom cupom = new Cupom("PROMO", new BigDecimal("20.00"), null, TipoCupom.FIXO);
        assertTrue(cupom.isAtivo());
        assertEquals(0, BigDecimal.ZERO.compareTo(cupom.getValorMinimoCompra()));
    }

    // SOMENTE PARA DEIXAR TUDO 100%, MAS NÃO PRECISAVA
    @Test
    public void deveTestarGettersESettersSimples() {
        Cupom cupom = new Cupom();

        cupom.setCodigo("NOVO10");
        assertEquals("NOVO10", cupom.getCodigo());

        cupom.setAtivo(true);
        assertTrue(cupom.isAtivo());

        cupom.setValorMinimoCompra(new BigDecimal("100.00"));
        assertEquals(0, new BigDecimal("100.00").compareTo(cupom.getValorMinimoCompra()));

        // Testa o construtor completo novamente só pra cobrir a linha do getId
        Cupom cupomComId = new Cupom(99L, "X", BigDecimal.TEN, BigDecimal.ZERO, TipoCupom.FIXO, true);
        assertEquals(99L, cupomComId.getId());
        assertEquals(TipoCupom.FIXO, cupomComId.getTipo());
    }
}