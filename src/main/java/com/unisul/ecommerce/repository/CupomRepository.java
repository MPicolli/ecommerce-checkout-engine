package com.unisul.ecommerce.repository;

import com.unisul.ecommerce.model.Cupom;
import com.unisul.ecommerce.model.TipoCupom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CupomRepository {

    private List<Cupom> bancoDeCupons = new ArrayList<>();

    public CupomRepository() {
        // Usando exatamente os mesmos parâmetros que já funcionavam no seu Controller!
        bancoDeCupons.add(new Cupom(1L, "CUPOM10", new BigDecimal("10.00"), BigDecimal.ZERO, TipoCupom.PERCENTUAL, true));
        bancoDeCupons.add(new Cupom(2L, "FIXO30", new BigDecimal("30.00"), new BigDecimal("100.00"), TipoCupom.FIXO, true));
    }

    public List<Cupom> buscarTodos() {
        return bancoDeCupons;
    }
}