package com.unisul.ecommerce.model;

public class Cliente {

    private Long id;
    private String nome;
    private String cep;
    private int saldoPontos;

    public Cliente(Long id, String nome, String cep, int saldoPontos) {
        this.id = id;
        this.nome = nome;
        this.cep = cep;
        setSaldoPontos(saldoPontos);
    }

    public Cliente(String nome, String cep) {
        this(null, nome, cep, 0);
    }

    public Cliente() {
        this(null, null, null, 0);
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public int getSaldoPontos() {
        return saldoPontos;
    }

    public void setSaldoPontos(int saldoPontos) {
        this.saldoPontos = Math.max(0, saldoPontos);
    }

}
