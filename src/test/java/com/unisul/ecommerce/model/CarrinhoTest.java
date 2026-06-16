package com.unisul.ecommerce.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CarrinhoTest {

    @Test
    public void deveAdicionarNovoItemAoCarrinho_QuandoProdutoForValido() {
        Carrinho carrinho = new Carrinho();
        Produto produto = new Produto(1L, "Teclado", new BigDecimal("100.00"), BigDecimal.ONE);
        carrinho.adicionarItem(produto, 1);

        assertEquals(1, carrinho.getItens().size());
        assertEquals(1, carrinho.getItens().get(0).getQuantidade());
    }

    @Test
    public void deveIgnorarInclusao_QuandoProdutoForInteiramenteNulo() {
        Carrinho carrinho = new Carrinho();
        carrinho.adicionarItem(null, 2);
        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    public void deveIgnorarInclusao_QuandoProdutoExistirMasNaoTiverId() {
        Carrinho carrinho = new Carrinho();
        Produto produtoSemId = new Produto();
        produtoSemId.setNome("Fantasma");
        carrinho.adicionarItem(produtoSemId, 1);
        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    public void deveIncrementarQuantidade_QuandoAdicionarProdutoQueJaExiste() {
        Carrinho carrinho = new Carrinho();
        Produto produto = new Produto(500L, "Mouse", new BigDecimal("50.00"), BigDecimal.ONE);

        carrinho.adicionarItem(produto, 2); // Adiciona a primeira vez
        carrinho.adicionarItem(produto, 3); // Cai no IF do equals e soma

        assertEquals(1, carrinho.getItens().size());
        assertEquals(5, carrinho.getItens().get(0).getQuantidade());
    }

    @Test
    public void deveTestarConstrutorComCliente() {
        Cliente cliente = new Cliente(1L, "João", "00000", 0);
        Carrinho carrinho = new Carrinho(cliente);

        assertEquals(cliente, carrinho.getCliente());
    }

    @Test
    public void deveRetornarValorTotalZero_QuandoCarrinhoVazioOuNulo() {
        Carrinho carrinho = new Carrinho();
        assertEquals(0, BigDecimal.ZERO.compareTo(carrinho.getValorTotal()));

        carrinho.setItens(null);
        assertEquals(0, BigDecimal.ZERO.compareTo(carrinho.getValorTotal()));
    }

    @Test
    public void deveCalcularValorTotalCorretamente_ComVariosItens() {
        Carrinho carrinho = new Carrinho();
        Produto p1 = new Produto(1L, "Cabo", new BigDecimal("10.00"), BigDecimal.ZERO);
        Produto p2 = new Produto(2L, "Carregador", new BigDecimal("50.00"), BigDecimal.ZERO);

        carrinho.adicionarItem(p1, 3);
        carrinho.adicionarItem(p2, 1);

        assertEquals(0, new BigDecimal("80.00").compareTo(carrinho.getValorTotal()));
    }

    @Test
    public void deveTestarGettersESettersSimples() {
        Carrinho carrinho = new Carrinho();

        Cupom cupom = new Cupom();
        carrinho.setCupomAplicado(cupom);
        assertEquals(cupom, carrinho.getCupomAplicado());

        carrinho.setCliente(null);
        assertNull(carrinho.getCliente());

        carrinho.setItens(new ArrayList<>());
        assertTrue(carrinho.getItens().isEmpty());
    }
}