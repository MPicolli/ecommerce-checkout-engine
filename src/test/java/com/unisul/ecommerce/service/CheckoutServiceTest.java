package com.unisul.ecommerce.service;

import com.unisul.ecommerce.exception.CarrinhoVazioException;
import com.unisul.ecommerce.exception.CupomInvalidoException;
import com.unisul.ecommerce.model.Carrinho;
import com.unisul.ecommerce.model.Cliente;
import com.unisul.ecommerce.model.Cupom;
import com.unisul.ecommerce.model.ItemCarrinho;
import com.unisul.ecommerce.model.Produto;
import com.unisul.ecommerce.model.ResumoPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckoutServiceTest {

        private CupomService cupomService;
        private FreteService freteService;
        private FidelidadeService fidelidadeService;
        private CheckoutService checkoutService;

        private Carrinho carrinhoValido;
        private Cliente clienteValido;

        @BeforeEach
        public void setUp() {
                // 1. Inicializando os Mocks (Dublês)
                cupomService = mock(CupomService.class);
                freteService = mock(FreteService.class);
                fidelidadeService = mock(FidelidadeService.class);

                // 2. Injetando os mocks no serviço real
                checkoutService = new CheckoutService(cupomService, freteService, fidelidadeService);

                // 3. Criando um carrinho com dados básicos para passar na validação inicial
                clienteValido = new Cliente();
                clienteValido.setCep("88000-000");
                clienteValido.setSaldoPontos(100);

                carrinhoValido = new Carrinho();
                carrinhoValido.setCliente(clienteValido);

                Produto produto = new Produto();
                produto.setPreco(new BigDecimal("100.00")); // Valor Base do Carrinho

                carrinhoValido.setItens(new ArrayList<>());
                carrinhoValido.getItens().add(new ItemCarrinho(produto, 1));
        }

        // --- TESTES DE VALIDAÇÃO (EXCEÇÕES DA LINHA 136 E 140) ---

        @Test
        public void deveLancarExcecao_QuandoCarrinhoForNulo() {
                assertThrows(CarrinhoVazioException.class, () -> {
                        checkoutService.finalizarPedido(null);
                });
        }

        @Test
        public void deveLancarExcecao_QuandoListaDeItensForNula() {
                carrinhoValido.setItens(null);
                assertThrows(CarrinhoVazioException.class, () -> {
                        checkoutService.finalizarPedido(carrinhoValido);
                });
        }

        @Test
        public void deveLancarExcecao_QuandoListaDeItensEstiverVazia() {
                carrinhoValido.setItens(new ArrayList<>()); // Lista vazia
                assertThrows(CarrinhoVazioException.class, () -> {
                        checkoutService.finalizarPedido(carrinhoValido);
                });
        }

        @Test
        public void deveLancarExcecao_QuandoClienteForNulo() {
                carrinhoValido.setCliente(null);
                assertThrows(IllegalArgumentException.class, () -> {
                        checkoutService.finalizarPedido(carrinhoValido);
                });
        }

        @Test
        public void deveLancarExcecao_QuandoCepDoClienteForNulo() {
                clienteValido.setCep(null);
                assertThrows(IllegalArgumentException.class, () -> {
                        checkoutService.finalizarPedido(carrinhoValido);
                });
        }

        // --- TESTES DE SUCESSO: finalizarPedido ---

        @Test
        public void deveFinalizarPedido_QuandoNaoHouverCupomAplicado() {
                // Cenário: Compra de 100, sem cupom, frete mockado em 15. Total = 115.
                carrinhoValido.setCupomAplicado(null);
                when(freteService.calcularFrete(any(), any())).thenReturn(new BigDecimal("15.00"));

                ResumoPedido resumo = checkoutService.finalizarPedido(carrinhoValido);

                assertEquals(0, new BigDecimal("0.00").compareTo(resumo.getValorDescontos()));
                assertEquals(0, new BigDecimal("115.00").compareTo(resumo.getTotalFinal()));
        }

        @Test
        public void deveFinalizarPedidoComPontos_QuandoHouverCupomAplicado() throws CupomInvalidoException {
                // Cenário: Compra de 100, cupom de 10, frete de 15, usando 50 pontos (R$ 5).
                Cupom cupom = new Cupom();
                carrinhoValido.setCupomAplicado(cupom); // Isso faz o if da linha 84 ser verdadeiro

                when(cupomService.aplicarCupom(any(), eq(cupom))).thenReturn(new BigDecimal("90.00")); // Valor com
                                                                                                       // desconto
                when(freteService.calcularFrete(any(), any())).thenReturn(new BigDecimal("15.00"));

                ResumoPedido resumo = checkoutService.finalizarPedidoComPontos(carrinhoValido, 50);

                // Desconto do cupom (100 - 90 = 10) + Desconto dos pontos (5) = 15 de desconto
                // total.
                assertEquals(0, new BigDecimal("15.00").compareTo(resumo.getValorDescontos()));
        }

        @Test
        public void deveFinalizarPedido_QuandoHouverCupomAplicado() throws CupomInvalidoException {
                // Cenário: Compra de 100, cupom reduz valor para 90, frete de 15. Total = 105.
                Cupom cupom = new Cupom();
                carrinhoValido.setCupomAplicado(cupom);

                when(cupomService.aplicarCupom(any(), eq(cupom))).thenReturn(new BigDecimal("90.00"));
                when(freteService.calcularFrete(any(), any())).thenReturn(new BigDecimal("15.00"));

                ResumoPedido resumo = checkoutService.finalizarPedido(carrinhoValido);

                assertEquals(0, new BigDecimal("10.00").compareTo(resumo.getValorDescontos())); // Desconto de 10
                assertEquals(0, new BigDecimal("105.00").compareTo(resumo.getTotalFinal()));
        }

        // --- TESTES DE SUCESSO: finalizarPedidoComPontos ---

        @Test
        public void deveFinalizarPedidoComPontos_QuandoPontosAUsarForZero() {
                // Cenário: Fluxo de pontos chamado, mas usuário usou 0 pontos.
                carrinhoValido.setCupomAplicado(null);
                when(freteService.calcularFrete(any(), any())).thenReturn(new BigDecimal("15.00"));

                ResumoPedido resumo = checkoutService.finalizarPedidoComPontos(carrinhoValido, 0);

                assertEquals(0, new BigDecimal("0.00").compareTo(resumo.getValorDescontos()));
                assertEquals(0, new BigDecimal("115.00").compareTo(resumo.getTotalFinal()));
        }

        @Test
        public void deveFinalizarPedidoComPontos_QuandoPontosAUsarForMaiorQueZero() {
                // Cenário: Compra 100, frete 15. Usa 50 pontos (R$ 5,00 de desconto). Total =
                // 110.
                carrinhoValido.setCupomAplicado(null);
                when(freteService.calcularFrete(any(), any())).thenReturn(new BigDecimal("15.00"));

                ResumoPedido resumo = checkoutService.finalizarPedidoComPontos(carrinhoValido, 50);

                assertEquals(0, new BigDecimal("5.00").compareTo(resumo.getValorDescontos()));
                assertEquals(0, new BigDecimal("110.00").compareTo(resumo.getTotalFinal()));
        }

        @Test
        public void deveZerarTotal_QuandoDescontoDosPontosForMaiorQueOTotalDaCompra() {
                // Cenário extremo: Compra 100, frete 0. Usa 2000 pontos (R$ 200,00 de
                // desconto).
                // A lógica do sistema obriga a conta a não ficar negativa, cravando em ZERO.
                carrinhoValido.setCupomAplicado(null);
                when(freteService.calcularFrete(any(), any())).thenReturn(BigDecimal.ZERO);

                ResumoPedido resumo = checkoutService.finalizarPedidoComPontos(carrinhoValido, 2000);

                assertEquals(0, BigDecimal.ZERO.compareTo(resumo.getTotalFinal()));
        }
}