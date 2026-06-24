package com.unisul.ecommerce.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Carrinho {

    private Cliente cliente;
    private List<ItemCarrinho> itens;
    private Cupom cupomAplicado;

    public Carrinho(Cliente cliente) {
        this();
        this.cliente = cliente;
    }

    public Carrinho() {
        this.cliente = null;
        this.itens = new ArrayList<>();
        this.cupomAplicado = null;
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (produto == null || produto.getId() == null) {
            return;
        }

        for (ItemCarrinho item : itens) {
            if (item.getProduto().getId().equals(produto.getId())) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        this.itens.add(new ItemCarrinho(produto, quantidade));
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }

    public Cupom getCupomAplicado() {
        return cupomAplicado;
    }

    public void setCupomAplicado(Cupom cupomAplicado) {
        this.cupomAplicado = cupomAplicado;
    }

    public BigDecimal getValorTotal() {
        if (this.itens == null || this.itens.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrinho item : this.itens) {
            BigDecimal precoProduto = item.getProduto().getPreco();
            BigDecimal quantidade = BigDecimal.valueOf(item.getQuantidade());
            BigDecimal subtotalItem = precoProduto.multiply(quantidade);
            total = total.add(subtotalItem);
        }
        return total;
    }

}
