package com.unisul.ecommerce.repository;

import com.unisul.ecommerce.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoRepositoryTest {

    private ProdutoRepository repo;

    @BeforeEach
    void setUp() {
        repo = new ProdutoRepository();
    }

    @Test
    void deveBuscarTodosOsProdutosDoBancoSimulado() {
        List<Produto> produtos = repo.buscarTodos();
        assertFalse(produtos.isEmpty());
        assertEquals(4, produtos.size());
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() {
        Optional<Produto> produto = repo.buscarPorId(1L);
        assertTrue(produto.isPresent());
        assertEquals("Teclado Mecânico RGB", produto.get().getNome());
    }

    @Test
    void deveSalvarEGerarIdParaNovoProduto() {
        Produto novo = new Produto("Item Teste", new BigDecimal("50.00"), new BigDecimal("1.000"));
        Produto salvo = repo.salvar(novo);
        
        assertNotNull(salvo.getId());
        assertEquals("Item Teste", salvo.getNome());
    }

    @Test
    void deveAtualizarProdutoExistente() {
        Produto existente = repo.buscarPorId(1L).get();
        existente.setNome("Teclado Atualizado");
        
        repo.salvar(existente);
        assertEquals("Teclado Atualizado", repo.buscarPorId(1L).get().getNome());
    }

    @Test
    void deveDeletarProdutoComSucesso() {
        repo.deletar(1L);
        Optional<Produto> produto = repo.buscarPorId(1L);
        assertFalse(produto.isPresent());
    }
}