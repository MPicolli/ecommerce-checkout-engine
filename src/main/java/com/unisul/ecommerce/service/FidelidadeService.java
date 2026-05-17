package com.unisul.ecommerce.service;

public class FidelidadeService {

    private int pontos;

    public FidelidadeService() {
        this.pontos = 0;
    }

    public int getPontos() {
        return pontos;
    }

    /**
     * Acumula pontos com base no valor gasto.
     * Regra: 1 ponto a cada R$ 10.
     */
    public void acumularPontos(double valorGasto) {
        if (valorGasto <= 0) {
            return;
        }

        int pontosGanhos = (int) (valorGasto / 10);
        this.pontos += pontosGanhos;
    }

    /**
     * Resgata pontos.
     * Regra: mínimo de 100 pontos.
     */
    public void resgatar(int pontosParaResgate) {
        if (this.pontos < 100) {
            throw new IllegalStateException("Pontos insuficientes para resgate (mínimo 100).");
        }

        if (pontosParaResgate > this.pontos) {
            throw new IllegalArgumentException("Quantidade maior que o saldo disponível.");
        }

        this.pontos -= pontosParaResgate;
    }
}