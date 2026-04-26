package com.unisul.ecommerce.exception;

public class PontosInsuficientesException extends RuntimeException {
    public PontosInsuficientesException(int pontosNecessarios, int pontosAtuais) {
        super("Saldo insuficiente! Você tentou usar " + pontosNecessarios +
                " pontos, mas só possui " + pontosAtuais
                + ".");
    }

}
