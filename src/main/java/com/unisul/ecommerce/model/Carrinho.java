package com.unisul.ecommerce.model;

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
        for (ItemCarrinho item : itens) {
            if (item.getProduto().getId() == produto.getId()) {
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

}
