package com.unisul.ecommerce.service;

import com.unisul.ecommerce.exception.CarrinhoVazioException;
import com.unisul.ecommerce.exception.CupomInvalidoException;
import com.unisul.ecommerce.model.Carrinho;
import com.unisul.ecommerce.model.Cliente;
import com.unisul.ecommerce.model.ResumoPedido;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CheckoutService {

    private final CupomService cupomService;
    private final FreteService freteService;
    private final FidelidadeService fidelidadeService;

    public CheckoutService(CupomService cupomService, FreteService freteService, FidelidadeService fidelidadeService) {
        this.cupomService = cupomService;
        this.freteService = freteService;
        this.fidelidadeService = fidelidadeService;
    }

    public ResumoPedido finalizarPedido(Carrinho carrinho) throws CarrinhoVazioException, CupomInvalidoException {
        validarCarrinho(carrinho);

        BigDecimal subtotal = carrinho.getValorTotal();
        BigDecimal valorComDesconto = subtotal;

        BigDecimal valorDescontos = new BigDecimal("0.00");

        if (carrinho.getCupomAplicado() != null) {
            valorComDesconto = cupomService.aplicarCupom(subtotal, carrinho.getCupomAplicado());
            valorDescontos = subtotal.subtract(valorComDesconto);
        }

        BigDecimal valorFrete = freteService.calcularFrete(carrinho, carrinho.getCliente().getCep());
        BigDecimal totalFinal = valorComDesconto.add(valorFrete);

        int pontosGanhos = calcularPontosGanhos(totalFinal);
        fidelidadeService.acumularPontos(totalFinal);

        Cliente cliente = carrinho.getCliente();

        int novosPontos = cliente.getSaldoPontos() + pontosGanhos;
        cliente.setSaldoPontos(novosPontos);

        return new ResumoPedido(
                carrinho,
                subtotal.setScale(2, RoundingMode.HALF_UP),
                valorDescontos.setScale(2, RoundingMode.HALF_UP),
                valorFrete.setScale(2, RoundingMode.HALF_UP),
                pontosGanhos,
                totalFinal.setScale(2, RoundingMode.HALF_UP));
    }

    public ResumoPedido finalizarPedidoComPontos(Carrinho carrinho, int pontosAUsar)
            throws CarrinhoVazioException, CupomInvalidoException {

        validarCarrinho(carrinho);

        BigDecimal subtotal = carrinho.getValorTotal();
        BigDecimal valorComDesconto = subtotal;
        BigDecimal valorDescontos = new BigDecimal("0.00");

        if (carrinho.getCupomAplicado() != null) {
            valorComDesconto = cupomService.aplicarCupom(subtotal, carrinho.getCupomAplicado());
            valorDescontos = subtotal.subtract(valorComDesconto);
        }

        BigDecimal valorFrete = freteService.calcularFrete(carrinho, carrinho.getCliente().getCep());
        BigDecimal totalFinal = valorComDesconto.add(valorFrete);

        Cliente cliente = carrinho.getCliente();

        if (pontosAUsar > 0) {
            fidelidadeService.resgatar(pontosAUsar);

            BigDecimal descontoPontos = BigDecimal.valueOf(pontosAUsar).multiply(new BigDecimal("0.10"));

            totalFinal = totalFinal.subtract(descontoPontos);
            if (totalFinal.compareTo(BigDecimal.ZERO) < 0) {
                totalFinal = BigDecimal.ZERO;
            }

            valorDescontos = valorDescontos.add(descontoPontos);

            int novos = cliente.getSaldoPontos() - pontosAUsar;
            cliente.setSaldoPontos(Math.max(0, novos));

        }

        int pontosGanhos = calcularPontosGanhos(totalFinal);
        fidelidadeService.acumularPontos(totalFinal);

        int novosPontos = cliente.getSaldoPontos() + pontosGanhos;
        cliente.setSaldoPontos(novosPontos);

        return new ResumoPedido(
                carrinho,
                subtotal.setScale(2, RoundingMode.HALF_UP),
                valorDescontos.setScale(2, RoundingMode.HALF_UP),
                valorFrete.setScale(2, RoundingMode.HALF_UP),
                pontosGanhos,
                totalFinal.setScale(2, RoundingMode.HALF_UP));
    }

    private void validarCarrinho(Carrinho carrinho) throws CarrinhoVazioException {
        if (carrinho == null || carrinho.getItens() == null || carrinho.getItens().isEmpty()) {
            throw new CarrinhoVazioException();
        }

        if (carrinho.getCliente() == null || carrinho.getCliente().getCep() == null) {
            throw new IllegalArgumentException("Cliente e CEP são obrigatórios para finalizar o pedido.");
        }
    }

    private int calcularPontosGanhos(BigDecimal valorTotal) {
        return valorTotal.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
    }
}