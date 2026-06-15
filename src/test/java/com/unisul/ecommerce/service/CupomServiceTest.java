package com.unisul.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import com.unisul.ecommerce.exception.CupomInvalidoException;
import com.unisul.ecommerce.model.Cupom;
import com.unisul.ecommerce.model.TipoCupom;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CupomServiceTest {

    private final CupomService cupomService = new CupomService();

    @Test
    public void deveGarantirQueValorFinalNaoSejaNegativo() throws CupomInvalidoException {
        BigDecimal subtotal = new BigDecimal("40.00");
        
        Cupom cupom = new Cupom("DESC50", new BigDecimal("50.00"), BigDecimal.ZERO, TipoCupom.FIXO);
        cupom.setAtivo(true);

        BigDecimal resultado = cupomService.aplicarCupom(subtotal, cupom);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    public void deveLancarCupomInvalidoExceptionQuandoCupomInativo() {
        BigDecimal subtotal = new BigDecimal("100.00");
        Cupom cupom = new Cupom("OFF10", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.FIXO);
        cupom.setAtivo(false);

        assertThrows(CupomInvalidoException.class, () -> {
            cupomService.aplicarCupom(subtotal, cupom);
        });
    }

    @Test
    public void deveAplicarDescontoPercentualComSucesso() throws CupomInvalidoException {
        BigDecimal subtotal = new BigDecimal("100.00");
        Cupom cupom = new Cupom("10OFF", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.PERCENTUAL);
        cupom.setAtivo(true);

        BigDecimal resultado = cupomService.aplicarCupom(subtotal, cupom);

        assertEquals(new BigDecimal("90.00"), resultado);
    }

    @Test
    public void deveLancarExcecao_QuandoCupomForTotalmenteNulo() {
        BigDecimal subtotal = new BigDecimal("100.00");
        
        assertThrows(CupomInvalidoException.class, () -> {
            cupomService.aplicarCupom(subtotal, null);
        });
    }

    @Test
    public void deveRetornarValorSemDesconto_QuandoTipoDoCupomForNuloOuDesconhecido() throws CupomInvalidoException {
        BigDecimal subtotal = new BigDecimal("100.00");
        
        Cupom cupomMock = mock(Cupom.class);
        
        when(cupomMock.estaValido(subtotal)).thenReturn(true);
        when(cupomMock.getTipo()).thenReturn(null);
        
        BigDecimal valorFinal = cupomService.aplicarCupom(subtotal, cupomMock);
        
        // Como o tipo é nulo, ele não entra em nenhum if de desconto e o valor permanece 100
        assertEquals(0, new BigDecimal("100.00").compareTo(valorFinal));
    }
}