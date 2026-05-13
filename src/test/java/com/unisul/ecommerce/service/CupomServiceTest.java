package com.unisul.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import com.unisul.ecommerce.exception.CupomInvalidoException;
import com.unisul.ecommerce.model.Cupom;
import com.unisul.ecommerce.model.TipoCupom;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

public class CupomServiceTest {

    private final CupomService service = new CupomService();

    @Test
    public void deveGarantirQueValorFinalNaoSejaNegativo() throws CupomInvalidoException {
        BigDecimal subtotal = new BigDecimal("40.00");
        
        Cupom cupom = new Cupom("DESC50", new BigDecimal("50.00"), BigDecimal.ZERO, TipoCupom.FIXO);
        cupom.setAtivo(true);

        BigDecimal resultado = service.aplicarCupom(subtotal, cupom);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    public void deveLancarCupomInvalidoExceptionQuandoCupomInativo() {
        BigDecimal subtotal = new BigDecimal("100.00");
        Cupom cupom = new Cupom("OFF10", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.FIXO);
        cupom.setAtivo(false);

        assertThrows(CupomInvalidoException.class, () -> {
            service.aplicarCupom(subtotal, cupom);
        });
    }

    @Test
    public void deveAplicarDescontoPercentualComSucesso() throws CupomInvalidoException {
        BigDecimal subtotal = new BigDecimal("100.00");
        Cupom cupom = new Cupom("10OFF", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.PERCENTUAL);
        cupom.setAtivo(true);

        BigDecimal resultado = service.aplicarCupom(subtotal, cupom);

        assertEquals(new BigDecimal("90.00"), resultado);
    }
}