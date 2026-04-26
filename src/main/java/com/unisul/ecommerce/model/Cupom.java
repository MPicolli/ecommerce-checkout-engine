package com.unisul.ecommerce.model;

import java.math.BigDecimal;

public class Cupom {

    private Long id;
    private String codigo;
    private BigDecimal valorDesconto;
    private BigDecimal valorMinimoCompra;
    private TipoCupom tipo;
    private boolean ativo;

    public Cupom(Long id, String codigo,
            BigDecimal valorDesconto, BigDecimal valorMinimoCompra, TipoCupom tipo, boolean ativo) {
        this.id = id;
        this.codigo = codigo;
        setValorDesconto(valorDesconto);
        this.valorMinimoCompra = valorMinimoCompra != null ? valorMinimoCompra : BigDecimal.ZERO;
        this.tipo = tipo;
        this.ativo = ativo;
    }

    public Cupom(String codigo, BigDecimal valorDesconto, BigDecimal valorMinimoCompra, TipoCupom tipo) {
        this(null, codigo, valorDesconto, BigDecimal.ZERO, tipo, true);
    }

    public Cupom() {
        this(null, null, null, null, null, false);
    }

    public boolean estaValido(BigDecimal valorCarrinho) {
        if (!ativo)
            return false;
        if (valorCarrinho == null)
            return false;
        return valorCarrinho.compareTo(valorMinimoCompra) >= 0;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            this.valorDesconto = BigDecimal.ZERO;
        } else if (this.tipo == TipoCupom.PERCENTUAL && valor.compareTo(BigDecimal.valueOf(100)) > 0) {
            this.valorDesconto = BigDecimal.valueOf(100);
        } else {
            this.valorDesconto = valor;
        }
    }

    public BigDecimal getValorMinimoCompra() {
        return valorMinimoCompra;
    }

    public void setValorMinimoCompra(BigDecimal valorMinimoCompra) {
        this.valorMinimoCompra = valorMinimoCompra;
    }

    public TipoCupom getTipo() {
        return tipo;
    }

    public void setTipo(TipoCupom tipo) {
        this.tipo = tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

}
