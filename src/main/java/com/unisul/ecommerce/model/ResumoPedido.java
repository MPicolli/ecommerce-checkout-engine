package com.unisul.ecommerce.model;

import java.math.BigDecimal;

public class ResumoPedido {

    private Carrinho carrinho;
    private BigDecimal subtotal;
    private BigDecimal valorDescontos;
    private BigDecimal valorFrete;
    private int pontosGanhos;
    private BigDecimal totalFinal;

    public ResumoPedido(Carrinho carrinho, BigDecimal subtotal,
            BigDecimal valorDescontos, BigDecimal valorFrete, int pontosGanhos, BigDecimal totalFinal) {
        this.carrinho = carrinho;
        this.subtotal = subtotal;
        this.valorDescontos = valorDescontos;
        this.valorFrete = valorFrete;
        this.pontosGanhos = pontosGanhos;
        this.totalFinal = (totalFinal.compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ZERO : totalFinal;
    }

    public ResumoPedido() {
        this(null, null, null, null, 0, null);
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(Carrinho carrinho) {
        this.carrinho = carrinho;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getValorDescontos() {
        return valorDescontos;
    }

    public void setValorDescontos(BigDecimal valorDescontos) {
        this.valorDescontos = valorDescontos;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public int getPontosGanhos() {
        return pontosGanhos;
    }

    public void setPontosGanhos(int pontosGanhos) {
        this.pontosGanhos = pontosGanhos;
    }

    public BigDecimal getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(BigDecimal totalFinal) {
        this.totalFinal = totalFinal;
    }

}
