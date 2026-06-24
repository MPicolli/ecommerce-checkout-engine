package com.unisul.ecommerce.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FidelidadeService {

    private int pontos;

    public FidelidadeService() {
        this.pontos = 0;
    }

    public FidelidadeService(int pontosIniciais) {
        if (pontosIniciais < 0) {
            throw new IllegalArgumentException("O saldo inicial de pontos não pode ser negativo.");
        }
        this.pontos = pontosIniciais;
    }

    public int getPontos() {
        return pontos;
    }

    public void acumularPontos(BigDecimal valorGasto) {
        if (valorGasto == null || valorGasto.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        int pontosGanhos = valorGasto.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
        this.pontos += pontosGanhos;
    }

    public void resgatar(int pontosParaResgate) {
        if (pontosParaResgate <= 0) {
            throw new IllegalArgumentException("A quantidade de pontos para resgate deve ser maior que zero.");
        }

        if (this.pontos < 100) {
            throw new IllegalStateException("Pontos insuficientes para resgate (mínimo 100).");
        }

        if (pontosParaResgate > this.pontos) {
            throw new IllegalArgumentException("Quantidade maior que o saldo disponível.");
        }

        this.pontos -= pontosParaResgate;
    }

    public boolean podeResgatar() {
        return this.pontos >= 100;
    }

}