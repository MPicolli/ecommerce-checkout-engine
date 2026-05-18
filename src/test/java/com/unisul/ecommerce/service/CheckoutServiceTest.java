package com.unisul.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.unisul.ecommerce.exception.CarrinhoVazioException;
import com.unisul.ecommerce.model.Carrinho;
import com.unisul.ecommerce.model.Cliente;
import com.unisul.ecommerce.model.Cupom;
import com.unisul.ecommerce.model.Produto;
import com.unisul.ecommerce.model.ResumoPedido;
import com.unisul.ecommerce.model.TipoCupom;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("CheckoutService - Testes")
class CheckoutServiceTest {

    @Mock
    private CupomService cupomServiceMock;

    @Mock
    private FreteService freteServiceMock;

    @Mock
    private FidelidadeService fidelidadeServiceMock;

    private CheckoutService checkoutService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checkoutService = new CheckoutService(
                cupomServiceMock,
                freteServiceMock,
                fidelidadeServiceMock);
    }

    @Test
    @DisplayName("Deve finalizar pedido com sucesso sem cupom")
    void testFinalizarPedidoSemCupom() {
        // Arrange
        Cliente cliente = new Cliente(1L, "João Silva", "88000000", 0);
        Carrinho carrinho = new Carrinho(cliente);

        Produto produto = new Produto(1L, "Notebook", new BigDecimal("2500.00"), new BigDecimal("2.5"));
        carrinho.adicionarItem(produto, 1);

        // Simular frete de R$ 15,00
        when(freteServiceMock.calcularFrete(carrinho, "88000000"))
                .thenReturn(new BigDecimal("15.00"));

        // Act
        ResumoPedido resumo = checkoutService.finalizarPedido(carrinho);

        // Assert
        assertNotNull(resumo);
        assertEquals(new BigDecimal("2500.00"), resumo.getSubtotal());
        assertEquals(new BigDecimal("0.00"), resumo.getValorDescontos());
        assertEquals(new BigDecimal("15.00"), resumo.getValorFrete());
        assertEquals(new BigDecimal("2515.00"), resumo.getTotalFinal());
        assertEquals(251, resumo.getPontosGanhos()); // 2515 / 10 = 251

        // Verificar que o método de frete foi chamado
        verify(freteServiceMock, times(1)).calcularFrete(carrinho, "88000000");
        verify(fidelidadeServiceMock, times(1)).acumularPontos(new BigDecimal("2515.00"));
    }

    @Test
    @DisplayName("Deve aplicar cupom percentual corretamente")
    void testFinalizarPedidoComCupomPercentual() {
        // Arrange
        Cliente cliente = new Cliente(1L, "Maria Santos", "01000000", 0);
        Carrinho carrinho = new Carrinho(cliente);

        Produto produto = new Produto(1L, "Mouse", new BigDecimal("100.00"), new BigDecimal("0.5"));
        carrinho.adicionarItem(produto, 2); // Total: R$ 200,00

        Cupom cupom = new Cupom("DESC10", new BigDecimal("10"), BigDecimal.ZERO, TipoCupom.PERCENTUAL);
        carrinho.setCupomAplicado(cupom);

        // Cupom de 10% em R$ 200 = R$ 20 de desconto, total R$ 180
        when(cupomServiceMock.aplicarCupom(new BigDecimal("200.00"), cupom))
                .thenReturn(new BigDecimal("180.00"));

        // Frete grátis para compras acima de R$ 200 (antes do desconto)
        when(freteServiceMock.calcularFrete(carrinho, "01000000"))
                .thenReturn(new BigDecimal("0.00"));

        // Act
        ResumoPedido resumo = checkoutService.finalizarPedido(carrinho);

        // Assert
        assertEquals(new BigDecimal("200.00"), resumo.getSubtotal());
        assertEquals(new BigDecimal("20.00"), resumo.getValorDescontos());
        assertEquals(new BigDecimal("0.00"), resumo.getValorFrete());
        assertEquals(new BigDecimal("180.00"), resumo.getTotalFinal());
        assertEquals(18, resumo.getPontosGanhos()); // 180 / 10 = 18

        verify(cupomServiceMock, times(1)).aplicarCupom(new BigDecimal("200.00"), cupom);
        verify(freteServiceMock, times(1)).calcularFrete(carrinho, "01000000");
    }

    @Test
    @DisplayName("Deve aplicar cupom fixo corretamente")
    void testFinalizarPedidoComCupomFixo() {
        // Arrange
        Cliente cliente = new Cliente(1L, "Pedro Costa", "90000000", 0);
        Carrinho carrinho = new Carrinho(cliente);

        Produto produto = new Produto(1L, "Teclado", new BigDecimal("150.00"), new BigDecimal("0.8"));
        carrinho.adicionarItem(produto, 1);

        Cupom cupom = new Cupom("DESC25", new BigDecimal("25.00"), BigDecimal.ZERO, TipoCupom.FIXO);
        carrinho.setCupomAplicado(cupom);

        // Cupom fixo de R$ 25, total R$ 125
        when(cupomServiceMock.aplicarCupom(new BigDecimal("150.00"), cupom))
                .thenReturn(new BigDecimal("125.00"));

        // Frete de R$ 30,00
        when(freteServiceMock.calcularFrete(carrinho, "90000000"))
                .thenReturn(new BigDecimal("30.00"));

        // Act
        ResumoPedido resumo = checkoutService.finalizarPedido(carrinho);

        // Assert
        assertEquals(new BigDecimal("150.00"), resumo.getSubtotal());
        assertEquals(new BigDecimal("25.00"), resumo.getValorDescontos());
        assertEquals(new BigDecimal("30.00"), resumo.getValorFrete());
        assertEquals(new BigDecimal("155.00"), resumo.getTotalFinal());
        assertEquals(15, resumo.getPontosGanhos()); // 155 / 10 = 15

        verify(cupomServiceMock, times(1)).aplicarCupom(new BigDecimal("150.00"), cupom);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar finalizar pedido com carrinho vazio")
    void testFinalizarPedidoComCarrinhoVazio() {
        // Arrange
        Carrinho carrinho = new Carrinho();

        // Act & Assert
        assertThrows(CarrinhoVazioException.class, () -> checkoutService.finalizarPedido(carrinho));
        verify(cupomServiceMock, never()).aplicarCupom(any(), any());
        verify(freteServiceMock, never()).calcularFrete(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção se cliente ou CEP não estiver preenchido")
    void testFinalizarPedidoSemClienteOuCep() {
        // Arrange
        Carrinho carrinho = new Carrinho();
        Produto produto = new Produto(1L, "Produto", new BigDecimal("100.00"), new BigDecimal("1.0"));
        carrinho.adicionarItem(produto, 1);
        carrinho.setCliente(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> checkoutService.finalizarPedido(carrinho));
    }

    @Test
    @DisplayName("Deve finalizar pedido com uso de pontos de fidelidade")
    void testFinalizarPedidoComPontos() {
        // Arrange
        Cliente cliente = new Cliente(1L, "Ana Silva", "88000000", 500);
        Carrinho carrinho = new Carrinho(cliente);

        Produto produto = new Produto(1L, "Monitor", new BigDecimal("600.00"), new BigDecimal("3.0"));
        carrinho.adicionarItem(produto, 1);

        when(freteServiceMock.calcularFrete(carrinho, "88000000"))
                .thenReturn(new BigDecimal("20.00"));

        // 100 pontos = R$ 10,00 de desconto
        int pontosAUsar = 100;

        // Act
        ResumoPedido resumo = checkoutService.finalizarPedidoComPontos(carrinho, pontosAUsar);

        // Assert
        assertEquals(new BigDecimal("600.00"), resumo.getSubtotal());
        assertEquals(new BigDecimal("10.00"), resumo.getValorDescontos()); // 100 pontos * 0.10
        assertEquals(new BigDecimal("610.00"), resumo.getTotalFinal()); // 600 + 20 - 10
        assertEquals(61, resumo.getPontosGanhos()); // 610 / 10 = 61

        verify(fidelidadeServiceMock, times(1)).resgatar(pontosAUsar);
        verify(fidelidadeServiceMock, times(1)).acumularPontos(new BigDecimal("610.00"));
    }

    @Test
    @DisplayName("Deve lançar exceção se não houver pontos suficientes para resgate")
    void testFinalizarPedidoComPontosInsuficientes() {
        // Arrange
        Cliente cliente = new Cliente(1L, "Lucas", "88000000", 50);
        Carrinho carrinho = new Carrinho(cliente);

        Produto produto = new Produto(1L, "Headset", new BigDecimal("300.00"), new BigDecimal("0.5"));
        carrinho.adicionarItem(produto, 1);

        when(freteServiceMock.calcularFrete(carrinho, "88000000"))
                .thenReturn(new BigDecimal("15.00"));

        // Simular exceção ao tentar resgatar
        doThrow(new IllegalStateException("Pontos insuficientes para resgate"))
                .when(fidelidadeServiceMock).resgatar(150);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> checkoutService.finalizarPedidoComPontos(carrinho, 150));
    }

    @Test
    @DisplayName("Deve calcular frete integrado corretamente")
    void testCalculoFreteIntegrado() {
        // Arrange
        Cliente cliente = new Cliente(1L, "Carlos", "02000000", 0);
        Carrinho carrinho = new Carrinho(cliente);

        Produto produto = new Produto(1L, "Produto Pesado", new BigDecimal("500.00"), new BigDecimal("5.0"));
        carrinho.adicionarItem(produto, 1);

        // Frete: R$ 15 (SP) + (5kg * R$ 2) = R$ 25
        when(freteServiceMock.calcularFrete(carrinho, "02000000"))
                .thenReturn(new BigDecimal("25.00"));

        // Act
        ResumoPedido resumo = checkoutService.finalizarPedido(carrinho);

        // Assert
        assertEquals(new BigDecimal("525.00"), resumo.getTotalFinal()); // 500 + 25
        verify(freteServiceMock, times(1)).calcularFrete(carrinho, "02000000");
    }

    @Test
    @DisplayName("Deve gerar resumo com múltiplos itens no carrinho")
    void testFinalizarPedidoComMultiplosItens() {
        // Arrange
        Cliente cliente = new Cliente(1L, "Fernanda", "88000000", 0);
        Carrinho carrinho = new Carrinho(cliente);

        Produto produto1 = new Produto(1L, "Notebook", new BigDecimal("2000.00"), new BigDecimal("2.0"));
        Produto produto2 = new Produto(2L, "Mouse", new BigDecimal("50.00"), new BigDecimal("0.1"));

        carrinho.adicionarItem(produto1, 1); // R$ 2000
        carrinho.adicionarItem(produto2, 2); // R$ 100

        // Total: R$ 2100 (frete grátis)
        when(freteServiceMock.calcularFrete(carrinho, "88000000"))
                .thenReturn(BigDecimal.ZERO);

        // Act
        ResumoPedido resumo = checkoutService.finalizarPedido(carrinho);

        // Assert
        assertEquals(new BigDecimal("2100.00"), resumo.getSubtotal());
        assertEquals(new BigDecimal("0.00"), resumo.getValorFrete());
        assertEquals(new BigDecimal("2100.00"), resumo.getTotalFinal());
        assertEquals(210, resumo.getPontosGanhos());
    }
}
