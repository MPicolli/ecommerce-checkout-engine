package com.unisul.ecommerce.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class FidelidadeServiceTest {

    private FidelidadeService fidelidadeService;

    @BeforeEach
    public void setUp() {
        // Garante que todo teste comece com um serviço "limpo" e 0 pontos
        fidelidadeService = new FidelidadeService();
    }

    // --- COBERTURA DE CONSTRUTORES ---

    @Test
    public void deveIniciarComZeroPontos_UsandoConstrutorPadrao() {
        assertEquals(0, fidelidadeService.getPontos());
    }

    @Test
    public void deveIniciarComPontos_UsandoConstrutorSobrecarregado() {
        // Simula o carregamento de um cliente do Banco de Dados com 150 pontos
        FidelidadeService serviceComPontos = new FidelidadeService(150);
        assertEquals(150, serviceComPontos.getPontos());
    }

    @Test
    public void deveLancarExcecao_QuandoConstrutorReceberPontosNegativos() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FidelidadeService(-10); // Branch: pontosIniciais < 0
        });
    }

    // --- COBERTURA DE ACÚMULO DE PONTOS ---

    @Test
    public void deveAcumularPontos_QuandoValorGastoForValido() {
        // R$ 29,90 / 10 = 2 pontos (Truncado pelo RoundingMode.DOWN)
        fidelidadeService.acumularPontos(new BigDecimal("29.90"));
        assertEquals(2, fidelidadeService.getPontos());
    }

    @Test
    public void naoDeveAcumularPontos_QuandoValorGastoForMenorQueDez() {
        // R$ 9,99 / 10 = 0 pontos
        fidelidadeService.acumularPontos(new BigDecimal("9.99"));
        assertEquals(0, fidelidadeService.getPontos());
    }

    @Test
    public void naoDeveAcumularPontos_QuandoValorGastoForNulo() {
        fidelidadeService.acumularPontos(null); // Branch: valorGasto == null
        assertEquals(0, fidelidadeService.getPontos());
    }

    @Test
    public void naoDeveAcumularPontos_QuandoValorGastoForZeroOuNegativo() {
        fidelidadeService.acumularPontos(BigDecimal.ZERO); // Branch: valor == 0
        fidelidadeService.acumularPontos(new BigDecimal("-50.00")); // Branch: valor < 0

        assertEquals(0, fidelidadeService.getPontos());
    }

    // --- COBERTURA DE RESGATE DE PONTOS (EXCEÇÕES E SUCESSO) ---

    @Test
    public void deveResgatarPontosComSucesso_QuandoSaldoSuficiente() {
        FidelidadeService service = new FidelidadeService(150);
        service.resgatar(50); // Resgata 50, sobram 100
        assertEquals(100, service.getPontos());
    }

    @Test
    public void deveResgatarPontosComSucesso_QuandoSaldoForExato() {
        FidelidadeService service = new FidelidadeService(100);
        service.resgatar(100); // Resgata tudo, zera a conta
        assertEquals(0, service.getPontos());
    }

    @Test
    public void deveLancarExcecao_AoTentarResgatarComSaldoMenorQueCem() {
        // Tem 99 pontos, tenta resgatar 50.
        // Falha na Branch: this.pontos < 100 (mínimo para habilitar o resgate)
        FidelidadeService service = new FidelidadeService(99);
        assertThrows(IllegalStateException.class, () -> {
            service.resgatar(50);
        });
    }

    @Test
    public void deveLancarExcecao_AoTentarResgatarMaisPontosDoQuePossui() {
        // Tem 150 pontos, tenta resgatar 200.
        // Falha na Branch: pontosParaResgate > this.pontos
        FidelidadeService service = new FidelidadeService(150);
        assertThrows(IllegalArgumentException.class, () -> {
            service.resgatar(200);
        });
    }

    @Test
    public void deveLancarExcecao_AoTentarResgatarPontosZeroOuNegativos() {
        // Tem 150 pontos (passa da verificação de ter mais de 100), mas tenta fazer um
        // ataque.
        // Falha na Branch: pontosParaResgate <= 0 (A trava de segurança que você
        // implementou!)
        FidelidadeService service = new FidelidadeService(150);

        assertThrows(IllegalArgumentException.class, () -> {
            service.resgatar(0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            service.resgatar(-50);
        });
    }

    // --- COBERTURA DO ENCAPSULAMENTO DE RESGATE (podeResgatar) ---

    @Test
    public void deveRetornarTrue_QuandoTiverPontosSuficientesParaResgatar() {
        FidelidadeService serviceExato = new FidelidadeService(100);
        assertTrue(serviceExato.podeResgatar()); // Branch: >= 100

        FidelidadeService serviceSobra = new FidelidadeService(101);
        assertTrue(serviceSobra.podeResgatar());
    }

    @Test
    public void deveRetornarFalse_QuandoNaoTiverPontosSuficientesParaResgatar() {
        FidelidadeService service = new FidelidadeService(99);
        assertFalse(service.podeResgatar()); // Branch: < 100
    }
}