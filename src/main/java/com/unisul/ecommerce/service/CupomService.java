package com.unisul.ecommerce.service;

import com.unisul.ecommerce.exception.CupomInvalidoException;
import com.unisul.ecommerce.model.Cupom;
import com.unisul.ecommerce.model.TipoCupom;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CupomService {

    public BigDecimal aplicarCupom(BigDecimal subtotal, Cupom cupom) throws CupomInvalidoException {
        validarCupom(subtotal, cupom);

        BigDecimal valorDesconto = BigDecimal.ZERO;

        if (cupom.getTipo() == TipoCupom.PERCENTUAL) {
            BigDecimal percentual = cupom.getValorDesconto().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            valorDesconto = subtotal.multiply(percentual);
        } else if (cupom.getTipo() == TipoCupom.FIXO) {
            valorDesconto = cupom.getValorDesconto();
        }

        BigDecimal valorFinal = subtotal.subtract(valorDesconto);

        if (valorFinal.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return valorFinal.setScale(2, RoundingMode.HALF_UP);
    }

    private void validarCupom(BigDecimal subtotal, Cupom cupom) throws CupomInvalidoException {
        if (cupom == null || !cupom.estaValido(subtotal)) {
            throw new CupomInvalidoException("Cupom inválido, expirado ou valor mínimo não atingido.");
        }
    }
}