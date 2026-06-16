package com.unisul.ecommerce.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class ItemCarrinhoTest {

    @Test
    public void deveCalcularSubtotalCorretamente() {
        Produto produto = new Produto();
        produto.setPreco(new BigDecimal("50.00"));

        ItemCarrinho item = new ItemCarrinho(produto, 3);

        assertEquals(0, new BigDecimal("150.00").compareTo(item.getSubtotal()));
    }

    @Test
    public void deveRetornarZero_QuandoProdutoForNulo() {
        ItemCarrinho item = new ItemCarrinho();
        item.setProduto(null);
        item.setQuantidade(2);

        assertEquals(BigDecimal.ZERO, item.getSubtotal());
    }

    @Test
    public void deveRetornarZero_QuandoPrecoDoProdutoForNulo() {
        Produto produto = new Produto();
        produto.setPreco(null);

        ItemCarrinho item = new ItemCarrinho(produto, 2);

        assertEquals(BigDecimal.ZERO, item.getSubtotal());
    }

    @Test
    public void deveGarantirQuantidadeMinimaSendoUm() {
        ItemCarrinho item = new ItemCarrinho(new Produto(), 10);

        item.setQuantidade(0);
        assertEquals(1, item.getQuantidade());

        item.setQuantidade(-5);
        assertEquals(1, item.getQuantidade());
    }

    @Test
    public void deveTestarConstrutorVazio() {
        ItemCarrinho item = new ItemCarrinho();
        assertNull(item.getProduto());
    }

    @Test
    public void deveTestarGettersESettersSimples() {
        ItemCarrinho item = new ItemCarrinho();
        Produto produto = new Produto();
        produto.setNome("Teclado");

        item.setProduto(produto);
        assertEquals(produto, item.getProduto());

        item.setQuantidade(5);
        assertEquals(5, item.getQuantidade());
    }
}