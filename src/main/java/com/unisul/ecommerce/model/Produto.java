package com.unisul.ecommerce.model;

import java.math.BigDecimal;

public class Produto {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private BigDecimal pesoKg;

    public Produto(Long id, String nome, BigDecimal preco, BigDecimal pesoKg) {
        this.id = id;
        this.nome = nome;
        setPreco(preco);
        setPesoKg(pesoKg);
    }

    public Produto(String nome, BigDecimal preco, BigDecimal pesoKg) {
        this(null, nome, preco, pesoKg);
    }

    public Produto() {
        this(null, null, null, null);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ZERO : preco;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = (pesoKg == null || pesoKg.compareTo(BigDecimal.ZERO) < 0) ? BigDecimal.ZERO : pesoKg;
    }

}
