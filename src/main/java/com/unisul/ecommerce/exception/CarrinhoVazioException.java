package com.unisul.ecommerce.exception;

public class CarrinhoVazioException extends RuntimeException {
    public CarrinhoVazioException() {
        super("Não é possível finalizar o pedido com o carrinho vazio.");
    }

}
