package com.unisul.ecommerce.model;

import java.math.BigDecimal;

public class ItemCarrinho {

    private Produto produto;
    private int quantidade;

    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = produto;
        setQuantidade(quantidade);
    }

    public ItemCarrinho() {
        this(null, 0);
    }

    public BigDecimal getSubtotal() {
        if (this.produto == null || this.produto.getPreco() == null) {
            return BigDecimal.ZERO;
        }
        return this.produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = Math.max(1, quantidade); // Garante que a quantidade não seja menor que 1.
    }

}
