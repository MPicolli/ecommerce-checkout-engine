package com.unisul.ecommerce.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ResumoPedidoTest {

    @Test
    public void deveGarantirTotalFinalNuncaNegativo_NoConstrutor() {
        Carrinho carrinho = new Carrinho();

        // Cenário 1: Passando valor negativo (deve zerar)
        ResumoPedido resumoNegativo = new ResumoPedido(carrinho, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, 10,
                new BigDecimal("-5.00"));
        assertEquals(0, BigDecimal.ZERO.compareTo(resumoNegativo.getTotalFinal()));

        // Cenário 2: Passando valor positivo (deve manter)
        ResumoPedido resumoPositivo = new ResumoPedido(carrinho, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, 10,
                new BigDecimal("100.00"));
        assertEquals(0, new BigDecimal("100.00").compareTo(resumoPositivo.getTotalFinal()));
    }

    @Test
    public void deveTestarConstrutorVazioENullSafe() {
        // Cobre o construtor vazio e a blindagem contra NullPointerException
        ResumoPedido resumo = new ResumoPedido();

        assertNull(resumo.getCarrinho());
        assertNull(resumo.getSubtotal());
        assertEquals(0, BigDecimal.ZERO.compareTo(resumo.getTotalFinal())); // O null virou ZERO graças à proteção
    }

    @Test
    public void deveTestarGettersESetters() {
        ResumoPedido resumo = new ResumoPedido();
        Carrinho carrinho = new Carrinho();

        // Disparando todos os Setters
        resumo.setCarrinho(carrinho);
        resumo.setSubtotal(new BigDecimal("50.00"));
        resumo.setValorDescontos(new BigDecimal("10.00"));
        resumo.setValorFrete(new BigDecimal("15.00"));
        resumo.setPontosGanhos(50);
        resumo.setTotalFinal(new BigDecimal("55.00")); // Confirmação do uso do setTotalFinal() correto

        // Validando todos os Getters
        assertEquals(carrinho, resumo.getCarrinho());
        assertEquals(0, new BigDecimal("50.00").compareTo(resumo.getSubtotal()));
        assertEquals(0, new BigDecimal("10.00").compareTo(resumo.getValorDescontos()));
        assertEquals(0, new BigDecimal("15.00").compareTo(resumo.getValorFrete()));
        assertEquals(50, resumo.getPontosGanhos());
        assertEquals(0, new BigDecimal("55.00").compareTo(resumo.getTotalFinal()));
    }
}