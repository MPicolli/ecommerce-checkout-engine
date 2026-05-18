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

    /**
     * Acumula pontos com base no valor gasto.
     * Regra: 1 ponto a cada R$ 10.
     */
    public void acumularPontos(BigDecimal valorGasto) {
        if (valorGasto == null || valorGasto.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        int pontosGanhos = valorGasto.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
        this.pontos += pontosGanhos;
    }

    /**
     * Resgata pontos.
     * Regra: mínimo de 100 pontos.
     */
    public void resgatar(int pontosParaResgate) {
        if (pontosParaResgate <= 0) {
            throw new IllegalArgumentException("A quantidade de pontos para resgate deve ser maior que zero.");
        }
        if (pontosParaResgate > this.pontos) {
            throw new IllegalArgumentException("Quantidade maior que o saldo disponível");
        }
        if (pontosParaResgate < 100) {
            throw new IllegalArgumentException("Pontos insuficientes para resgate. O mínimo é 100 pontos.");
        }
        this.pontos -= pontosParaResgate;
    }

    public boolean podeResgatar() {
        return this.pontos >= 100;
    }

}