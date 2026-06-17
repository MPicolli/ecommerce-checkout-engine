package com.unisul.ecommerce.repository;

import com.unisul.ecommerce.model.Produto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepository {

    private List<Produto> bancoDeProdutos = new ArrayList<>();
    private Long geradorId = 1L;

    public ProdutoRepository() {
        // Agora os pesos estão usando BigDecimal para respeitar o seu modelo original
        salvar(new Produto(null, "Teclado Mecânico RGB", new BigDecimal("350.00"), new BigDecimal("0.800")));
        salvar(new Produto(null, "Mouse Gamer 10000 DPI", new BigDecimal("150.00"), new BigDecimal("0.200")));
        salvar(new Produto(null, "Monitor 24 Polegadas", new BigDecimal("850.00"), new BigDecimal("3.500")));
        salvar(new Produto(null, "Headset Surround 7.1", new BigDecimal("220.00"), new BigDecimal("0.400")));
    }

    public Produto salvar(Produto produto) {
        if (produto.getId() == null) {
            Produto novo = new Produto(geradorId++, produto.getNome(), produto.getPreco(), produto.getPesoKg());
            bancoDeProdutos.add(novo);
            return novo;
        }
        deletar(produto.getId());
        bancoDeProdutos.add(produto);
        return produto;
    }

    public List<Produto> buscarTodos() {
        return new ArrayList<>(bancoDeProdutos);
    }

    public Optional<Produto> buscarPorId(Long id) {
        return bancoDeProdutos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public void deletar(Long id) {
        bancoDeProdutos.removeIf(p -> p.getId().equals(id));
    }
}