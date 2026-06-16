package com.unisul.ecommerce.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PontosInsuficientesExceptionTest {

    @Test
    public void deveFormatarMensagemDeErroCorretamente() {
        int pontosNecessarios = 500;
        int pontosAtuais = 100;

        // Instancia a exceção (isso cobre as linhas vermelhas do construtor)
        PontosInsuficientesException exception = new PontosInsuficientesException(pontosNecessarios, pontosAtuais);

        // Verifica se a mensagem montada no super() está com os números certos
        String mensagemEsperada = "Saldo insuficiente! Você tentou usar 500 pontos, mas só possui 100.";
        assertEquals(mensagemEsperada, exception.getMessage());
    }
}